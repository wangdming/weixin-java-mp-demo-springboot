package com.zq.wx.mp.resource.zq;

import com.zq.wx.mp.utils.JsonUtils;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
public class AuthTokenResponse extends BaseDataResponse {

    private String token;

    public AuthTokenResponse(String json) {
        checkZqEx(json);

        if (success) {
            Map<String, String> res = JsonUtils.fromJson(json, Map.class);
            if (res != null && res.get("token") != null) {
                token = res.get("token");
            }
        }
    }

    public String getErrorCode() {
        return e.getErrorCode();
    }
}
