package handler.http;

//
// Author: Nick Guy
// Created at: 25/08/2017
//

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class UrlEncoder {

    public static String UrlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static String UrlEncodeUTF8(Map<?, ?> map) {
        final StringBuilder sb = new StringBuilder();
        map.forEach((k, v) -> {
            if(sb.length() > 0)
                sb.append("&");

            sb.append(
              String.format("%s=%s",
                UrlEncodeUTF8(k.toString()),
                UrlEncodeUTF8(v.toString())
              )
            );

        });
        return sb.toString();
    }

}
