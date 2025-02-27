package com.myprj.sample.api.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import com.myprj.sample.api.aop.LoggingAspect;
import org.slf4j.MDC;
import org.slf4j.Marker;

public class LogDynamicFilter extends TurboFilter {
    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {

        String dynamicLogLevel = MDC.get(LoggingAspect.DYNAMIC_LOG_LEVEL);

        if(dynamicLogLevel != null) {
            Level newLevel = Level.toLevel(dynamicLogLevel);

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
