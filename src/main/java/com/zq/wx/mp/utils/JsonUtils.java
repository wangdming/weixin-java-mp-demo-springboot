package com.zq.wx.mp.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zq.wx.mp.exception.ZqRuntimeException;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
public class JsonUtils {

    private static Gson gson = new Gson();

    public static String toJson(Object obj) {
        Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
        return gson.toJson(obj);
    }

    public static <T>T fromJson(String json, Class<T> clz) {
        return gson.fromJson(json, clz);
    }
}
