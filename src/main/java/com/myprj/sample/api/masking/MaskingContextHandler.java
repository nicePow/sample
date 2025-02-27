package com.myprj.sample.api.masking;

/**
 * 마스킹 컨텍스트 핸들러
 */
public class MaskingContextHandler {
    static ThreadLocal<MaskingContext> MASK = new ThreadLocal<>();

    /**
     * 컨텍스트 마스킹 여부 설정
     * @param mask 마스킹 여부
     */
    public static void setMask(MaskingContext mask) {MASK.set(mask);}

    /**
     * 컨텍스트 마스킹 여부 조회
     * @return 마스킹 여부
     */
    public static MaskingContext getMask() { return MASK.get(); }

    /**
     * 컨텍스트 정리
     */
    public static void clearContext() { MASK.remove(); }

    /**
     * 마스킹 처리할지 여부를 판단
     * @return 마스킹 처리여부(true : 마스킹 처리 ,  false : 마스칭 처리 않음)
     */
    public static boolean shouldMask() {
        MaskingContext mask = getMask();

        if(mask == null) {
            return true;
        }

        if(mask.getParamMaskFlagExists()){
            // 마스킹 여부 강제 설정이 있으면 이를 따름
            return mask.getParamMaskFlag();
        }

        return !mask.getUserUnmaskAuthority();
    }

}
