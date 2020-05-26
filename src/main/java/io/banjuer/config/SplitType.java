package io.banjuer.config;

public enum SplitType {
    /**
     * 切表方式:
     * RANGE: 按字段所有值
     * HASH: 按字段散列
     */
    RANGE,
    HASH
}
