package io.banjuer.config.em;

public enum JobStatus {

    /**
     *
     */
    start(10),
    running(11),
    success(20),
    error(30);

    private Integer code;

    JobStatus(Integer code) {
        this.code = code;
    }
}
