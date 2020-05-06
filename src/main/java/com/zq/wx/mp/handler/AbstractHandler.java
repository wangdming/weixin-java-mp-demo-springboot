package com.zq.wx.mp.handler;

import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
public abstract class AbstractHandler implements WxMpMessageHandler {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    WxMpXmlOutMessage resMsg(WxMpXmlMessage wxMessage, String msg) {
        return WxMpXmlOutMessage.TEXT().content(msg)
            .fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser()).build();
    }

    WxMpXmlOutMessage emptyRes() {
        return null;
    }
}
