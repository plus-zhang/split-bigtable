package io.banjuer.core;

import io.banjuer.web.entity.BaseResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseService {

    public abstract BaseResponse<Double> getProgress(String key);

}
