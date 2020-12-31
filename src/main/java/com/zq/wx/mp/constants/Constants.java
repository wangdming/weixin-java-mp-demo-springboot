package com.zq.wx.mp.constants;

import java.util.regex.Pattern;

public class Constants {

    public static final String PA_APP_ID_FG = "wx34e1dfbfc4c6c572";
    public static final String PA_APP_ID_MG = "wxd3696547d8c3b0d1";
    public static final String PA_APP_ID_BG = "wx428eb505c47440f3";

    public static final String MP_APP_ID_FG = "wx1c616a5b321d220f";
    public static final String MP_APP_ID_MG = "wx1c616a5b321d220f";
    public static final String MP_APP_ID_BG = "wxb073c99fa3e771f5";

    public static final String LANG = "zh_CN";

    public static final String URI_USER_REFRESH_IN_BATCH = "/api/data/wx/user/refresh/inBatch";

    public static final String URI_USER_SAVE = "/api/data/wx_pa/user";

    public static final String URI_SEND_BIND_MOBILE_SMS = "/api/data/wx_pa/sms/sendBindMobileSms";

    public static final String URI_BIND_MOBILE = "/api/data/wx_pa/sms/bindMobile";

    public static final String ZQ_WX_AUTH_TOKEN = "zq_wx_auth_token";

    public static final String MENU_KEY_BIND_MOBILE_USER_TYPE_1 = "menu_key_bind_mobile_user_type_1";
    public static final String MENU_KEY_BIND_MOBILE_USER_TYPE_3 = "menu_key_bind_mobile_user_type_3";
    public static final String MENU_KEY_SHOW_TICKET_SCANS = "menu_key_show_ticket_scans";

    public static final String MENU_KEY_SKIP_TO_MP_USER_TYPE_1 = "menu_key_skip_to_mp_user_type_1";
    public static final String MENU_KEY_SKIP_TO_MP_USER_TYPE_3 = "menu_key_skip_to_mp_user_type_3";

    public static Pattern PATTERN_MOBILE = Pattern.compile("^1[3|4|5|7|8][0-9]\\d{4,8}$");

    public static final String MSG_SUBSCRIBE = "" +
            "本公众号仅限公司内部同事使用。" +
            "\n\n-- 使用方法" +
            "\n请设立部同事点击进入\"设立部\"页面 -> 然后\"绑定手机\"即可。" +
            "\n\n-- 功能" +
            "\n①每天早9:00，向你推送你的代办工作。" +
            "\n②你的工作完成后，实时通知后续环节的同事。" +
            "\n\n感谢您的使用。";

    public static final String ERROR_MSG_TEMPLATE_MESSAGE_PUSH_FAIL = "template_message_push_fail";
}
