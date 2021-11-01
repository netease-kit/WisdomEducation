package com.netease.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class InetAddressUtil {
    public static String getCurrentEnvironmentNetworkIp() {
        String currentHostIpAddress = null;
        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    InetAddress addr = address.nextElement();
                    if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()
                            && !addr.getHostAddress().contains(":")) {
                        currentHostIpAddress = addr.getHostAddress();
                    }
                }
            }
            if (currentHostIpAddress == null) {
                currentHostIpAddress = "127.0.0.1";
            }

        } catch (SocketException e) {
            currentHostIpAddress = "127.0.0.1";
        }
        return currentHostIpAddress;
    }
}
