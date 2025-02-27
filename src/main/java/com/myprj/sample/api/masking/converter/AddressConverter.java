package com.myprj.sample.api.masking.converter;

import com.myprj.sample.api.masking.MaskingConverter;

/**
 * 주소마스킹
 * 
 * <pre>
 *     주소 문자열 내 숫자만 마스킹
 * </pre>
 */
public class AddressConverter extends MaskingConverter {


    @Override
    protected String maskInternal(String source) {
        return source.replaceAll("\\d", MASK);
    }
}
