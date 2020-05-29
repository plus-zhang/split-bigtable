package io.banjuer.config.em;

public enum SqlType {

    /**
     *
     */
    select(10),
    update(11),
    insert(12),
    delete(13);

    private Integer code;

    SqlType(Integer code) {
        this.code = code;
    }

}
