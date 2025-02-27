package com.myprj.sample.api.data.crypto;

import com.myprj.sample.api.data.crypto.CryptoConverter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 암호화 변환기 설정
 *
 * <pre>
 * 암호화 변환기들, 기본 암호화 변환기를 설정하고 조회할 수 있습니다.
 * </pre>
 *
 * @see CryptoConverter
 */
public class CryptoPolicy {

    /**
     * 암호화 변환기들
     */
    private final Map<String, CryptoConverter> converters = new LinkedHashMap<>();

    /**
     * 기본 암호화 변환기
     */
    @Getter
    @Setter
    private CryptoConverter defaultConverter;

    /**
     * 암호화 변환기를 등록
     *
     * @param name      암호화 변환기 이름
     * @param converter 등록할 암호화 변환기
     */
    public void add(String name, CryptoConverter converter) {
        Assert.hasLength(name, "변환기 이름이 없습니다.");
        Assert.notNull(converter, "변환기가 없습니다.");

        converters.put(name, converter);
    }

    /**
     * 암호화 변환기를 조회
     *
     * @param name 조회할 암호화 변환기 이름
     * @return 이름에 해당하는 암호화 변환기. 없는 경우 {@code null}
     */
    public CryptoConverter get(String name) {
        Assert.hasLength(name, "변환기 이름이 없습니다.");

        return converters.get(name);
    }

}
