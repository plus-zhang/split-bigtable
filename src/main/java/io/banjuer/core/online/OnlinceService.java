package io.banjuer.core.online;

import io.banjuer.config.em.SqlType;
import io.banjuer.core.BaseService;
import io.banjuer.core.SqlParser;
import io.banjuer.web.entity.BaseResponse;
import org.springframework.stereotype.Service;

@Service
public class OnlinceService extends BaseService {

    public BaseResponse execueSql(String sql) {
        SqlParser parse = SqlParser.parse(sql);
        SqlType sqlType = parse.getSqlType();
        return switch (sqlType) {
            case update -> doUpdate(parse);
            case insert -> doInsert(parse);
            case select -> doSelect(parse);
        };
    }

    private BaseResponse doSelect(SqlParser parse) {
        return null;
    }

    private BaseResponse doInsert(SqlParser parse) {
        return null;
    }

    private BaseResponse doUpdate(SqlParser parse) {
        return null;
    }

    @Override
    public BaseResponse<Double> getProgress(String key) {
        return null;
    }
}
