package com.myprj.sample.fwk.requesttrace;

/**
 * 거래추적 정보 공통 변수 저장/접근 클래스
 */
public class RequestTraceContextHandler {

    private static final ThreadLocal<RequestTraceContext> REQUEST_TRACE;

    static {
        REQUEST_TRACE = new InheritableThreadLocal<>();
    }

    /**
     * threadlocal의 값( )RequestTrace) 세팅하기
     * @param trace 저장할 trace관련 값
     */
    public static void setRequestTraceContext(RequestTraceContext trace) { REQUEST_TRACE.set(trace); }

    /**
     * threadlocal의 값( )RequestTrace) 조회하기
     * @return RequestTrace 저장된 trace관련 값
     */
    public static RequestTraceContext getRequestTraceContext(){ return REQUEST_TRACE.get(); }

    public static RequestTraceContext getRequestTraceContext(boolean create) {
        RequestTraceContext trace = getRequestTraceContext();
        if(trace != null){
            return trace;
        }

        if(!create){
            return null;
        }

        RequestTraceContext created = RequestTraceContext.builder()
                .guid(RequestTraceContext.generateGuid())
                .build() ;
        setRequestTraceContext(created);

        return created;
    }

    public static void clearContext() { REQUEST_TRACE.remove(); }
}
