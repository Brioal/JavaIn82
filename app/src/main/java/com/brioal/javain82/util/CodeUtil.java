package com.brioal.javain82.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Brioal on 2016/7/12.
 */

public class CodeUtil {
    public static String encode(String s) {
        char[] c = s.toCharArray();
        for (int i = 0; i < c.length; i++) {
            Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
            Matcher m = p.matcher(c[i] + "");
            if (!m.find()) {
                c[i] ^= 0x99;
            }
        }
        return new String(c);
    }

}
