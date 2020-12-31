package com.zq.wx.mp.resource.zq;

import com.zq.wx.mp.exception.ZqRuntimeException;
import com.zq.wx.mp.utils.JsonUtils;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Data
@ToString
public class BaseDataResponse {

    protected boolean success = true;
    protected ZqRuntimeException e;

    protected void checkZqEx(String json) {
        if (StringUtils.isEmpty(json)) {
            return;
        }

//        System.out.println("BaseDataResponse.checkZqEx, json: " + json);
        e = JsonUtils.fromJson(json, ZqRuntimeException.class);

        if (StringUtils.isNotEmpty(e.getErrorCode())) {
            success = false;
        }
    }
}
