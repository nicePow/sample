package com.myprj.sample.api.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.MDC;
import org.slf4j.Marker;

/**
 * 실시간 로그레벨 필터
 */
public class LogRealtimeFilter extends TurboFilter {

    public static final String REALTIME_LOG_LEVEL = "REALTIME_LOG_LEVEL";

    /**
     * LopLevel 결정
     *
     * @param marker 마커
     * @param logger 로거
     * @param level 로그레벨
     * @param format 포멧
     * @param params 파라미터
     * @param t throwable 에러
     * @return 로그여부
     *
     * <pre>
     * TurboFilter의 주요 옵션 및 설명a
     *  - ACCEPT: 로그 이벤트를 허용합니다. 필터가 이 값을 반환하면 해당 로그 이벤트는 로그백의 다음 단계로 전달됩니다.
     *  - DENY: 로그 이벤트를 거부합니다. 필터가 이 값을 반환하면 해당 로그 이벤트는 로그백의 다음 단계로 전달되지 않습니다.
     *  - NEUTRAL: 로그 이벤트를 허용하거나 거부하지 않습니다. 필터가 이 값을 반환하면 다음 필터로 이벤트가 전달됩니다1.
     * </pre>
     */
    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        if(logger != null){
            if("ABC".equals(logger.getName()) || "DEF".equals(logger.getName())){
                return FilterReply.ACCEPT;
            }
        }

        String realtimeLogLevel = MDC.get(REALTIME_LOG_LEVEL);

        if(realtimeLogLevel != null) {
            Level newLevel = Level.toLevel(realtimeLogLevel);
            if(level.isGreaterOrEqual(newLevel)){
                return FilterReply.ACCEPT;
            } else {
                return FilterReply.DENY;
            }
        } else {
            return FilterReply.NEUTRAL;
        }


    }
}
