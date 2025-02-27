package com.framework.first.api.aop.journal;

import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class JournalLogDto {
    private String timestamp;
    private String guid;

    private String guidSeq;

    private String sysCode;

    private String screenId;

    private String functionCode;

    private String systemToken;

    private String url;

    private String method;

    private String logType;

    private String requestPaylosd;

    private String responsePayload;

    private JournalLogCaller caller;

    private JournalLogHost host;

    private JournalLogResponse response;

    private JournalLogUser user;


    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class JournalLogCaller {
        private String name;
        private String ip;
    }

    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class JournalLogHost {
        private String name;
        private String ip;
    }

    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class JournalLogResponse {
        private String status;
        private String code;
        private String desc;
    }

    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class JournalLogUser{
        private String id;
        private String ip;
    }
}
