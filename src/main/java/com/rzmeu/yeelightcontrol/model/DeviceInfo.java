package com.rzmeu.yeelightcontrol.model;

import lombok.Data;

import java.util.List;

@Data
public class DeviceInfo {
    private String id;
    private String name;
    private String host;
    private int port;
    private String model;
    private String firmwareVersion;
    private List<String> support;
    private String power;
    private int bright;
    private Integer colorMode;
    private int ct;
    private int rgb;
    private int hue;
    private int sat;

}
