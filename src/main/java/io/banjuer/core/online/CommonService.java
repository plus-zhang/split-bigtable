package io.banjuer.core.online;

import io.banjuer.helper.JdbcHelper;
import io.banjuer.helper.MySQLJdbcTemplate;
import io.banjuer.util.EmptyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CommonService {

    private final Map<String, List<String>> fieldCache = new ConcurrentHashMap<>();

    public String getTableFields(String dataBase, String tableName, String withOut) {
        String key = dataBase + tableName + withOut;
        List<String> fields = fieldCache.get(key);
        if (EmptyUtils.isEmpty(fields)) {
            MySQLJdbcTemplate jdbc = JdbcHelper.INSTANCE.getUserDataJdbc(dataBase);
            List<String> tempFields = new LinkedList<>();
            jdbc.executeQuery("desc " + tableName, null, rs -> {
                while (rs.next()) {
                    String field = rs.getString(1);
                    if (!field.equals(withOut))
                        tempFields.add(field);
                }
            });
            fieldCache.put(key, tempFields);
        }
        return StringUtils.join(fieldCache.get(key), ",");
    }
}
