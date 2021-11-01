package com.netease.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * IP 工具类
 *
 * @author shenxiangyu on 2020/12/01
 */
public class IPUtil {

    /**
     * 获取客户端真实 IP
     *
     * @param request 请求
     * @return 真实 IP
     */
    public static String getRealClientIpAddr(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(ipAddress) && !"unknown".equalsIgnoreCase(ipAddress)) {
            return ipAddress;
        }

        ipAddress = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ipAddress) && !"unknown".equalsIgnoreCase(ipAddress)) {
            int index = ipAddress.indexOf(',');
            if (index != -1) {
                return ipAddress.substring(0, index);
            } else {
                return ipAddress;
            }
        } else {
            return request.getRemoteAddr();
        }
    }
}
