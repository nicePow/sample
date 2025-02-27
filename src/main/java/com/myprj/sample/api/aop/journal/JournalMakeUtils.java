package com.myprj.sample.api.aop.journal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.myprj.sample.api.data.crypto.CryptoService;
import com.myprj.sample.api.masking.Masking;
import com.myprj.sample.fwk.requesttrace.RequestTraceContext;
import com.myprj.sample.fwk.requesttrace.RequestTraceContextHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.support.PageJacksonModule;
import org.springframework.cloud.openfeign.support.SortJacksonModule;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class JournalMakeUtils {

    private static final ObjectMapper mapper;

    public static final String LOG_TYPE_REQUEST;

    public static final String LOG_TYPE_RESPONSE;

    private final static Logger LOG_JOURNAL;

    static {
        LOG_JOURNAL = LoggerFactory.getLogger("JOURNAL");

        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        //암호화 한 데이터를 다시 마스킹 하는 것을 방지하기 위해 Masking어노테이션이 동작하지 않도록처리
        /*
        mapper.setAnnotationIntrospector((JacksonAnnotationIntrospector) findSerializer(a) -> {
           Masking masking = a.getAnnotation(Masking.class);
           if(masking != null){
               // Masking 어노테이션은 무시
               return null;
           } else {
               return super.findSerializer(a);
           }
        });
        */

        // page 직렬화
        mapper.registerModule(new PageJacksonModule());
        mapper.registerModule(new SortJacksonModule());

        LOG_TYPE_REQUEST = "REQUEST";
        LOG_TYPE_RESPONSE = "RESPONSE";
    }

    /**
     *
     * @param r 응답 Object
     * @param cryptoService cryptoService
     * @param saveResponsePayload
     */
    @SneakyThrows
    public static void journalSuccess(Object r, CryptoService cryptoService, boolean saveResponsePayload){
        JournalLogDto journalLog = makeBaseDto();
        if(journalLog == null){
            return;
        }

        if(r == null){
            journalLog = journalLog.toBuilder()
                    .logType(LOG_TYPE_RESPONSE)
                    .response(
                            JournalLogDto.JournalLogResponse.builder()
                                    .status(String.valueOf(HttpStatus.OK.value()))
                                    .build()
                    )
                    .build();
        } else {
            String encryptedBody;
            if(saveResponsePayload){
                String body = mapper.writeValueAsString(r);
                Object obj = mapper.readValue(body, r.getClass());
                encryptObjectByAnnotation(obj, cryptoService);
                encryptedBody = mapper.writeValueAsString(obj);
            } else {
                encryptedBody = "";
            }

            journalLog = journalLog.toBuilder()
                    .logType(LOG_TYPE_RESPONSE)
                    .guidSeq(RequestTraceContextHandler.getRequestTraceContext().getNextGuidSequence())
                    .responsePayload(encryptedBody)
                    .response(
                            JournalLogDto.JournalLogResponse.builder()
                                    .status(String.valueOf(HttpStatus.OK.value()))
                                    .build()
                    )
                    .build();
        }
    }

    public static void journalFail(String code, String desc) {
        JournalLogDto journalLog = makeBaseDto();
        if(journalLog == null) {
            return;
        }

//        journalLog = journalLog.toBuilder()
//                .
    }

    private static JournalLogDto makeBaseDto(){
        RequestTraceContext rtCtx = RequestTraceContextHandler.getRequestTraceContext();
        if(rtCtx == null){
            return null;
        }

        ServletRequestAttributes servletRequest = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(servletRequest == null){
            return null;
        }

        HttpServletRequest request = servletRequest.getRequest();
        String url = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String method = request.getMethod();

        String userIp = request.getHeader("x-forwarded-for");
        if(!StringUtils.hasLength(userIp)) {
            userIp = request.getRemoteAddr();
        }

        return JournalLogDto.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                .guid(rtCtx.getGuid())
                .guidSeq(rtCtx.getGuidSequence())
                .sysCode(rtCtx.getSystemCode())
                .screenId(rtCtx.getScreenId())
                .functionCode(rtCtx.getFunctionCode())
                .systemToken(rtCtx.getSystemToken())
                .url(url)
                .method(method)
                .logType(LOG_TYPE_REQUEST)
                .requestPaylosd(rtCtx.getInput())
                .host(JournalLogDto.JournalLogHost.builder()
                        .ip(request.getLocalAddr())
                        .name(request.getLocalName())
                        .build()
                )
                .caller(JournalLogDto.JournalLogCaller.builder()
                        .name(rtCtx.getSystemCode())
                        .ip(request.getRemoteAddr())
                        .build()
                )
                .user(JournalLogDto.JournalLogUser.builder()
                        .id(rtCtx.getUserId())
                        .ip(userIp)
                        .build()
                )
                .build();
    }

    /**
     * 재귀적으로 Masking 어노테이션 찾기
     * @param obj dto
     * @param cryptoService cryptoService
     */
    @SneakyThrows
    private static void encryptObjectByAnnotation(Object obj, CryptoService cryptoService){
        if(obj == null){
            return;
        }

        if(obj.getClass().isPrimitive() || obj.getClass().getName().startsWith("java")){
            return;
        }

        Field[] fields = obj.getClass().getDeclaredFields();
        for(Field field : fields){
            field.setAccessible(true);

            Object fieldValue = field.get(obj);

            if(field.isAnnotationPresent(Masking.class)) {
                Masking info = field.getAnnotation(Masking.class);

                if(fieldValue instanceof String originValue) {
                    if(!StringUtils.hasLength(originValue)){
                        continue;
                    }

                    String encValue;
                    if(cryptoService != null) {
                        try {
                            encValue = cryptoService.encrypt(originValue);
                        } catch (Exception e){
                            log.error("value encryption error : ", e);
                            encValue = "<encryption-exception-occurred>"; // 암호화가 동작할때만 value를 남긴다.
                        }
                    } else {
                        encValue = "<encryption-failed>"; // 암호화가 동작할때만 value를 남긴다.
                    }
                    log.trace("Masking Annotation member = type: {}, name: {}, value: {}", info.value(), field, field.get(obj));
                    field.set(obj, encValue);
                }
            }

        }

    }
//    private static void encryptObjectByAnnotation(Object obj){
//
//    }

    public static void journalRequtest(){
        JournalLogDto journalLog = makeBaseDto();
        if(journalLog == null){
            return;
        }

        try{
            String journal = mapper.writeValueAsString(journalLog);
            LOG_JOURNAL.info(journal);
        }catch (JsonProcessingException e){
            log.error("JsonProcessingException: ",e);
            throw new RuntimeException(e);
        }
    }
}
