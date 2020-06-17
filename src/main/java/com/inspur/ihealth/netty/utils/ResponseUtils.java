package com.inspur.ihealth.netty.utils;

/**
 * 应答工具类
 */
public class ResponseUtils {


    /**
     * 服务器身份应答(普通应答)
     * @param gsmVersion  设备版本GSM
     * @param responseType  回复类别
     * @param length  长度
     * @return 应答串（16进制）
     */
    public static String response(String gsmVersion,String length,String responseType,String responseData) {

        StringBuilder sb = new StringBuilder();
        sb.append("aa80");
        sb.append(gsmVersion);
        sb.append(length);
        sb.append(responseType);
        sb.append(responseData); //成功00 , 出错01
        sb.append(XorUtils.xor(gsmVersion+length+responseType+responseData));

        return sb.toString().trim();


    }
}
