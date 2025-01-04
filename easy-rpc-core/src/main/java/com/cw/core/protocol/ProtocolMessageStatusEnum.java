package com.cw.core.protocol;

import lombok.Getter;

@Getter
public enum ProtocolMessageStatusEnum {
    OK("ok",20),
    BAD_REQUEST("bad request",40),
    BAD_RESPONSE("bad response",50),
    ;


    private final String text;

    private final int value;

    ProtocolMessageStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据value获取枚举
     *
     * @param value
     * @return
     */
    public static ProtocolMessageStatusEnum getEnumByValue(int value) {
        for (ProtocolMessageStatusEnum protocolMessageStatusEnum : ProtocolMessageStatusEnum.values()) {
            if (protocolMessageStatusEnum.value == value) {
                return protocolMessageStatusEnum;
            }
        }
        return null;
    }
}
