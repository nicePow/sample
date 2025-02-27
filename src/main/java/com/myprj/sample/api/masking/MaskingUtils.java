package com.myprj.sample.api.masking;

import com.myprj.sample.api.masking.converter.AddressConverter;
import com.myprj.sample.api.masking.converter.NameConverter;
import com.myprj.sample.api.masking.converter.PhoneNumberConverter;
import com.myprj.sample.api.masking.converter.RegNumConverter;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.myprj.sample.api.masking.MaskingType.*;

@Slf4j
public abstract class MaskingUtils {
    private final static Map<MaskingType, MaskingConverter> converters;

    static {
        converters = new LinkedHashMap<>();
        converters.put(REG_NUM, new RegNumConverter());
        converters.put(NAME,new NameConverter());
        converters.put(PHONE_NUMBER,new PhoneNumberConverter());
        converters.put(ADDRESS,new AddressConverter());
    }

    /**
     * 마스킹 처리
     * @param source 마스킹할 문자열
     * @param type 마스킹 타입
     * @return 마스킹 타입별 마스킹된 결과
     */
    public static String mask(String source, MaskingType type) {
        if (type == null){
            throw new RuntimeException("type값이 없습니다.");
        }

        MaskingConverter converter = converters.get(type);
        if(converter == null){
            throw new RuntimeException(String.format("type값에 해당하는 마스킹 변환기가 없습니다. type=%s",type));
        }

        return converter.mask(source);
    }
}
