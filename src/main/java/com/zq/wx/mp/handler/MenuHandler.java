package com.zq.wx.mp.handler;

import com.zq.wx.mp.config.ZqHttpConfigProperties;
import com.zq.wx.mp.constants.Constants;
import com.zq.wx.mp.utils.AppIdUtil;
import com.zq.wx.mp.utils.FluentUtil;
import com.zq.wx.mp.utils.UserState;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Component
public class MenuHandler extends AbstractHandler {

// The dependencies of some of the beans in the application context form a cycle:
//
// ┌─────┐
// |  wxMpConfiguration defined in file [/Users/wangdm/IdeaProjects/weixin-java-mp-demo-springboot/target/classes/com/github/binarywang/demo/wx/mp/config/WxMpConfiguration.class]
// ↑     ↓
// |  menuHandler (field private UserService MenuHandler.userService)
// ↑     ↓
// |  userService (field private me.chanjar.weixin.mp.api.WxMpService UserService.wxMpService)
// └─────┘

    //    @Autowired
//    private UserService userService;
    @Autowired
    private ZqHttpConfigProperties zqHttpConfigProperties;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {

        // DEBUG
        logger.info("WxMpXmlMessage: {}", wxMessage.toString());
        logger.info("context: {}", context.toString());

        String eventKey = wxMessage.getEventKey();

        if (eventKey.equals(Constants.MENU_KEY_BIND_MOBILE_USER_TYPE_3)) {
            // fg绑定手机号
            return bindMobile(wxMessage, wxMpService);
        } else if (eventKey.equals(Constants.MENU_KEY_SHOW_TICKET_SCANS)) {
            // fg查询扫描件
            return showTicketScans(wxMessage, wxMpService);
        } else if (eventKey.equals(Constants.MENU_KEY_BIND_MOBILE_USER_TYPE_1)) {
            // bg绑定手机号
            return bindMobile(wxMessage, wxMpService);
        } else {
            return resMsg(wxMessage, "不支持此操作");
        }
    }

    private WxMpXmlOutMessage bindMobile(WxMpXmlMessage wxMessage, WxMpService wxMpService) {
        String appId = wxMpService.getWxMpConfigStorage().getAppId();
        int userType = AppIdUtil.getUserType(appId);
        String openId = wxMessage.getFromUser();

        // step.0 记录状态
        UserState.pressBindMobileButton(userType, openId);

        // step.1 获取用户信息
        WxMpUser wxMpUser;
        try {
            wxMpUser = wxMpService.getUserService().userInfo(openId);
        } catch (WxErrorException e) {
            logger.error("get user info error: {}", e);
            return resMsg(wxMessage, "发生错误，请重试。");
        }

        // step.2 保存用户信息
        logger.debug("menu handler. wxMpUser: \n{}", wxMpUser.toString());
        saveWxMpUser(wxMpUser);

        // step.3 记录用户状态
        UserState.startWaitingMobile(userType, openId);

        // step.4 返回客服消息，要求输入手机号码
        return resMsg(wxMessage, "请输入您的手机号码：");
    }

    private void saveWxMpUser(WxMpUser user) {
        String url = zqHttpConfigProperties.host + ":" + zqHttpConfigProperties.port + Constants.URI_USER_SAVE;
        FluentUtil.httpPost_1(url, user);
    }

    private WxMpXmlOutMessage showTicketScans(WxMpXmlMessage wxMessage, WxMpService wxMpService) {
        String appId = wxMpService.getWxMpConfigStorage().getAppId();
        int userType = AppIdUtil.getUserType(appId);
        String openId = wxMessage.getFromUser();

        UserState.pressCheckScansButton(userType, openId);

        return resMsg(wxMessage, "请输入扫描件查询码：");
    }
}
