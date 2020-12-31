package com.zq.wx.mp.resource.templateMsg;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TemplateMessageParam {

    private String templateId;
    private String toUser;
    private String first;
    private List<String> keywords = Lists.newArrayList();
    private String remark;

    private boolean useMp = false;
    private String mpAppId;
    private String mpPagePath;

//    private String baIssuedExpressNumber;

    // NOTE 微信bug，First设置字体颜色无效，修改其他字段的颜色
    // NOTE ba_create_bg，title设置为红色
    // NOTE ba_issued_bg，title设置为绿色
    private boolean titleRed = false;
    private boolean titleGreen = false;
    private boolean contentRed = false;
}
