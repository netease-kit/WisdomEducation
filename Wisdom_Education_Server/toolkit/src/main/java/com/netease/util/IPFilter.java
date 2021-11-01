package com.netease.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class IPFilter {

    private static final Logger logger = LoggerFactory.getLogger(IPFilter.class);

    private static final int IPSEC = 256;
    private static final int MASKDEF = 0xFFFFFFFF;

    private List<IPEntity> list = new ArrayList<>();

    private String name;

    private String urlMatchRegex;

    public IPFilter(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlMatchRegex() {
        return urlMatchRegex;
    }

    public void setUrlMatchRegex(String urlMatchRegex) {
        this.urlMatchRegex = urlMatchRegex;
    }

    public boolean isUrlMatch(String url) {
        boolean urlmatch = urlMatchRegex != null && url.matches(urlMatchRegex);
        logger.debug("url match test, url:{}, urlregex: {}, match:{}", url, urlMatchRegex, urlmatch);
        return urlmatch;
    }


    public int size() {
        return list.size();
    }

    public void clear() {
        list.clear();
    }

    public int ip2int(String ip) {
        int value = 0;
        String i[] = ip.split("\\.");
        for (String t : i) {
            int v = Integer.parseInt(t);
            if (v < 0 || v > 255) {
                return 0;
            }
            value = value * IPSEC + v;
        }
        return value;
    }

    public int mask2int(String mask) {
        int bits = 32 - Integer.parseInt(mask);
        if (bits < 0) {
            bits = 0;
        }
        return MASKDEF >> bits << bits;
    }

    public void addIP(String ip, String mask) {
        IPEntity e = new IPEntity(ip, mask);
        list.add(e);
    }

    public void addIP(String ipWithMask) {
        String[] t = ipWithMask.split("/");
        String mask = "32";
        if (t.length == 2) {
            String ip = t[0];
            mask = t[1];
            addIP(ip, mask);
        } else {
            String ip = t[0];
            addIP(ip, mask);
        }
    }

    public IPFilter load() {
        Boolean enable = Conf.getBoolean(name + "_IPFilterEnable", true);
        if (enable) {
            setUrlMatchRegex(Conf.getString(name + "_UrlMatchRegex"));
            String validIpList = Conf.getString(name + "_IPValidList", "");
            load(validIpList.split("[,;]"));
        }
        return this;
    }

    public void load(String[] validIpList) {
        for (String key : validIpList) {
            logger.debug("[{}]load ip: {}", name, key);
            addIP(key);
        }
    }

    public void removeIP(String ip, String mask) {
        IPEntity e = new IPEntity(ip, mask);
        list.remove(e);
    }

    public boolean isAllowed(String ip) {
        if (StringUtils.isBlank(ip)) {
            return false;
        }
        for (IPEntity e : list) {
            if (e.equals(ip)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "IPFilter{" +
                "name='" + name + '\'' +
                ", urlMatchRegex='" + urlMatchRegex + '\'' +
                '}';
    }

    private class IPEntity {
        private int iip;
        private int imask;

        IPEntity(String ip, String mask) {
            iip = ip2int(ip);
            imask = mask2int(mask);
        }

        public boolean equals(String target) {
            int targetIp = ip2int(target);
            return (iip == (targetIp & imask));
        }

        public boolean equals(Object obj) {
            if (obj instanceof IPEntity) {
                IPEntity e = (IPEntity) obj;
                return ((e.iip == this.iip) && (e.imask == this.imask));
            }
            return false;
        }
    }

}
