package handler.utils;

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

}
