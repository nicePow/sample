package com.myprj.sample.api.masking;

import io.micrometer.common.util.StringUtils;

/**
 * 추상 마스킹 변환기
 */
public abstract class MaskingConverter {
    /**
     * 마스킹시 교체될 문자
     */
    public static final String MASK;

    /**
     *  '-' 문자 제외패턴
     */
    public static final String EXCEPT_DASH;

    static {
        MASK = "*";
        EXCEPT_DASH = "[^\\-]";
    }

    /**
     * 마스킹 처리
     * @param source 원본문자열
     * @return 마스킹 처리된 문자열
     */
    public String mask(String source) {
        if(StringUtils.isEmpty(source)){
            return source;
        }
        return maskInternal(source);
    }

    protected abstract  String maskInternal(String source);
}
