package handler.utils;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class StringUtils {

    protected static final String ACTIONAL_DELIMITERS = " '-/";

    public static String ToDisplayCase(String str) {
        StringBuilder sb = new StringBuilder();
        boolean capNext = true;

        char[] chars = str.toCharArray();
        for (char c : chars) {
            c = capNext ?
                    Character.toUpperCase(c) :
                    Character.toLowerCase(c);
            sb.append(c);
            capNext = ACTIONAL_DELIMITERS.indexOf(c) >= 0;
        }
        return sb.toString();
    }

    public static String Trim(String string, char... trimTargets) {
        return Trim(string, MakeString(trimTargets));
    }

    public static String Trim(String string, String trimTargets) {

        char[] value = string.toCharArray();

        int len = value.length;
        int st = 0;
        char[] val = value;    /* avoid getfield opcode */

        while ((st < len) && (trimTargets.contains(val[st]+""))) {
            st++;
        }
        while ((st < len) && (trimTargets.contains(val[len - 1]+""))) {
            len--;
        }
        return ((st > 0) || (len < value.length)) ? string.substring(st, len) : string;
    }

    public static String MakeString(char... chars) {
        StringBuilder s = new StringBuilder();
        for (char aChar : chars)
            s.append(aChar);
        return s.toString();
    }

    public static UUID NewUUID() {
        return UUID.randomUUID();
    }

    public static String NewUUIDString() {
        return NewUUID().toString();
    }

    public static <T> int IndexOf(Collection<T> items, T item) {
        if(items instanceof List)
            return ((List) items).indexOf(item);

        int index = 0;
        for (T o : items) {
            if(item.equals(o)) return index;
            index++;
        }
        return -1;
    }

}
