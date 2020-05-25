package io.banjuer.util;

public class ArrayUtil {

    public static void print(Object[] arr) {
        StringBuilder sb = new StringBuilder(arr.length);
        sb.append('[');
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i != arr.length - 1)
                sb.append(", ");
        }
        sb.append(']');
        System.out.println(sb);
    }

}
