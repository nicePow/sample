package com.myprj.sample.api.masking.converter;


import com.myprj.sample.api.masking.MaskingConverter;

import java.util.regex.Pattern;

public class PhoneNumberConverter extends MaskingConverter {

    private static final int MASK_INDEX;

    private static final int MASK_LENGTH;
    private static final Pattern ANY_CHAR_PATTERN;

    static {
        MASK_INDEX = 4;
        MASK_LENGTH = 4;
        ANY_CHAR_PATTERN = Pattern.compile(".");
    }

    @Override
    protected String maskInternal(String source) {
        // '-'가 있는경우
        String[] values = source.split("-");
        if(values.length >= 2) {
            String val1 = values[values.length-2];
            values[values.length - 2] = ANY_CHAR_PATTERN.matcher(val1).replaceAll(MASK);
            return String.join("-", values);
        }

        // '-'가 없는경우
        if(source.length() < MASK_INDEX){
            return source;
        }
        String val1 = source.substring(0,source.length() - MASK_INDEX);
        if(val1.length() <= MASK_LENGTH){
            val1= ANY_CHAR_PATTERN.matcher(val1).replaceAll(MASK);
        } else {
            String val11 = val1.substring(0,val1.length()-MASK_LENGTH);
            String val22 = val1.substring(val1.length() - MASK_LENGTH);
            val1= val11 + ANY_CHAR_PATTERN.matcher(val22).replaceAll(MASK);

        }
        String val2 = source.substring(source.length()-MASK_INDEX);
        return String.join("",val1, val2);
    }
}
