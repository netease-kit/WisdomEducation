package com.netease.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class RequestUtil {
    private static final Logger logger = LoggerFactory.getLogger(RequestUtil.class);

    /**
     * get ip
     *
     * @param request
     * @param useInternalIp
     * @return
     */
    public static String getIp(HttpServletRequest request, boolean useInternalIp) {
        try {
            String header1 = request.getHeader("X-Forwarded-For");
            String header2 = request.getHeader("Proxy-Client-IP");
            String header3 = request.getHeader("WL-Proxy-Client-IP");

            String header = null;
            if (StringUtils.isNotBlank(header1) && !"unknown".equalsIgnoreCase(header1)) {
                header = header1;
            } else if (StringUtils.isNotBlank(header2) && !"unknown".equalsIgnoreCase(header2)) {
                header = header2;
            } else if (StringUtils.isNotBlank(header3) && !"unknown".equalsIgnoreCase(header3)) {
                header = header3;
            }

            String realIp = null;
            if (StringUtils.isNotBlank(header)) {
                String[] ips = header.split(",");
                for (String ip : ips) {
                    // 过滤2g/3g网关添加的内网ip
                    if (!RegexUtil.isInternalIp(ip)) {
                        realIp = ip;
                        break;
                    }
                }

                // 只有内网ip并且应用允许的情况下才取内网ip
                if (realIp == null && useInternalIp) {
                    realIp = ips[0];
                }
            }

            if (StringUtils.isNotBlank(realIp)) {
                return realIp.trim();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return request.getRemoteAddr();
    }

    /**
     * get ip (exclude internal ips)
     *
     * @param request
     * @return
     */
    public static String getIp(HttpServletRequest request) {
        return getIp(request, false);
    }

    /**
     * get port
     *
     * @param request
     * @return
     */
    public static int getPort(HttpServletRequest request) {
        return request.getRemotePort();
    }

    /**
     * get headers
     *
     * @param request
     * @return
     */
    public static Map<String, List<String>> getHeaders(HttpServletRequest request) {
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            ArrayList<String> value = Collections.list(request.getHeaders(key));
            headers.put(key, value);
        }

        return headers;
    }

    /**
     * get body
     *
     * @param request
     * @return
     * @throws Exception
     */
    public static byte[] getBody(HttpServletRequest request) throws Exception {
        int len = request.getContentLength();
        InputStream is = request.getInputStream();
        try {
            if (len == -1) {
                throw new IOException("read content length is -1!");
            }

            byte[] buffer = new byte[len];
            if (is != null) {
                IOUtils.readFully(is, buffer);
            }

            return buffer;
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
}
