package com.example.qinote;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by 王石旺 on 2016/12/20.
 */

public class Utility {
    public static String getFriendlyDayString(){
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd");
        return simpleDateFormat.format(calendar.getTime());
    }

    public static String getFriendlyMonthYearString(){
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat monthSimpleDateFormat=new SimpleDateFormat("MMMM");
        SimpleDateFormat yearSimpleDateFormat=new SimpleDateFormat("yyyy");
        String month=monthSimpleDateFormat.format(calendar.getTime());
        String year=yearSimpleDateFormat.format(calendar.getTime());
        return month+"\n"+year;
    }


}
