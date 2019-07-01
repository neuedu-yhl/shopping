package com.neuedu.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
    public static String dateToStr(Date date,String format)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String format1 = sdf.format(date);
        return format1;
    }

    public static Date strToDate(String date,String format)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date parse = null;
        try {
            parse = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse;
    }
}
