package com.myprj.sample.api.data.crypto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 암호화 대상 문자열임을 표시하는 어노테이션
 *
 * <pre>
 * 문자열 필드에 붙여서 해당 필드가 암호화 대상 문자열임을 나타냅니다.
 *
 * {@code
 * @SuperBuilder(toBuilder = true)
 * @NoArgsConstructor
 * @AllArgsConstructor
 * @Getter
 * @Setter
 * @ToString
 * public class PrivacyData {
 *
 *     private String name;
 *
 *     private int age;
 *
 *     // 필드 값 자체가 암호화 대상 문자열인 경우입니다.
 *     // 암호화 변환기 이름을 특정하지 않으면 기본 암호화 변환기가 선택됩니다.
 *     @Crypto
 *     private String address;
 *
 *     // 필드 값 자체가 암호화 대상 문자열인 경우입니다.
 *     // name 속성값을 통해 암호화 변환기를 특정할 수 있습니다.
 *     @Crypto(name = "hash")
 *     private String password;
 *
 *     // 필드의 클래스가 암화화 대상인 경우입니다.
 *     @CryptoObject
 *     private PrivacyData friend;
 *
 *     // 컬렉션의 요소들이 암호화 대상인 경우입니다.
 *     @CryptoObject
 *     private List<PrivacyData> friends;
 *
 * }
 * }
 * </pre>
 *
 * @see CryptoObject
 * @see CryptoService
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Crypto {

    /**
     * 암복호화 변환기 이름
     *
     * <pre>
     * 없는 경우는 기본 암복호화 변환기로 간주합니다.
     * </pre>
     */
    String name() default "";

}
