package com.myprj.sample.api.masking.converter;


import com.myprj.sample.api.masking.MaskingConverter;

public class NameConverter extends MaskingConverter {
    private static final int MASKING_INDEX = 1;

    /**
     * 마스킹 처리
     *
     * <pre>
     *     MASKING_INDEX +1 보다 작으면 마스킹 안함
     *     MASKING_INDEX 문자만 마스킹 대상
     * </pre>
     * @param source 원본문자열
     * @return 마스킹된 문자열
     */
    @Override
    protected String maskInternal(String source) {
        if(source.length() < MASKING_INDEX + 1){
            return source;
        }
        if(source.length() > MASKING_INDEX + 1){
            String val1 = source.substring(0, MASKING_INDEX);
            String val2 = source.substring(MASKING_INDEX+1);
            return val1 + MASK + val2;
        }

        String value1 = source.substring(0,MASKING_INDEX);
        return value1 + MASK;
    }
}
