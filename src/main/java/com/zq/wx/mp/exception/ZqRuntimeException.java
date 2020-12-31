package com.zq.wx.mp.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
//@JsonIgnoreProperties(ignoreUnknown = true)
public class ZqRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1645578935788299824L;

    private String errorCode;
    private List<String> errorData;
    private String errorMsgKey;
    private String errorMsg;
    private String invokeInfo;
}
