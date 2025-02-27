package com.myprj.sample.api.masking;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MaskingContext {

    /**
     * 마스킹 강제 ON/OFF 플래그(?_mask=)가 파라미터에 존재하는지 여부
     *
     * <pre>
     *     - true : _mask 파라미터 존재
     *     - false : _mask 파라미터 없음(default)
     * </pre>
     */
    @Builder.Default
    private Boolean paramMaskFlagExists = false;

    /**
     * 마스킹 강제 ON/OFF 플래그(?_mask=)의 값
     *
     * <pre>
     *     마스킹 해제 권한보다도 더 우선순위가 높다.
     *     - true : 마스킹처리(ON) : default
     *          - 예시 : 마스킹 해제 권한이 있는 사용자일지라도 {@code @Masking}  어노테이션이 있는 멤버변수들은 강제로 마스킹 처리함
     *     - false : 마스킹처리(OFF)
     *          - 예시 : {@code @Masking}  어노테이션이 있는 멤버변수들은 강제로 마스킹 cjfl dksgdma
     * </pre>
     */
    @Builder.Default
    private Boolean paramMaskFlag = true;

    /**
     * 사용자별 마스킹 해제 권한 여부
     *
     * <pre>
     *     - true : 마스킹 해제 권한 존재 (즉, 마스킹 처리 않음)
     *     - false : 마스킹 해제 권한 업음 (즉, 마스킹 처리 : default)
     * </pre>
     */
    @Builder.Default
    private Boolean userUnmaskAuthority = false;
}
