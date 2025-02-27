package com.myprj.sample.fwk.online;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ResponseContext {
    public static final String MESSAGE_CODE;
    public static final String MESSAGE_CONTENT;

    static {
        MESSAGE_CODE = "First_Message_code";
        MESSAGE_CONTENT = "First_Message_Content";
    }

    private String messageCode;

    private Object[] parameters;

    private Exception exception;

    private boolean error;

    private String resolvedMessage;
}
