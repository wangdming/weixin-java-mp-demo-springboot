package com.zq.wx.mp.service;

import com.zq.wx.mp.constants.Constants;
import com.zq.wx.mp.resource.RPCResponse;
import com.zq.wx.mp.resource.templateMsg.TemplateMessageParam;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplateMessageService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WxMpService wxMpService;

    public RPCResponse ba(String appId, TemplateMessageParam param) {
        WxMpTemplateMessage.WxMpTemplateMessageBuilder builder =
            WxMpTemplateMessage.builder().toUser(param.getToUser()).templateId(param.getTemplateId());

        if (param.isUseMp()) {
            WxMpTemplateMessage.MiniProgram miniProgram =
                new WxMpTemplateMessage.MiniProgram(param.getMpAppId(), param.getMpPagePath(), false);
            builder.miniProgram(miniProgram);
        }

        WxMpTemplateMessage templateMessage = builder.build();

        // TODO 微信bug，first设置颜色无效
        // TODO 所以将"金额"字段显示为红色(在下边for循环中)
        if (param.isTitleRed()) {
            templateMessage.getData().add(new WxMpTemplateData("first", param.getFirst(), "#FF0000"));
        } else if (param.isTitleGreen()) {
            templateMessage.getData().add(new WxMpTemplateData("first", param.getFirst(), "#3CB371"));
        } else {
            templateMessage.getData().add(new WxMpTemplateData("first", param.getFirst(), "#173177"));
        }

        for (int i = 0; i < param.getKeywords().size(); i++) {
            if (i == 2 && param.isTitleRed()) {
                templateMessage.getData().add(
                    new WxMpTemplateData("keyword" + (i + 1), param.getKeywords().get(i), "#FF0000"));
            } else if (i == 2 && param.isTitleGreen()) {
                templateMessage.getData().add(
                    new WxMpTemplateData("keyword" + (i + 1), param.getKeywords().get(i), "#3CB371"));
            } else if (i == 4 && param.isContentRed()) {
                templateMessage.getData().add(
                    new WxMpTemplateData("keyword" + (i + 1), param.getKeywords().get(i), "#FF0000"));
            } else {
                templateMessage.getData().add(
                    new WxMpTemplateData("keyword" + (i + 1), param.getKeywords().get(i), "#173177"));
            }
        }

        templateMessage.getData().add(new WxMpTemplateData("remark", param.getRemark(), "#173177"));

        return doSend(appId, templateMessage);
    }

    private RPCResponse doSend(String appId, WxMpTemplateMessage templateMessage) {
        try {
            String result = wxMpService.switchoverTo(appId).getTemplateMsgService().sendTemplateMsg(templateMessage);
            logger.info("模板消息推送成功: {}", result);
            return RPCResponse.success();
        } catch (WxErrorException e) {
            logger.error("模板消息推送失败: {}", e.toString());
            return RPCResponse.fail(Constants.ERROR_MSG_TEMPLATE_MESSAGE_PUSH_FAIL, e.toString());
        }
    }
}
