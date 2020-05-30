package io.banjuer.core.offline;

import io.banjuer.config.ProjectConst;
import io.banjuer.config.em.SplitType;
import io.banjuer.core.online.CommonService;
import io.banjuer.core.work.SelectWorker;
import io.banjuer.exception.ImportException;
import io.banjuer.helper.JdbcHelper;
import io.banjuer.helper.MySQLJdbcTemplate;
import io.banjuer.util.SqlUtils;
import io.banjuer.web.entity.BaseResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class ImportService {

    ExecutorService executor = new ThreadPoolExecutor(0, 32, 30, TimeUnit.MINUTES, new ArrayBlockingQueue<>(64));

    @Autowired
    private CommonService commonService;

    public BaseResponse<String> importIn(String dataBase, String source, String field, String[] values, String fieldType, SplitType splitType) throws InterruptedException {
        MySQLJdbcTemplate manageJdbc = JdbcHelper.INSTANCE.getManageJdbc();
        MySQLJdbcTemplate dataJdbc = JdbcHelper.INSTANCE.getManageDataJdbc();
        switch (splitType) {
            case RANGE :
                // 插入记录
                manageJdbc.executeUpdate("insert into " + ProjectConst.T_OL_IMPORT +
                        "(table_name,split_type,shard_field,shard_type,shard_value,plan,create_time) values(?,?,?,?,?,?,?)",
                        new Object[]{source, splitType.name(), field, fieldType, StringUtils.join(values, ","), 10, new Date()});
                // 并行导入
                CountDownLatch latch = new CountDownLatch(values.length);
                String fields = commonService.getTableFields(dataBase, source, field);
                for (String value : values) {
                    executor.submit(new SelectWorker(latch, dataJdbc, "create " + source + ProjectConst.SHARD_TABLE_TAIL + value + "(" +
                            "select " + fields + " from " + dataBase + "." + source + " where " + field + "=" + SqlUtils.getWhereField(field, fieldType) +
                            ")"));
                }
                latch.await();
                break;
            case HASH :
                // TODO
                break;
            default :
                throw new ImportException("unsupport split type, only support range");
        }
        return BaseResponse.success();
    }

}
