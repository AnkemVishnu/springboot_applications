/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bizmobia.vgwallet.vgwapp.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author BizMobia23
 */
public class DateFormate {

    public static Long createdDate() {
        return new Date().getTime();
    }
    
    public static String dateConvert(Date date) {
        Calendar calendar_start = getCalendarForNowdate(date);
        int year1 = calendar_start.get(Calendar.YEAR);
        int monthis1 = calendar_start.get(Calendar.MONTH) + 1;
        int day1 = calendar_start.get(Calendar.DATE);
        return year1 + "-" + monthis1 + "-" + day1 + " 00:00:00";

    }

    public static Date fromDate(Date date) {
        Calendar calendar_start = getCalendarForNowdate(date);
        int year1 = calendar_start.get(Calendar.YEAR);
        int monthis1 = calendar_start.get(Calendar.MONTH);
        int day1 = calendar_start.get(Calendar.DATE);
        calendar_start.set(year1, monthis1, day1, 00, 00, 00);

        Date startDate = calendar_start.getTime();
        return startDate;
    }

    public static Date toDate(Date date) {
        Calendar calendar_end = getCalendarForNowdate(date);
        int year = calendar_end.get(Calendar.YEAR);
        int monthis = calendar_end.get(Calendar.MONTH);
        int day = calendar_end.get(Calendar.DATE);
        calendar_end.set(year, monthis, day, 23, 59, 59);
        Date endDate = calendar_end.getTime();

        return endDate;
    }

    public static String simpleDateFormat(Date date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        String fromdatestr = sdf.format(date);
        return fromdatestr;
    }

    public static String TransactinDateConvertion(Date date) throws ParseException {
        Calendar cald = Calendar.getInstance();
        cald.setTime(date);
        cald.getTimeInMillis();
        return String.valueOf(cald.getTimeInMillis()).substring(3);
    }

    public static Calendar getCalendarForNowdate(Date showdate) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(showdate);
        return calendar;
    }

    private List<Date> getFromAndToDate(Date fromdate, Date todate) {
        List<Date> datel = new ArrayList<>();

        Calendar calendar_start = getCalendarForNowdate(fromdate);
        int year1 = calendar_start.get(Calendar.YEAR);
        int monthis1 = calendar_start.get(Calendar.MONTH);
        int day1 = calendar_start.get(Calendar.DATE);
        calendar_start.set(year1, monthis1, day1, 00, 00, 00);
        //calendar_start.set(Calendar.DAY_OF_MONTH, calendar_start.getActualMinimum(Calendar.DAY_OF_MONTH));

        Date startDate = calendar_start.getTime();
        datel.add(startDate);
        Calendar calendar_end = getCalendarForNowdate(todate);
        int year = calendar_end.get(Calendar.YEAR);
        int monthis = calendar_end.get(Calendar.MONTH);
        int day = calendar_end.get(Calendar.DATE);
        calendar_end.set(year, monthis, day, 23, 59, 59);
        Date endDate = calendar_end.getTime();
        datel.add(endDate);
        return datel;
    }

}
