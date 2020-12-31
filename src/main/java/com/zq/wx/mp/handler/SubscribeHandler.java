package com.zq.wx.mp.handler;

import java.util.Map;

import com.zq.wx.mp.config.ZqHttpConfigProperties;
import com.zq.wx.mp.constants.Constants;
import com.zq.wx.mp.utils.AppIdUtil;
import com.zq.wx.mp.utils.FluentUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zq.wx.mp.builder.TextBuilder;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Component
public class SubscribeHandler extends AbstractHandler {

    @Autowired
    private ZqHttpConfigProperties zqHttpConfigProperties;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) throws WxErrorException {

        // debug
//        this.logger.info("新关注用户 OPENID: " + wxMessage.getFromUser());

        String appId = wxMpService.getWxMpConfigStorage().getAppId();
        int userType = AppIdUtil.getUserType(appId);

        // 获取微信用户基本信息
        try {
            WxMpUser wxMpUser = wxMpService.getUserService()
                .userInfo(wxMessage.getFromUser(), null);
            if (wxMpUser != null) {
                // 添加关注用户到本地数据库
                logger.debug("subscribe handler. wxMpUser: {}", wxMpUser.toString());
                save(userType, wxMpUser);
            }
        } catch (WxErrorException e) {
            if (e.getError().getErrorCode() == 48001) {
                this.logger.info("该公众号没有获取用户信息权限！");
            }
            throw e;
        }


        WxMpXmlOutMessage responseResult = null;
        try {
            responseResult = this.handleSpecial(wxMessage);
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }

        if (responseResult != null) {
            return responseResult;
        }

        try {
            return new TextBuilder().build("感谢您的关注。\n请点击底部菜单绑定手机号，即可接收实时业务通知。", wxMessage, wxMpService);
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     * 处理特殊请求，比如如果是扫码进来的，可以做相应处理
     */
    private WxMpXmlOutMessage handleSpecial(WxMpXmlMessage wxMessage) throws Exception {
        //TODO
        return null;
    }

    private void save(int userType, WxMpUser user) {
        String url = buildUrl(userType);
        FluentUtil.httpPost_1(url, user);
    }

    private String buildUrl(int userType) {
        return zqHttpConfigProperties.host + ":" + zqHttpConfigProperties.port + Constants.URI_USER_SAVE + "?userType=" + userType;
    }
}
