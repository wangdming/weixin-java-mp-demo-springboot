package com.zq.wx.mp.handler;

import com.zq.wx.mp.config.ZqHttpConfigProperties;
import com.zq.wx.mp.config.ZqHttpUrlsConfigProperties;
import com.zq.wx.mp.constants.Constants;
import com.zq.wx.mp.resource.zq.AuthTokenResponse;
import com.zq.wx.mp.resource.zq.MaterialUploadResponse;
import com.zq.wx.mp.utils.AppIdUtil;
import com.zq.wx.mp.utils.FluentUtil;
import com.zq.wx.mp.utils.UserState;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Component
public class MsgHandler extends AbstractHandler {

    @Autowired
    private ZqHttpConfigProperties zqHttpConfigProperties;
    @Autowired
    private ZqHttpUrlsConfigProperties urlsConfigProperties;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {

        String appId = wxMpService.getWxMpConfigStorage().getAppId();
        int userType = AppIdUtil.getUserType(appId);
        String openId = wxMessage.getFromUser();

        /**
         * 注意，模板消息推送成功时，微信会回推一条成功通知，也会进入这个handler，需要先判断
         */
        if (StringUtils.isNotEmpty(wxMessage.getEvent()) && wxMessage.getEvent().equals("TEMPLATESENDJOBFINISH")) {
            return emptyRes();
        } else if (UserState.isBindingMobile(userType, openId)) {
            // 绑定手机号
            return bindMobile(wxMessage, wxMpService);
        } else if (UserState.isCheckingScans(userType, openId)) {
            // 票据扫描件
            return inputBaFetchNo(wxMessage, wxMpService);
        } else {
//            return resMsg(wxMessage, "不支持此操作");
            return emptyRes();
        }

        // 这里没有eventKey
//        String eventKey = wxMessage.getEventKey();

//        if (eventKey.equals(Constants.MENU_KEY_BIND_MOBILE_USER_TYPE_3)) {
//            // 绑定手机号
//            return bindMobile(wxMessage, wxMpService);
//        } else if (eventKey.equals(Constants.MENU_KEY_SHOW_TICKET_SCANS)) {
//            // 票据扫描件
//            return inputBaFetchNo(wxMessage, wxMpService);
//        } else {
//            return resMsg(wxMessage, "不支持此操作");
//        }
    }

    private WxMpXmlOutMessage bindMobile(WxMpXmlMessage wxMessage, WxMpService wxMpService) {
        String appId = wxMpService.getWxMpConfigStorage().getAppId();
        int userType = AppIdUtil.getUserType(appId);

        String openId = wxMessage.getFromUser();

        if (UserState.isWaitingMobile(userType, openId)) {
            return inputMobile(userType, openId, wxMessage);
        } else if (UserState.isWaitingSmsCode(userType, openId)) {
            return inputSmsCode(userType, openId, wxMessage);
        } else {
            return resMsg(wxMessage, "不支持此操作");
        }
    }

    private WxMpXmlOutMessage inputMobile(int userType, String openId, WxMpXmlMessage wxMessage) {
        if (isValidMobile(wxMessage.getContent())) {
            String mobile = wxMessage.getContent();
            AuthTokenResponse res = sendSmsCode(userType, openId, mobile);
            if (!res.isSuccess()) {
                if (res.getErrorCode().equals("1000")) {
                    return resMsg(wxMessage, "手机号码错误，请重新输入：");
                } else {
                    return resMsg(wxMessage, "系统异常，请联系管理员。");
                }
            }

            UserState.finishWaitingMobile(userType, openId);
            UserState.startWaitingSmsCode(userType, openId);

            return resMsg(wxMessage, "请输入短信验证码：");
        } else {
            return resMsg(wxMessage, "手机号格式错误，请重新输入：");
        }
    }

    private WxMpXmlOutMessage inputSmsCode(int userType, String openId, WxMpXmlMessage wxMessage) {
        String smsCode = wxMessage.getContent();
        AuthTokenResponse res = bindMobile(userType, openId, smsCode);
        if (!res.isSuccess()) {
            if (res.getErrorCode().equals("1100")) {
                return resMsg(wxMessage, "验证码错误，请重新输入：");
            } else {
                return resMsg(wxMessage, "系统异常，请联系管理员。");
            }
        }
        UserState.finishWaitingSmsCode(userType, openId);

        // 储存token
        UserState.putToken(openId, res.getToken());

        return resMsg(wxMessage, "绑定成功。");
    }

