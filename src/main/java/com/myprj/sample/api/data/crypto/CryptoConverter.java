package com.myprj.sample.api.data.crypto;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.function.Function;

/**
 * 암호화 변환기
 *
 * @see AttributeConverter
 * @see CryptoPolicy
 */
@Converter
public interface CryptoConverter extends AttributeConverter<String, String> {

    default String convertToDatabaseColumn(String attribute) {
        return encrypt(attribute);
    }

    default String convertToEntityAttribute(String dbData) {
        return decrypt(dbData);
    }

    /**
     * 문자열을 암호화
     *
     * @param source 원본 문자열
     * @return 암호화한 문자열
     */
    String encrypt(String source);

    /**
     * 문자열을 복호화
     *
     * @param source 원본 문자열
     * @return 복호화한 문자열
     */
    String decrypt(String source);

    /**
     * 복호화할지 판단
     *
     * <pre>
     * 양방향 암복호화인 경우 {@code true}를, 단방향 암호화인 경우 {@code false}를 반환해야 합니다.
     * </pre>
     *
     * @return 복호화 처리 여부
     */
    default boolean supportDecrypt() {
        return true;
    }

    /**
     * 암호화 변환기를 만들어 반환
     *
     * @param encryptor 암호화 함수
     * @param decryptor 복호화 함수
     * @return 만들어진 암호화 변환기
     */
    static CryptoConverter of(Function<String, String> encryptor, Function<String, String> decryptor) {
        return new CryptoConverter() {
            @Override
            public String encrypt(String source) {
                return encryptor.apply(source);
            }

            @Override
            public String decrypt(String source) {
                return decryptor.apply(source);
            }

            @Override
            public boolean supportDecrypt() {
                // 복호화 함수가 있으면 복호화하는 것으로,
                // 없으면 복호하하지 않는 것으로 간주합니다.
                return decryptor != null;
            }
        };
    }

}
