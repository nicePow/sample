package com.myprj.sample.fwk.online;

/**
 * 응답 헤더에 메시지르 ㄹ설정할 때 필요한 컨섹스트
 */
public class ResponseContextHandler {

    private static final ThreadLocal<ResponseContext> RESPONSE_CONTEXT;

    static{
        RESPONSE_CONTEXT = new ThreadLocal<>();
    }

    /**
     * 정상 응답 메시지를 설정한다.
     *
     * @param messageCode 메세지 코드
     * @param parameters 메시지 변환에 필요한 파라미터
     */
    public static void setMessage(String messageCode, Object... parameters){
        setResponseContext(messageCode, null, null, false, parameters);
    }

    /**
     * 오류 응답 메시지를 설정한다.
     *
     * @param exception 에러상황
     * @param messageCode 메세지 코드
     * @param parameters 메시지 변환에 필요한 파라미터
     */
    public static void setErrorMessage(Exception exception, String messageCode, Object... parameters){
        setResponseContext(messageCode, null, exception, true, parameters);
    }

    public static ResponseContext getResponseContext() { return RESPONSE_CONTEXT.get(); }

    public static void clearContext() { RESPONSE_CONTEXT.remove();}

    public static void setResponseContext(String messageCode, String resolvedMessage, Exception exception, boolean error, Object... parameters) {
        RESPONSE_CONTEXT.set(ResponseContext.builder()
                        .messageCode(messageCode)
                        .resolvedMessage(resolvedMessage)
                        .exception(exception)
                        .error(error)
                        .parameters(parameters)
                        .build());
    }
}
