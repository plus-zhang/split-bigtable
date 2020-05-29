package io.banjuer.config.em;

public enum DataStatus {
    read_only(10),
    writ(11),
    valid(20),
    invalid(30);

    private Integer code;

    DataStatus(Integer code) {
        this.code = code;
    }
}
