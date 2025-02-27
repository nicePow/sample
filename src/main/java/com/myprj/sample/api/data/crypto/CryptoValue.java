package com.myprj.sample.api.data.crypto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 암호화 대상 값
 *
 * <pre>
 * 암호화 대상 값 자체가 문자열일 때 사용하기 위한 용도입니다.
 *
 * {@code
 *     CryptoValue addressValue = new CryptoValue(address);
 *     List<PrivacyData> list = privacyMapper.selectAllByAddress(addressValue);
 * }</pre>
 *
 * @see Crypto
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CryptoValue {

    /**
     * 값
     */
    @Crypto
    private String value;

    @Override
    public String toString() {
        return value;
    }

}
