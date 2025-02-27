package com.myprj.sample.fwk.requesttrace;

import lombok.Getter;

/**
 * 표준 Header의 key값 정의
 */

@Getter
public enum RequestTraceHeader {

    GUID("Reap-Guid", "거래고유번호" ,  null),
    GUID_SEQUENCE("Reap-Guid-seq", "거래고유순번" ,  null),
    ORIGN_SYSCODE("Reap-Orign-Syscode", "시스템코드:",  null),
    USER_ID("Reap-User_id", "사용자아이디" ,  null),
    SCREEN_ID("Reap-Screen_ID", "화면아이디" ,  null),
    FUNCTION_CODE("Reap-Function-Code", "화면전달코드" ,  null);

    RequestTraceHeader(String key, String exp, String defaultValue) {
        this(key, exp, defaultValue,true, true);
    }

    RequestTraceHeader(String key, String exp, String defaultValue, boolean exposeRequest, boolean exposeResponse){
        this.key = key;
        this.exp = exp;
        this.defaultValue = defaultValue;
        this.exposeRequest = exposeRequest;
        this.exposeResponse = exposeResponse;
    }

    /**
     * 헤더의 key값
     */
    private final String key;

    /**
     * key설명
     */
    private final String exp;

    /**
     * 기본값
     */
    private final String defaultValue;

    /**
     * request 포함여부
     */
    private final boolean exposeRequest;

    /**
     * response 포함여부
     */
    private final boolean exposeResponse;
}