    private AuthTokenResponse sendSmsCode(int userType, String openId, String mobile) {
        String url = zqHttpConfigProperties.host + ":" + zqHttpConfigProperties.port
            + Constants.URI_SEND_BIND_MOBILE_SMS + "?openId=" + openId + "&mobile=" + mobile
            + "&userType=" + userType;
        return FluentUtil.httpGet_1(url);
    }

    private AuthTokenResponse bindMobile(int userType, String openId, String smsCode) {
        String url = zqHttpConfigProperties.host + ":" + zqHttpConfigProperties.port
            + Constants.URI_BIND_MOBILE + "?openId=" + openId + "&smsCode=" + smsCode
            + "&userType=" + userType;
        return FluentUtil.httpGet_1(url);
    }

    private Boolean isValidMobile(String input) {
        if (StringUtils.isEmpty(input)) {
            return false;
        } else {
            Matcher m = Constants.PATTERN_MOBILE.matcher(input);
            return m.matches();
        }
    }

    private WxMpXmlOutMessage inputBaFetchNo(WxMpXmlMessage wxMessage, WxMpService wxMpService) {
        // 获取access token
        String accessToken;
        try {
            accessToken = wxMpService.getAccessToken();
        } catch (Exception e) {
            logger.error("get accessToken error: {}", e);
            return resMsg(wxMessage, "发生错误，请重试。");
        }

        // 获取用户openId
        String openId = wxMessage.getFromUser();

        // 上传临时素材较慢，先返回"请稍候"
        WxMpKefuMessage holdOn = WxMpKefuMessage.TEXT().content("处理中，请稍候。").toUser(openId).build();
        try {
            wxMpService.getKefuService().sendKefuMessage(holdOn);
        } catch (Exception e) {
            logger.error("send holdOn(请稍候) kefu(客户) message error: {}", e);
        }
        try {
            // 为防止"处理中，请稍候。"先发后至，这里延迟1秒
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 调用data的接口，将票据扫描件上传至临时素材，并返回图片的mediaId
        MaterialUploadResponse materialUploadRes = uploadImages(openId, accessToken, wxMessage.getContent());

        // 接口返回异常
        if (!materialUploadRes.isSuccess()) {
            logger.error("data接口返回错误：", materialUploadRes.getE().getErrorMsg());
//            return resMsg(wxMessage, "发生错误，请重试。");
        } else {
            logger.info("data上传临时素材结果：{}", materialUploadRes.toString());
        }

        // 如果扫描件返回空，提示"检查快递单号"
        if (materialUploadRes.getMediaIds().isEmpty()) {
            return resMsg(wxMessage, "查询码错误，请您重新输入：");
        }

        // 循环推送票据扫描件
        for (String mediaId : materialUploadRes.getMediaIds()) {
            WxMpKefuMessage msg = WxMpKefuMessage.IMAGE().toUser(openId).mediaId(mediaId).build();
            try {
                wxMpService.getKefuService().sendKefuMessage(msg);
            } catch (Exception e) {
                logger.error("send image kefu(客服) message error: {}", e);
                return resMsg(wxMessage, "发生错误，请重试。");
            }
        }

        // 如果票据过多，最后提示"请前往小程序"
        if (materialUploadRes.isToMuchScans()) {
            WxMpKefuMessage msg = WxMpKefuMessage.TEXT().toUser(openId)
                .content("票据过多，请前往Web端或小程序查看详细信息。").build();
            try {
                wxMpService.getKefuService().sendKefuMessage(msg);
            } catch (Exception e) {
                logger.error("send image kefu(客服) message error: {}", e);
                return resMsg(wxMessage, "发生错误，请重试。");
            }
        }

        UserState.finishCheckScans(3, openId);

        try {
            // 为防止"扫描件发送完毕。"后发先至，这里延迟1秒
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resMsg(wxMessage, "扫描件发送完毕。");
    }

    private MaterialUploadResponse uploadImages(String openId, String accessToken, String fetchNo) {
        String url = zqHttpConfigProperties.host + ":" + zqHttpConfigProperties.port
            + urlsConfigProperties.uploadMaterial
            .replace("{openId}", openId)
            .replace("{accessToken}", accessToken)
            .replace("{fetchNo}", fetchNo);
        return FluentUtil.httpGet_2(url);
    }
}
