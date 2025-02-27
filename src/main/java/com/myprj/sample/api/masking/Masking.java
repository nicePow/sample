package com.myprj.sample.api.masking;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 마스킹 어노테이션
 *
 * <pre>
 *     변수 상단에 {@code @Masking}  어노테이션을 적어, 마스킹 대상임을 나타낸다.
 * </pre>
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@JacksonAnnotationsInside
@JsonSerialize(using = MaskingSerializer.class)
public @interface Masking {

    /**
     * 마스킹방식
     */
    MaskingType value();
}
