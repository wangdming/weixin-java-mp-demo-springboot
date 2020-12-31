package com.zq.wx.mp.utils;

import com.zq.wx.mp.constants.Constants;

public class AppIdUtil {

    public static Integer getUserType(String appId) {
        int userType = 0;

        switch (appId) {
            case Constants.PA_APP_ID_BG:
                userType = 1;
                break;
            case Constants.PA_APP_ID_MG:
                userType = 2;
                break;
            case Constants.PA_APP_ID_FG:
                userType = 3;
                break;
            default:
                break;
        }

        return userType;
    }
}
