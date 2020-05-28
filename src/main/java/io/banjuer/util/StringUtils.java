package io.banjuer.util;

public class StringUtils {

    public static void trim(String[] strs){
        if (EmptyUtils.isEmpty(strs))
            return;
        for (int i = 0; i < strs.length; i++) {
            strs[i] = strs[i].trim();
        }
    }

    public static String join(String[] strs){
        if (EmptyUtils.isEmpty(strs))
            return "";
        StringBuilder sb = new StringBuilder();
        for (String str : strs) {
            String s = str == null ? "" : str;
            sb.append(' ').append(s);
        }
        return sb.toString().trim();
    }

}
