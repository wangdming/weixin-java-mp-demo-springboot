package com.zq.wx.mp.service;

import com.zq.wx.mp.config.ZqHttpConfigProperties;
import com.zq.wx.mp.constants.Constants;
import com.zq.wx.mp.utils.FluentUtil;
import com.google.common.collect.Lists;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.bean.result.WxMpUserList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WxMpService wxMpService;
    @Autowired
    private ZqHttpConfigProperties zqHttpConfigProperties;

    public String oAuthUrl_bindMobile() {
        String redirectUrl = "http://www.zq-tax.top/api/wx/user/pull";
        return wxMpService.oauth2buildAuthorizationUrl(redirectUrl, WxConsts.OAuth2Scope.SNSAPI_USERINFO, null);
    }

    public String pullWxUserByCode(String code) {
        // get access token
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = null;
        try {
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
        } catch (WxErrorException e) {
            logger.error("get access token by code ERROR.", e);
        }

        // get wxUser
        WxMpUser wxMpUser = null;
        try {
            wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
        } catch (WxErrorException e) {
            logger.error("pull wxMpUser by access token ERROR.", e);
        }

        // save wxUser
        return saveWxMpUser(wxMpUser, Constants.URI_USER_SAVE);
    }

    public void refreshUsersInfo() {
        WxMpUserList wxUsersOpedIds = pullOpenIds();
        if (wxUsersOpedIds == null || wxUsersOpedIds.getCount() == 0) {
            logger.info("pull wx user openIds get null.");
            return;
        }

        List<WxMpUser> users = pullUsersInfo(wxUsersOpedIds);
        if (users == null || users.isEmpty()) {
            logger.info("pull wx users' info get null.");
        }

        doRefreshWxMpUsers(users);
    }

    private WxMpUserList pullOpenIds() {
        WxMpUserList wxUserList = null;
        try {
            wxUserList = wxMpService.getUserService().userList("");
        } catch (WxErrorException e) {
            logger.error("pull wx user's openId error：", e);
        }
        return wxUserList;
    }

    private List<WxMpUser> pullUsersInfo(WxMpUserList wxMpUserList) {
        List<WxMpUser> users = Lists.newArrayList();

        List<String> openIds = wxMpUserList.getOpenids();
        for (String openId : openIds) {
            try {
                WxMpUser user = wxMpService.getUserService().userInfo(openId, Constants.LANG);
                users.add(user);
            } catch (WxErrorException e) {
                logger.error("pull wx user info error，openId={}, \n:", openId, e);
            }
        }

        return users;
    }

    public void save(int userType, WxMpUser user, String uri) {
        String url = buildCreateWxUserUrl(uri, userType);
        FluentUtil.httpPost_1(url, user);
    }

    private void doRefreshWxMpUsers(List<WxMpUser> users) {
        String url = buildUrl(Constants.URI_USER_REFRESH_IN_BATCH);
        String res = FluentUtil.httpPost(url, users);
        logger.info("refresh wx users' info response: {}", res);
    }

    private String saveWxMpUser(WxMpUser user, String uri) {
        String url = buildUrl(uri);
        String res = FluentUtil.httpPost(url, user);
        logger.info("save wx user response: {}", res);
        return res;
    }

    private String buildCreateWxUserUrl(String uri, int userType) {
        return buildUrl(uri) + "&userType=" + userType;
    }

    private String buildUrl(String uri) {
        return zqHttpConfigProperties.host + ":" + zqHttpConfigProperties.port + uri;
    }
}
