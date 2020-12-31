package com.zq.wx.mp.resource.zq;

import com.google.common.collect.Lists;
import com.zq.wx.mp.utils.JsonUtils;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@ToString
public class MaterialUploadResponse extends BaseDataResponse {

    private boolean toMuchScans;
    private List<String> mediaIds = Lists.newArrayList();

    public MaterialUploadResponse(String json) {
        checkZqEx(json);

        if (success) {
            Map<String, Object> res = JsonUtils.fromJson(json, Map.class);

            if (res.get("toMuchScans") != null) {
                toMuchScans = (Boolean) res.get("toMuchScans");
            }
            if (res.get("mediaIds") != null) {
                mediaIds = (List) res.get("mediaIds");
            }
        }
    }
}
