package com.myprj.sample.fwk.requesttrace;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.UUID;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Slf4j
public class RequestTraceContext {

    public static final NumberFormat DIGITS_2;
    public static final int GUID_LENGTH;
    public static final int GUID_SEQUENCE_LENGTH;
    public static final int GUID_SEQUENCE_START;

    static {
        DIGITS_2 = new DecimalFormat("00");
        GUID_LENGTH = 36;
        GUID_SEQUENCE_LENGTH = 2;
        GUID_SEQUENCE_START = 1;
    }

    /**
     * 고유번호
     */
    private String guid;

    /**
     * 고유번호 순번
     */
    @Builder.Default
    private int sequence = GUID_SEQUENCE_START;

    /**
     * 시스템코드
     */
    private String systemCode;


    /**
     * 화면 아이디
     *
     */
    private String userId;

    /**
     * 화면아이디
     */
    private String screenId;

    /**
     * 기능코드
     */
    private String functionCode;

    /**
     * journal log용 입력값
     */
    private String input;

    private String systemToken;

    // System API Key(시스템간 호출일때 사용)
    private String systemApiKey;

    /**
     * guid를 넘겨 받으면 값을 사용 , 없으면 guid생성
     * @param source
     * @return
     */
    public static String checkGuidOrCreate(String source){
        if((source == null) || (source.length() != GUID_LENGTH)){
            return generateGuid();
        }
        return source;
    }


    /**
     * GUID생성
     * @return
     */
    public static String generateGuid() { return UUID.randomUUID().toString(); }

    /**
     * 거래 추적 수행 일련번호를 가져온다.
     * @param source 헤더에서 넘어온 값
     * @return integer형 거래추적 순번
     */
    public static int checkSeqOrCreate(String source){
        if((source == null) || (source.length() != GUID_SEQUENCE_LENGTH)){
            return GUID_SEQUENCE_START;
        }

        try{
            return DIGITS_2.parse(source).intValue();
        } catch (ParseException e) {

            log.error("guid parse error : ", e);
            return GUID_SEQUENCE_START;
        }
    }

    /**
     * 거래추적 순번을 증가한다.
     * @return 증가된 순번값
     */
    public int increaseSequence() {
        sequence++;
        
        return sequence;
    }

    public String getGuidSequence() {return DIGITS_2.format(sequence);}

    public String getFunctionType(){
        if(StringUtils.hasLength(functionCode)){
            return functionCode.substring(0,1);
        } else {
            return "";
        }
    }

    /**
     * 다음 거래추적 순번 문자 포맷으로 변환(증가없이 다음 값만 조회)
     * @return ex : 01, 02, 03
     */
    public String getNextGuidSequence(){return DIGITS_2.format(sequence+1);}

    /**
     * 시스템 토큰 존재여부
     */
    public boolean hasSystemApiKey(){return StringUtils.hasLength(systemApiKey);}

    /**
     * 순분 문자 포멧으로 변환
     * @param seq
     * @return
     */
    public static String getSqeuenceForm(int seq){return DIGITS_2.format(seq);}

}
