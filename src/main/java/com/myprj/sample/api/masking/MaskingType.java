package com.myprj.sample.api.masking;

import lombok.Getter;

/**
 * 마스킹 방식
 */
@Getter
public enum MaskingType {

    REG_NUM("REG_NUM","주민번호"),
    NAME("NAME", "이름"),
    PHONE_NUMBER("PHONE_NUMBER","핸드폰번호"),
    ADDRESS("ADDRESS","주소"),
    ;

    MaskingType(String code, String desc){
        this.code = code;
        this.desc = desc;
    }

    /**
     * 마스킹 코드
     */
    private final String code;

    /**
     * 마스킹 설명
     */
    private final String desc;

}
