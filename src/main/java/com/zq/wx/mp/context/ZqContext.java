package com.zq.wx.mp.context;

import lombok.Data;

@Data
public class ZqContext {

    private static final ThreadLocal<String> tl_appId = new ThreadLocal<>();

    public static void setAppId(String appId) {
        tl_appId.set(appId);
    }

    public static String getAppId() {
        return tl_appId.get();
    }
}
