package com.myprj.sample.api.log;

/**
 * 실시간 로그레벨 조회
 */
public interface LogRealtimeLogLevelInterface {

    /**
     * 사용자 실시간 로그레벨 조회
     * @param userId 사용자id
     * @return 로그레벨(없으면 Null or Empty)
     */
    public String getUserLogLevel(String userId);

    /**
     * API실시간 로그레벨 조회
     * @param url 호출 URL(Controller에 정의된 URI)
     * @param method method
     * @return 로그레벨(없으면 Null or Empty)
     */
    public String getApiLogLevel(String url, String method);
}
