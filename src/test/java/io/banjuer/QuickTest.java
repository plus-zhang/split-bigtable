package io.banjuer;

import io.banjuer.helper.JdbcHelper;
import io.banjuer.helper.MySQLJdbcTemplate;
import io.banjuer.util.ArrayUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class QuickTest {

    static MySQLJdbcTemplate template = JdbcHelper.INSTANCE.getMysqlJdbc("openblk699");

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(8);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 8; i++) {
            Query query = new Query();
            query.sql = "SELECT odr,AVG(wlFbc) FROM data_" + i +" WHERE bk=699 AND lyr=74 AND wl=298 AND odr=0 AND LN=0 AND pl=0";
            query.table = "data";
            query.latch = latch;
            query.template = template;
            new Thread(query).start();
        }
        latch.await();
        List<Object []> result = finalSelect("SELECT odr,AVG(wlFbc) FROM data_final GROUP BY odr");
        ArrayUtil.print(result.get(0));
        long end = System.currentTimeMillis();
        System.out.println("total cost:" + (end - start));
    }

    static List<Object []> finalSelect(String sql) {
        List<Object []> result = new LinkedList<>();
        template.executeQuery(sql, null, rs -> {
            int count = rs.getMetaData().getColumnCount();
            Object[] column = new Object[count];
            while (rs.next()) {
                for (int i = 1; i <= count; i++) {
                    column[i -1] = rs.getObject(i);
                }
                result.add(column);
            }
        });
        return result;
    }

    static class Query implements Runnable {

        String sql;
        MySQLJdbcTemplate template;
        CountDownLatch latch;
        String table;
        @Override
        public void run() {
            String finalSql = "insert into " + table + "_final " + sql;
            try {
                template.executeUpdate(finalSql, null);
            } finally {
                latch.countDown();
            }
            System.out.println(" ==========:" + latch.getCount());
            System.out.println("sql:" + sql + " finish");
        }
    }

}
