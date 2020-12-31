package com.zq.wx.mp.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RPCResponse {

    private Boolean success;

    private String errorCode;
    private String errorMsg;

    public static RPCResponse success() {
        RPCResponse res = new RPCResponse();
        res.setSuccess(true);
        return res;
    }

    public static RPCResponse fail(String errorCode, String errorMsg) {
        RPCResponse res = new RPCResponse();
        res.setSuccess(false);
        res.setErrorCode(errorCode);
        res.setErrorMsg(errorMsg);
        return res;
    }
}
