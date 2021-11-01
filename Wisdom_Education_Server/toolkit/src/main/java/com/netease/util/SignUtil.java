package com.netease.util;

import org.apache.commons.codec.digest.DigestUtils;

import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignUtil {

    /**
     * 按照自然序拼接输入的非空参数
     *
     * @return 待签名用的source
     */
    public static String source(String... params) {
        if (params == null) {
            throw new IllegalArgumentException("params required");
        }
        List<String> validParams = new ArrayList<String>();
        for (String param : params) {
            if (param != null) {
                validParams.add(param);
            }
        }
        String[] validParamArr = validParams.toArray(new String[0]);
        Arrays.sort(validParamArr);// natual order
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < validParamArr.length; i++) {
            sb.append(validParamArr[i]);
        }
        return sb.toString();
    }

    /**
     * 对source进行sha1签名
     */
    public static final String sha1Hex(String source) {
        return DigestUtils.sha1Hex(source);
    }

    /**
     * 对source进行sha256签名
     */
    public static final String sha256Hex(String source) {
        return DigestUtils.sha256Hex(source);
    }


    /**
     * @param secretKey - 开发人员提供
     * @param uid       - http header [E-UID]
     * @param bodymd5   - body md5
     * @param nonce     - http header[E-NONCE]
     * @param timestamp - http header[E-TIMESTAMP]
     * @param params    - http request参数列表
     * @return sign - verify with http header[E-CHECKSUM]
     */
    public static final String source(String secretKey, String uid, String bodymd5, String nonce, String timestamp, String version, String... params) {

        List<String> allParams = new ArrayList<String>();
        allParams.add(uid);
        allParams.add(secretKey);
        if (bodymd5 != null) {
            allParams.add(bodymd5);
        }
        allParams.add(nonce);
        allParams.add(timestamp);
        if (version != null) {
            allParams.add(version);
        }
        if (params != null) {
            allParams.addAll(Arrays.asList(params));
        }
        return SignUtil.source(allParams.toArray(new String[0]));
    }

    public static void main(String args[]) {
        String domain = "asdf.jingege.me";
        String[] cnameParam = {"CNAME"};
        String[] mxParam = {"MX"};
        try {
            String[] list = WebUtils.lookupHosts(domain, cnameParam);
            for (int i = 0; i < list.length; i++) {
                System.out.println(i + ":" + list[i]);
            }
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
