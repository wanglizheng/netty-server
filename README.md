# netty-server

```
1、设备连接收到血压计身份数据包，如下：
--血压计身份数据包 样例
server received: cc8005010100170101026b06b3130b02014f0e28140a5822011406110a05d5
cc80 --前导码
05   --设备版本GSM
01   --协议版本号
01	--软件版本号
0017 --长度(2byte)
01	--数据类型(1byte)
01	--设备公司编码(1byte)
02	--设备类型(1byte)
6b	--设备型号(1byte)
06b3130b02014f	--设备编码(7byte)
0e28140a582201	--设备公司编码(1byte)
1406110a05	--时间戳(年月日时分)
d5	--校验码


2、发送(1.3)服务器身份应答(普通应答) 获取血压数据，发送举例： aa800502010006  设备版本GSM（05）的值从步骤1获取。
aa800502010006
aa800502010006
aa800502010007

3、收到血压数据：
--血压数据样例  a-10 b-11 c-12 d-13
server received:  cc80050101001d0201026b06b3130b02014f0e28140a582201021406110a050078004b49a4

cc80 --前导码       0~4
05	--设备版本GSM   4~6
01	--协议版本号    6~8
01	--软件版本号    8~10
001d  --长度(2byte) 16+13 =29   10~14
02	--数据类型(1byte)        14~16
01	--设备公司编码(1byte)    16~18
02  --设备类型(1byte) 血压计  18~20
6b  --设备型号(1byte)         20~22
06b3130b02014f  --设备编码(7byte)   22~36
0e28140a582201  --SIM卡号码(7byte)  36~50
02	--用户标识  50~52
1406110a05 --时间戳(年月日时分)   52~62
0078 --16*7+8 = 120  收缩压  62~66
004b --16*4+11 = 75  舒张压  66~70
49   --16*4+9 = 73   脉搏    70~72
a4	--校验   72~74

4、获取用户测量发送失败的设备记忆数据，(2.1)服务器应答当次数据  aa80050200XX  设备版本GSM（05）的值从步骤1获取。

5、记忆数据格式同步骤3，业务逻辑处理入库即可

6、收到记忆数据发送应答： (3.1)服务器应答记忆数据 ，清除设备上的记忆数据，避免重复发送。aa80050300XX  设备版本GSM（05）的值从步骤1获取。

--记忆数据
cc80
05
01
01
0078  长度(2byte) 数据条数*10+20  16*7+8 = 120（10条）
03   --记忆数据
01	--公司编码
02	--设备类型
6b	--设备型号
06b3130b02014f	设备编码
0e28140a582201	sim卡号
02	--用户编码
0a14061111000072003a3f1406111102006300285f140611110c0076002a62140611111d0073004c3f1406101139008100563f140610113b008100594014061012020086005942140611093a006e0033461406110a050078004b49140611103900740053401f

```
