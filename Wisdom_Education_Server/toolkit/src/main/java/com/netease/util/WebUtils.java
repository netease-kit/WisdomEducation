package com.netease.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class WebUtils {
    public static Logger logger = LoggerFactory.getLogger(WebUtils.class);

    /**
     * 将字符串加密成md5串
     *
     * @param str
     * @return by jinshan Dec 17, 2009
     */
    public static String MD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            byte[] messageDigest = md.digest(str.getBytes());
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String tmp = Integer.toHexString(0xFF & messageDigest[i]);
                if (tmp.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(tmp);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取访问者的ip地址
     *
     * @param request
     * @return by jinshan Nov 11, 2009
     */
    public static String getRequestIp(HttpServletRequest request) {
        try {
            String ip;
            String rip = request.getRemoteAddr();
            String xff = request.getHeader("X-Forwarded-For");
            if (xff != null && xff.length() != 0) {
                int px = xff.indexOf(',');
                if (px != -1) {
                    ip = xff.substring(0, px);
                } else {
                    ip = xff;
                }
            } else {
                ip = rip;
            }
            ip = ip.trim();
            return ip;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * 生成随即密码
     *
     * @param pwd_len 生成的密码的总长度
     * @return 密码的字符串
     */
    public static String genRandomPwd(int pwd_len) {
        // 35是因为数组是从0开始的，26个字母+10个数字
        final int maxNum = 36;
        int i; // 生成的随机数
        int count = 0; // 生成的密码的长度
        char[] str = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
                'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        StringBuffer pwd = new StringBuffer("");
        Random r = new Random();
        while (count < pwd_len) {
            // 生成随机数，取绝对值，防止生成负数，

            i = Math.abs(r.nextInt(maxNum)); // 生成的数最大为36-1

            if (i >= 0 && i < str.length) {
                pwd.append(str[i]);
                count++;
            }
        }

        return pwd.toString();
    }

    /**
     * @param domainName 域名
     * @param param      要查询的类型，比如MX，A，CNAME记录
     * @return 返回的记录。包含对象是string[]，表示DNS资源
     * @throws javax.naming.NamingException
     * @方法名 lookupHosts
     * @功能 查找DNS资源记录
     */
    public static String[] lookupHosts(String domainName, String[] param)
            throws NamingException {
        // see: RFC 974 - Mail routing and the domain system
        // see: RFC 1034 - Domain names - concepts and facilities
        // see: http://java.sun.com/j2se/1.5.0/docs/guide/jndi/jndi-dns.html
        // - DNS Service Provider for the Java Naming Directory Interface (JNDI)
        if (param == null || param.length != 1)
            return null;
        // get the default initial Directory Context
        InitialDirContext iDirC = new InitialDirContext();
        // NamingException thrown if no DNS record found for domainName
        Attributes attributes = iDirC
                .getAttributes("dns:/" + domainName, param);

        Attribute attributeDns = attributes.get(param[0]);
        if (attributeDns == null || attributeDns.size() <= 0) {
            return (new String[]{});
        }
        // dns资源记录返回
        String[][] pvhn = new String[attributeDns.size()][2];
        for (int i = 0; i < attributeDns.size(); i++) {
            String rr = "" + attributeDns.get(i);
            if (rr.endsWith("."))
                rr = rr.substring(0, rr.length() - 1);
            pvhn[i] = rr.split("\\s+", 2);
        }
        iDirC.close();
        // 分割相关的数据
        String[] sortedHostNames = new String[pvhn.length];
        for (int i = 0; i < pvhn.length; i++) {
            if (pvhn[i].length <= 1)
                sortedHostNames[i] = pvhn[i][0];
            else
                sortedHostNames[i] = pvhn[i][1];
        }
        return sortedHostNames;
    }

}
