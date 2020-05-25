package io.banjuer.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于数据表字段与java驼峰命名相互转换
 * @author guochengsen
 */
public class FieldConvertUtil
{
    /**
     * 驼峰集合转数据字段集合
     */
    public static List<String> field2Column(List<String> fields)
    {
        if (EmptyUtils.isEmpty(fields))
            return null;
        List<String> columns = new ArrayList<>(fields.size());
        fields.forEach(f -> columns.add(field2Column(f)));
        return columns;
    }

    /**
     * 表列名转驼峰属性
     */
    public static String column2Field(String column)
    {
        if (EmptyUtils.isEmpty(column))
            return column;
        StringBuilder field = new StringBuilder();
        String[] words = column.split("_");
        field.append(words[0]);
        for (int i = 1; i < words.length; i++) {
            field.append(upperFirst(words[i]));
        }
        return field.toString();
    }

    /**
     * 驼峰转表名
     */
    public static String field2Column(String field)
    {
        if (EmptyUtils.isEmpty(field))
            return field;
        StringBuilder sb = new StringBuilder();
        char[] chars = field.toCharArray();
        sb.append(CharacterUtils.toLower(chars[0]));
        for (int i = 1; i < chars.length; i++) {
            char c = chars[i];
            if (CharacterUtils.isUpper(c)) {
                sb.append('_');
                c = CharacterUtils.toLower(c);
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static String upperFirst(String word)
    {
        char[] cs = word.toCharArray();
        if (cs.length == 0)
            return "";
        cs[0] = CharacterUtils.toUpper(cs[0]);
        return new String(cs);
    }

    public static void main(String[] args)
    {
        System.out.println(upperFirst("id"));
        System.out.println(field2Column("tl.tagId"));
    }

}
