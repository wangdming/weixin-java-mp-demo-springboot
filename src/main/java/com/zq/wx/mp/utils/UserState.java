package com.zq.wx.mp.utils;

import com.google.common.collect.Maps;

import java.util.Map;

public class UserState {

    /**
     * 记录用户刚刚点击了"绑定手机号"还是"快捷查看票据扫描件"
     */
    private static Map<String, Boolean> isBindingMobileQueue = Maps.newHashMap();
    private static Map<String, Boolean> isCheckingScansQueue = Maps.newHashMap();

    public static void pressBindMobileButton(int userType, String openId) {
        isBindingMobileQueue.put(key(userType, openId), true);
        finishWaitingMobile(userType, openId);
        finishWaitingSmsCode(userType, openId);

        isCheckingScansQueue.remove(key(userType, openId));
    }

    public static void pressCheckScansButton(int userType, String openId) {
        isBindingMobileQueue.remove(key(userType, openId));
        finishWaitingMobile(userType, openId);
        finishWaitingSmsCode(userType, openId);

        isCheckingScansQueue.put(key(userType, openId), true);
    }

    public static void finishCheckScans(int userType, String openId) {
        isCheckingScansQueue.remove(key(userType, openId));
    }

    public static boolean isBindingMobile(int userType, String openId) {
        return isBindingMobileQueue.get(key(userType, openId)) != null && isBindingMobileQueue.get(key(userType, openId));
    }

    public static boolean isCheckingScans(int userType, String openId) {
        return isCheckingScansQueue.get(key(userType, openId)) != null && isCheckingScansQueue.get(key(userType, openId));
    }

    /**
     * 是否等待输入手机号码。key: openId
     */
    private static Map<String, Boolean> waitingMobileQueue = Maps.newHashMap();

    public static void startWaitingMobile(int userType, String openId) {
        waitingMobileQueue.put(key(userType, openId), true);
    }

    public static void finishWaitingMobile(int userType, String openId) {
        waitingMobileQueue.put(key(userType, openId), false);
    }

    public static Boolean isWaitingMobile(int userType, String openId) {
        if (waitingMobileQueue.get(key(userType, openId)) == null) {
            return false;
        } else {
            return waitingMobileQueue.get(key(userType, openId));
        }
    }

    /**
     * 是否等待输入短信验证码。key: openId
     */
    private static Map<String, Boolean> waitingSmsCodeQueue = Maps.newHashMap();

    public static void startWaitingSmsCode(int userType, String openId) {
        waitingSmsCodeQueue.put(key(userType, openId), true);
    }

    public static void finishWaitingSmsCode(int userType, String openId) {
        waitingSmsCodeQueue.put(key(userType, openId), false);
    }

    public static Boolean isWaitingSmsCode(int userType, String openId) {
        if (waitingSmsCodeQueue.get(key(userType, openId)) == null) {
            return false;
        } else {
            return waitingSmsCodeQueue.get(key(userType, openId));
        }
    }

    /**
     * 绑定手机号成功，储存token。key: openid
     * NOTE TODO 目前token是永久的，有待改善
     */
    private static Map<String, String> tokenStorage = Maps.newHashMap();

    public static void putToken(String openId, String token) {
        tokenStorage.put(openId, token);
    }

    public static String getToken(String openId) {
        return tokenStorage.get(openId);
    }

    private static String key(int userType, String openId) {
        return userType + "_" + openId;
    }
}
