package com.myprj.sample.api.data.crypto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 암호화 서비스
 */
@RequiredArgsConstructor
@Slf4j
public class CryptoService {

    private final CryptoPolicy cryptoPolicy;

    /**
     * 문자열을 암호화
     *
     * @param source 원본 문자열
     * @return 암호화된 문자열
     */
    public String encrypt(String source) {
        return encrypt(source, null);
    }

    /**
     * 문자열을 복호화
     *
     * @param source 원본 문자열
     * @return 복호화된 문자열
     */
    public String decrypt(String source) {
        return decrypt(source, null);
    }

    /**
     * 문자열을 암호화
     *
     * @param source        원본 문자열
     * @param converterName 변환기 이름
     * @return 변환기로 암호화한 문자열
     */
    public String encrypt(String source, String converterName) {
        CryptoConverter cryptoConverter = getCryptoConverter(converterName);

        return cryptoConverter.encrypt(source);
    }

    /**
     * 문자열을 복호화
     *
     * @param source        원본 문자열
     * @param converterName 변환기 이름
     * @return 변환기로 복호화한 문자열
     */
    public String decrypt(String source, String converterName) {
        CryptoConverter cryptoConverter = getCryptoConverter(converterName);
        if (!cryptoConverter.supportDecrypt()) {
            return source;
        }

        return cryptoConverter.decrypt(source);
    }

    protected String convert(String source, String name, Type type) {
        Assert.notNull(name, "변환 형식이 필요합니다.");

        if (type == Type.ENCRYPT) {
            return encrypt(source, name);
        }
        if (type == Type.DECRYPT) {
            return decrypt(source, name);
        }

        throw new IllegalStateException("Unsupported type: " + type);
    }

    /**
     * 암복호화를 해야 할 객체인지 판단
     *
     * @param source 검사할 대상
     * @return 암복호화를 해야 할 객체이면 true, 그렇지 않으면 false
     */
    public boolean shouldCrypto(Object source) {
        if (source == null) {
            return false;
        }

        Class<?> klass = source.getClass();
        Set<Field> fields = getAllFields(klass);

        return fields.stream()
                .anyMatch(f -> f.getAnnotation(Crypto.class) != null || f.getAnnotation(CryptoObject.class) != null);
    }

    /**
     * 객체 암호화
     *
     * @param source 대상 객체
     */
    public void encrypt(Object source) {
        convert(source, Type.ENCRYPT);
    }

    /**
     * 객체 복호화
     *
     * @param source 대상 객체
     */
    public void decrypt(Object source) {
        convert(source, Type.DECRYPT);
    }

    /**
     * 객체를 변환(암복호화)
     *
     * @param source 원본 객체
     * @param <T>    대상 타입
     * @return 변환(암복호화)된 객체
     */
    @SneakyThrows
    protected <T> T convert(T source, Type type) {
        if (source == null) {
            return null;
        }

        if (source instanceof Collection<?> collection) {
            collection.forEach(e -> convert(e, type));
        }

        Set<Field> fields = getAllFields(source.getClass());
        for (Field field : fields) {
            Crypto crypto = null;
            CryptoObject cryptoObject;

            if (field.getType().isAssignableFrom(String.class)) {
                crypto = field.getAnnotation(Crypto.class);
                if (crypto == null) {
                    continue;
                }
            } else {
                cryptoObject = field.getAnnotation(CryptoObject.class);
                if (cryptoObject == null) {
                    continue;
                }
            }

            field.setAccessible(true);
            Object value = field.get(source);
            if (value == null) {
                continue;
            }

            Object convertedValue;
            if (value instanceof String stringValue) {
                if (crypto == null) {
                    continue;
                }

                String name = crypto.name();
                convertedValue = convert(stringValue, name, type);
            } else {
                convertedValue = convert(value, type);
            }
            field.set(source, convertedValue);
        }

        return source;
    }

    /**
     * 클래스의 필드를 반환
     *
     * <pre>
     * 부모 클래스까지 포함한 모든 필드를 얻습니다.
     * </pre>
     *
     * @param sourceClass 원본 클래스
     * @return 필드 집합
     */
    protected Set<Field> getAllFields(Class<?> sourceClass) {
        Set<Field> fields = new LinkedHashSet<>();
        for (Class<?> klass = sourceClass; klass != null; klass = klass.getSuperclass()) {
            fields.addAll(Arrays.asList(klass.getDeclaredFields()));
        }

        return fields;
    }

    protected CryptoConverter getCryptoConverter(String name) {
        CryptoConverter cryptoConverter;
        if (!StringUtils.hasLength(name)) {
            cryptoConverter = cryptoPolicy.getDefaultConverter();
        } else {
            cryptoConverter = cryptoPolicy.get(name);
        }

        Assert.notNull(cryptoConverter, "변환기를 찾을 수 없습니다.");

        return cryptoConverter;
    }

    protected enum Type {
        ENCRYPT, DECRYPT,
    }

}
