package io.banjuer.util;

public class StringUtils {

    public static void trim(String[] strs){
        if (EmptyUtils.isEmpty(strs))
            return;
        for (int i = 0; i < strs.length; i++) {
            strs[i] = strs[i].trim();
        }
    }
}
