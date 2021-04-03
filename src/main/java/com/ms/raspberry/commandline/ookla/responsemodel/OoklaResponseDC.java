package com.ms.raspberry.commandline.ookla.responsemodel;

import java.util.Date;

public class OoklaResponseDC {
    private OoklaTransferSpeedDC download;
    private OoklaInterfaceDC _interface;
    private String isp;
    private Double packetLoss;
    private OoklaPingDC ping;
    private OoklaResultDC result;
    private OoklaServerDC server;
    private Date timestamp;
    private String type;
    private OoklaTransferSpeedDC upload;

    public OoklaTransferSpeedDC getDownload() {
        return download;
    }

    public void setDownload(OoklaTransferSpeedDC download) {
        this.download = download;
    }

    public OoklaInterfaceDC getInterface() {
        return _interface;
    }

    public void setInterface(OoklaInterfaceDC _interface) {
        this._interface = _interface;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

    public Double getPacketLoss() {
        return packetLoss;
    }

    public void setPacketLoss(Double packetLoss) {
        this.packetLoss = packetLoss;
    }

    public OoklaPingDC getPing() {
        return ping;
    }

    public void setPing(OoklaPingDC ping) {
        this.ping = ping;
    }

    public OoklaResultDC getResult() {
        return result;
    }

    public void setResult(OoklaResultDC result) {
        this.result = result;
    }

    public OoklaServerDC getServer() {
        return server;
    }

    public void setServer(OoklaServerDC server) {
        this.server = server;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public OoklaTransferSpeedDC getUpload() {
        return upload;
    }

    public void setUpload(OoklaTransferSpeedDC upload) {
        this.upload = upload;
    }
}
