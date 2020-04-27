package com.rzmeu.yeelightcontrol;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class Util {

    private static final ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    public static String convertObjectToJson(Object object) {
        return mapper.writeValueAsString(object);
    }
}
