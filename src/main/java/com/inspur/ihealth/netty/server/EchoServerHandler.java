package com.inspur.ihealth.netty.server;

import com.inspur.ihealth.netty.common.ConvertCode;
import com.inspur.ihealth.netty.domain.BloodPressureMbb;
import com.inspur.ihealth.netty.utils.ResponseUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/***
 * 服务端自定义业务处理handler
 */
@Slf4j
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 对每一个传入的消息都要调用；
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        try {
            ByteBuf buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            //复制内容到字节数组bytes
            buf.readBytes(bytes);
            //将接收到的数据转为字符串，此字符串就是客户端发送的字符串
            String receiveStr = ConvertCode.receiveHexToString(bytes);
            //log.info("server:{} , received: ", ctx.channel().remoteAddress(), receiveStr);
            log.info("server received: {}", receiveStr);

            //解析收到血压计身份数据包的16进制串，拿到设备的版本，应答获取血压数据包
            String preString = receiveStr.substring(0, 4); //前导符
            String gsmVersion = "05";
            String dataType = "01";


            if ("cc80".equals(preString) || "CC80".equals(preString)) {
                gsmVersion = receiveStr.substring(4, 6); //GSM 版本

                /**
                 * 数据包类型
                 *  01：血压计发送身份数据帧
                 *  02：血压计发送当次测量数据
                 *  03：血压计发送记忆中的数据
                 */
                dataType = receiveStr.substring(14, 16); //数据包类型

                //血压计发送身份数据帧 应答
                if ("01".equals(dataType)) {
                    writeToClient(ResponseUtils.response(gsmVersion, "02", "01", "00"), ctx, "普通应答");
                }
                //血压计发送当次测量数据 应答
                if ("02".equals(dataType)) {
                    BloodPressureMbb bloodPressureMbb = new BloodPressureMbb();
                    //cc800 50101001d0201026b06b3130b02014f0e28140a582201021406110a050078004b49a4
                    bloodPressureMbb.setGsmVersion(gsmVersion);
                    bloodPressureMbb.setProtocolVersion(receiveStr.substring(6,8));
                    bloodPressureMbb.setSoftVersion(receiveStr.substring(8,10));
                    bloodPressureMbb.setCompanySN(receiveStr.substring(16,18));
                    bloodPressureMbb.setDeviceType(receiveStr.substring(20,22));
                    //bloodPressureMbb.setDeviceSn(receiveStr.substring(22,36));
                    bloodPressureMbb.setDeviceSn(receiveStr.substring(22,26)
                            + Integer.parseInt(receiveStr.substring(26,28),16)
                            + Integer.parseInt(receiveStr.substring(28,30),16)
                            + Integer.parseInt(receiveStr.substring(30,32),16)
                            + Integer.parseInt(receiveStr.substring(32,36),16));
                    bloodPressureMbb.setSimNo(receiveStr.substring(36,50));
                    bloodPressureMbb.setUserFlag(receiveStr.substring(50,52));
                    bloodPressureMbb.setMeasureTime("20" + Integer.parseInt(receiveStr.substring(52,54),16)
                                + "-" + Integer.parseInt(receiveStr.substring(54,56),16)
                                + "-" + Integer.parseInt(receiveStr.substring(56,58),16)
                                + " " + Integer.parseInt(receiveStr.substring(58,60),16)
                                + ":" + Integer.parseInt(receiveStr.substring(60,62),16));
                    bloodPressureMbb.setHighPress(Integer.parseInt(receiveStr.substring(62,66),16));
                    bloodPressureMbb.setLowPress(Integer.parseInt(receiveStr.substring(66,70),16));
                    bloodPressureMbb.setHeartRate(Integer.parseInt(receiveStr.substring(70,72),16));
                    log.info(bloodPressureMbb.toString());
                    //处理完业务逻辑应答，如果有设备的记忆记录，会上报
                    writeToClient(ResponseUtils.response(gsmVersion, "02", "02", "00"), ctx, "应答当次数据");
                }
                //血压计发送记忆中的数据 应答
                if ("03".equals(dataType)) {
                    writeToClient(ResponseUtils.response(gsmVersion, "02", "03", "00"), ctx, "应答记忆数据");
                    ctx.close();
                }

            }


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            log.error("异常：", e);
        }


    }


    /**
     * 通知ChannelInboundHandler最后一次对channelRead()的调用时当前批量读取中的的最后一条消息。
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        ctx.flush();
    }

    /**
     * 在读取操作期间，有异常抛出时会调用。
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("channelActived>>>>>>>>>>>>>>");
    }

    /**
     * 公用回写数据到客户端的方法
     *
     * @param channel
     * @param mark    用于打印/log的输出
     *                <br>//channel.writeAndFlush(msg);//不行
     *                <br>//channel.writeAndFlush(receiveStr.getBytes());//不行
     *                <br>在netty里，进出的都是ByteBuf，楼主应确定服务端是否有对应的编码器，将字符串转化为ByteBuf
     */
    private void writeToClient(final String receiveStr, ChannelHandlerContext channel, final String mark) {
        try {
            ByteBuf bufff = Unpooled.buffer();//netty需要用ByteBuf传输
            bufff.writeBytes(ConvertCode.hexString2Bytes(receiveStr));//对接需要16进制
            channel.writeAndFlush(bufff).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    StringBuilder sb = new StringBuilder("");
                    if (!StringUtils.isEmpty(mark)) {
                        sb.append("【").append(mark).append("】");
                    }
                    if (future.isSuccess()) {
                        log.info(sb.toString() + "回写成功" + receiveStr);
                    } else {
                        log.error(sb.toString() + "回写失败" + receiveStr);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("调用通用writeToClient()异常：", e);
        }
    }
}
