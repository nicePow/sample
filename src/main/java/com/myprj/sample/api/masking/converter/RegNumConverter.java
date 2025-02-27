package com.myprj.sample.api.masking.converter;


import com.myprj.sample.api.masking.MaskingConverter;

public class RegNumConverter extends MaskingConverter {
    
    // 마스킹 대상 문자열 길이
    private static final int MASKING_LENGTH = 6;

    /**
     * 마스킹 처리
     * @param source
     * @return
     */
    @Override
    protected String maskInternal(String source) {
        if(source.length()<=MASKING_LENGTH){
            return source.replaceAll(EXCEPT_DASH,MASK);
        }
        
        int index = source.length() - MASKING_LENGTH;
        String val1 = source.substring(0, index);
        String val2 = source.substring(index);
        return val1 + val2.replaceAll(EXCEPT_DASH, MASK);
    }
}
