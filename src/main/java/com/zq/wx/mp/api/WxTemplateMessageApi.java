package com.zq.wx.mp.api;

import com.zq.wx.mp.resource.RPCResponse;
import com.zq.wx.mp.resource.templateMsg.TemplateMessageParam;
import com.zq.wx.mp.service.TemplateMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wx-pa/template-message/{appId}")
public class WxTemplateMessageApi {

    @Autowired
    private TemplateMessageService templateMessageService;

    @PostMapping
    public RPCResponse ba(@PathVariable String appId, @RequestBody TemplateMessageParam param) {
        return templateMessageService.ba(appId, param);
    }
}
