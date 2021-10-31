package com.ms.connectivitymonitor.commandline.ookla.responsemodel;

public class OoklaInterfaceDC {
    String externalIp;
    String internalIp;
    Boolean isVpn;
    String macAddr;
    String name;

    public String getExternalIp() {
        return externalIp;
    }

    public void setExternalIp(String externalIp) {
        this.externalIp = externalIp;
    }

    public String getInternalIp() {
        return internalIp;
    }

    public void setInternalIp(String internalIp) {
        this.internalIp = internalIp;
    }

    public Boolean getIsVpn() {
        return isVpn;
    }

    public void setIsVpn(Boolean vpn) {
        isVpn = vpn;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
