package com.myprj.sample.api.aop;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myprj.sample.api.aop.journal.JournalMakeUtils;
import com.myprj.sample.api.log.LogRealtimeFilter;
import com.myprj.sample.api.log.LogRealtimeLogLevelInterface;
import com.myprj.sample.fwk.online.ResponseContext;
import com.myprj.sample.fwk.online.ResponseContextHandler;
import com.myprj.sample.fwk.requesttrace.RequestTraceContext;
import com.myprj.sample.fwk.requesttrace.RequestTraceContextHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import java.io.InputStream;
import java.util.*;

/**
 * API 요청 전/후 처리를 위한 Aspect
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    public static final String REQUEST_GUID;
    public static final String DEFAULT_ERROR_MESSAGE;

    public static final String DYNAMIC_LOG_LEVEL;

    public static final String DYNAMIC_LOG_LEVEL_SETTER;

    static {
        REQUEST_GUID = "REQUEST_GUID";
        DEFAULT_ERROR_MESSAGE = "메시지 코드 조회 실패";
        DYNAMIC_LOG_LEVEL = "DYNAMIC_LOG_LEVEL";
        DYNAMIC_LOG_LEVEL_SETTER = "DYNAMIC_LOG_LEVEL_SETTER";
    }

    @Value("${spring.application.name:}")
    private String applicationName;

    @Autowired
    private final ObjectMapper objectMapper;

    @Value("${spring.profiles.active:local}")
    private String profile;

//    [TODO] CryptoService 구현필요
//    @Autowired(required = false)
//    private CryptoService cryptoService;

    @Autowired(required = false)
    private LogRealtimeLogLevelInterface logRealtimeLogLevelInterface;


    public LoggingAspect(ObjectMapper objectMapper) { this.objectMapper = objectMapper; }

    @Pointcut("demoPackage() && restController() && executionRequestMappingMethod()")
    public void loggingPointcut(){

    }

    @Pointcut("within(com.framework.first..*)")
    public void demoPackage(){

    }

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restController(){

    }

    /**
     * api pointcut
     */
    @Pointcut(
            // @formatter:off
            "execution(@org.springframework.web.bind.annotation.GetMapping * *(..))" +
                    "||" +
            "execution(@org.springframework.web.bind.annotation.PostMapping * *(..))" +
            "||" +
            "execution(@org.springframework.web.bind.annotation.PutMapping * *(..))" +
            "||" +
            "execution(@org.springframework.web.bind.annotation.DeleteMapping * *(..))" +
            "||" +
            "execution(@org.springframework.web.bind.annotation.PatchMapping * *(..))" +
            "||" +
            "execution(@org.springframework.web.bind.annotation.RequestMapping * *(..))"
            // @formatter:on
            )
    public  void executionRequestMappingMethod() {

    }

    @Before("loggingPointcut()")
    public void before(JoinPoint joinPoint) {
        log.trace("before");
        if(notLogging()){
            return;
        }

        puGuid();
        putRealTimeLogLevel();
//        putUserLogLevel();
        journal(joinPoint);
    }



    /**
     * 경로를 보고 로깅이 필요한지 판단(현재는 api 체계 잡기 전이라 전체 경로)
     * @return
     */
    private boolean notLogging(){
        log.trace("notLogging");

        ServletRequestAttributes servletRequest = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if(servletRequest == null){
            return true;
        }

        HttpServletRequest request = servletRequest.getRequest();
        String uri = request.getRequestURI();

        if(uri == null){
            return true;
        }

        return !uri.startsWith("/");
    }

    private void puGuid(){
        clearGuid();
        log.trace("putGuid");

        RequestTraceContext requestTrace = RequestTraceContextHandler.getRequestTraceContext();

        if(requestTrace != null) {
            MDC.put(REQUEST_GUID, requestTrace.getGuid());
        } else {
            log.warn("current trace is null");
        }
    }


    /**
     * MDC(slf4j의 threadLocal)에 GUID값 제거
     */
    private void clearGuid() {
        log.trace("clearGuid");
        MDC.remove(REQUEST_GUID);
    }

    /**
     *
     * @param argument 파라미터 객체
     * @return Object 클래스명
     */
    private Object argument(Object argument){
        if(argument instanceof HttpServletRequest){
            return HttpServletRequest.class;
        }
        if(argument instanceof HttpServletResponse){
            return HttpServletResponse.class;
        }
        if(argument instanceof MultipartFile file){
            return file.getOriginalFilename();
        }
        if(argument instanceof InputStream){
            return InputStream.class;
        }

        // 파라미터가 첨부파일일 경우가 있는데 그럴경우 파일명으로 내려준다.
        if(argument instanceof LinkedList<?> list) {
            if( !list.isEmpty() ){
                if( list.getFirst() instanceof MultipartFile ) {
                    List<String> nameList = new ArrayList<>();
                    for(Object obj : list){
                        if(obj instanceof MultipartFile file){
                            nameList.add(file.getOriginalFilename());
                        }
                    }
                    return nameList;
                }
            }
        }

        return argument;
    }


    /**
     * 저널로그 - before에서 수행(입력값 확인)
     * @param joinPoint aop의 조인트포인트
     */
    private void journal(JoinPoint joinPoint) {
        if(RequestTraceContextHandler.getRequestTraceContext() == null){
            return;
        }

        // make input parameters
        Map<String, Object> input = new LinkedHashMap<>();
        String[] parameterNames = ((CodeSignature)joinPoint.getSignature()).getParameterNames();
        Object[] arguments = joinPoint.getArgs();
        for(int i=0; i<arguments.length; i++){
            String parameterName = parameterNames[i];
            Object argument = argument(arguments[i]);
            input.put(parameterName, argument);
        }

        try {
            RequestTraceContextHandler.getRequestTraceContext().setInput(objectMapper.writeValueAsString(input));
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException: ", e);
            RequestTraceContextHandler.getRequestTraceContext().setInput("{\"value\":\"input param make Json Exception\"}");
        }
        JournalMakeUtils.journalRequtest();

    }

    /**
     * 저널로그 - afterReturning에서 수행
     * @param joinPoint
     * @param r
     */
    private void journal(JoinPoint joinPoint, Object r){
        if(RequestTraceContextHandler.getRequestTraceContext() == null){
            return;
        }

        ResponseContext responseContext = ResponseContextHandler.getResponseContext();
        if(responseContext != null && responseContext.isError()) {
            String code = responseContext. getMessageCode();
            Object[] parameters = responseContext.getParameters();
            String desc = "결과도출 실패";
            JournalMakeUtils.journalFail(code, desc);
        } else {
            boolean saveResponse = !profile.equalsIgnoreCase("prod");
            //[TODO] cryptoService 구현필요
            //JournalMakeUtils.journalSuccess(r, cryptoService, saveResponse);
        }
    }



    /**
     * 실시간 사용자 로그레벨 설정
     */
    /*
    private void putUserLogLevel() {
        String logLevel = MDC.get(DYNAMIC_LOG_LEVEL);
        if(StringUtils.hasLength(logLevel)){
            return;
        }

        RequestTraceContext requestTrace = RequestTraceContextHandler.getRqeuestTraceContext();

        if( requestTrace == null ){
            return;
        }

        UserLogLevelDto dto = dynamicLogLevelService.getUserLogLevelFromCache(requestTrace.getUserId());

        if(dto != null){
            log.debug("User Dynamic Log Level : {}", dto);
            MDC.put(DYNAMIC_LOG_LEVEL, dto.getLogLevel());
            MDC.put(DYNAMIC_LOG_LEVEL_SETTER, dto.getSetterId());
        }
    }
    */

    /**
     * MDC(slf4j의 threadLocal)에 LogLevel값 제거
     */
    private void clearLogLevel() {
        MDC.remove(DYNAMIC_LOG_LEVEL);
        MDC.remove(DYNAMIC_LOG_LEVEL_SETTER);
    }

    private String getValidateLogLevel(String level) {
        String[] allowList = {"ALL","TRACE", "DEBUG", "INFO", "WARN", "ERROR", "OFF"};
        
        if(Arrays.stream(allowList).noneMatch(f->f.equalsIgnoreCase(level))) {
            return "";
        }
        
        return level;
    }

    /**
     * 실시간 로그레벨 설정
     */
    private void putRealTimeLogLevel(){
        log.trace("putRealTimeLogLevel");

        RequestTraceContext requestTrace = RequestTraceContextHandler.getRequestTraceContext();
        if (requestTrace == null) {
            return;
        }

        ServletRequestAttributes servletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (servletRequest == null) {
            return;
        }

        HttpServletRequest request = servletRequest.getRequest();
        String url = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String method = request.getMethod();

        if(logRealtimeLogLevelInterface != null){
            String readUserLogLevel = logRealtimeLogLevelInterface.getUserLogLevel(requestTrace.getUserId());
            String readApiLogLevel = logRealtimeLogLevelInterface.getApiLogLevel(url, method);

            String userLogLevel = getValidateLogLevel(readUserLogLevel);
            String apiLogLevel = getValidateLogLevel(readApiLogLevel);
            
            if(StringUtils.hasLength(userLogLevel) && StringUtils.hasLength(apiLogLevel)){
                Level user = Level.toLevel(userLogLevel);
                Level api = Level.toLevel(apiLogLevel);
                
                // 사용자 로그 레벨 및 API로그 레벨이 모두 존재하는 경우, 더 낮은 레벨로 세팅
                if(user.isGreaterOrEqual(api)){
                    MDC.put(LogRealtimeFilter.REALTIME_LOG_LEVEL,apiLogLevel);
                } else {
                    MDC.put(LogRealtimeFilter.REALTIME_LOG_LEVEL,userLogLevel);
                }
                
            } else if (StringUtils.hasLength(userLogLevel)) {
                MDC.put(LogRealtimeFilter.REALTIME_LOG_LEVEL, userLogLevel);
            } else if (StringUtils.hasLength(apiLogLevel)) {
                MDC.put(LogRealtimeFilter.REALTIME_LOG_LEVEL, apiLogLevel);
            }
        }



    }

    /**
     * MDC(slf4j의 threadLocal)에 Realtime_log_level값 제거
     */
    private void clearRealtimeLogLevel(){
        log.trace("clearRealtimeLogLevel");
        MDC.remove(LogRealtimeFilter.REALTIME_LOG_LEVEL);
    }
}
