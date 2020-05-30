package io.banjuer.util;

public class SqlUtils {

    public static String getWhereField(String field, String fieldType) {
        String ret;
        switch (fieldType.toLowerCase()) {
            case "varchar":
            case "datetime":
                ret = "'" + field + "'";
                break;
            default:
                ret = field;
                break;
        }
        return ret;
    }

}
