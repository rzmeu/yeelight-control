package com.rzmeu.yeelightcontrol.model;

import lombok.Data;

import java.util.List;

@Data
public class Command {
    private int id;
    private String method;
    private List<String> params;
}
