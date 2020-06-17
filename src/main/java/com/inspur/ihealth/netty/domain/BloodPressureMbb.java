package com.inspur.ihealth.netty.domain;

import lombok.Data;

/**
 * 脉搏波血压计中间类
 */
@Data
public class BloodPressureMbb {

    /**
     * 设备版本GSM
     */
    private String gsmVersion;

    /**
     * 协议版本号
     */
    private String protocolVersion;

    /**
     * 软件版本号
     */
    private String softVersion;

    /**
     * 设备公司编码
     */
    private String companySN;

    /**
     * 设备型号
     */
    private String deviceType;

    /**
     * 设备编码
     */
    private String deviceSn;

    /**
     * SIM卡号码
     */
    private String simNo;

    /**
     * 用户标识  01: A;  02:B
     */
    private String userFlag;

    /**
     * 时间戳
     */
    private String measureTime;

    /**
     * 收缩压
     */
    private Integer highPress;
    /**
     * 舒张压
     */
    private Integer lowPress;

    /**
     * 脉搏
     */
    private Integer heartRate;

}
