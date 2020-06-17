package com.inspur.ihealth.netty.utils;

/**
 * 按位异或工具类
 */
public class XorUtils {

    public static String xor(String content) {
        content = change(content);
        String[] b = content.split(" ");
        //0异或任何数＝任何数;1异或任何数－任何数取反
        int a = 0;
        for (int i = 0; i < b.length; i++) {
            a = a ^ Integer.parseInt(b[i], 16);
        }
        if(a<10){
            StringBuffer sb = new StringBuffer();
            sb.append("0");
            sb.append(a);
            return sb.toString();
        }
        return Integer.toHexString(a);
    }

    public static String change(String content) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            if (i % 2 == 0) {
                sb.append(" " + content.substring(i, i + 1));
            } else {
                sb.append(content.substring(i, i + 1));
            }
        }
        return sb.toString().trim();
    }

}
