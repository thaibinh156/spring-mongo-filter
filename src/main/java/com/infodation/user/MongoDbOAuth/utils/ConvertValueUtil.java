package com.infodation.user.MongoDbOAuth.utils;

import com.infodation.user.MongoDbOAuth.config.DateFormatConfig;

import java.text.ParseException;
import java.util.regex.Pattern;

public class ConvertValueUtil {
    public final static String LONG = "LONG";
    public final static String INT = "INT";
    public final static String DOUBLE = "DOUBLE";
    public final static String DATE = "DATE";
    public final static String BOOLEAN = "BOOLEAN";
    public final static String STRING = "STRING";

    public static String convertWithKey(String key, String value) {

        if (key.endsWith("at") || key.contains("date")) return DATE;

        if (key.contains("description")) return STRING;

        if (key.startsWith("is")) return BOOLEAN;

        if (isLong(value)) return LONG;

        if (isDouble(value)) return DOUBLE;

        return STRING;
    }

    public static Object parseValue(String type, String value) throws ParseException {
        switch (type.toUpperCase()) {
            case LONG:
                return Long.parseLong(value);
            case INT:
                return Integer.parseInt(value);
            case DOUBLE:
                return Double.parseDouble(value);
            case BOOLEAN:
                return Boolean.parseBoolean(value);
            case DATE:
                return new DateFormatConfig().getFormatter(value).parse(value);
            default:
                return value;
        }
    }

    private static boolean isLong(String str) {
        if (!Pattern.matches("-?\\d+", str)) {
            return false;
        }

        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isDouble(String str) {
        return Pattern.matches("-?\\d+(\\.\\d+)?", str);
    }
}
