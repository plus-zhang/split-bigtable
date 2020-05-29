package io.banjuer.config.em;

public enum SelectJobStatus {

    /**
     *
     */
    prepare(10),
    map(11),
    reduce(12),
    cache(13),
    error(30);

    private Integer code;
    
    SelectJobStatus(Integer code) {
        this.code = code;
    }

}
