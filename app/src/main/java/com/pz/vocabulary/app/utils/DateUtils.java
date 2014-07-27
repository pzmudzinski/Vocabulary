package com.pz.vocabulary.app.utils;

import com.pz.vocabulary.app.screens.IntentArguments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by piotr on 03/07/14.
 */
public class DateUtils implements IntentArguments{
    public static Date today()
    {
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 0); //anything 0 - 23
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date d1 = c.getTime();
        return d1;
    }

    public static Date todayMinusXDays(int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(today());
        cal.add(Calendar.DATE, -days);
        return cal.getTime();
    }

    public static Date startMonth() {
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH,Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    public static Date todayMinusXMonths(int months)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(today());
        cal.add(Calendar.MONTH, -months);
        return cal.getTime();
    }

    public static Date startWeek()
    {
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK,Calendar.getInstance().getFirstDayOfWeek());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    public static Date getDateFromBundleArg(int showWordsSince)
    {
        Date since = null;
        switch (showWordsSince) {
            case ARG_VALUE_WORDS_SINCE_TODAY:
                since = DateUtils.today();
                break;
            case ARG_VALUE_WORDS_SINCE_YESTERDAY:
                since = DateUtils.todayMinusXDays(1);
                break;
            case ARG_VALUE_WORDS_SINCE_3_DAYS:
                since = DateUtils.todayMinusXDays(3);
                break;
            case ARG_VALUE_WORDS_SINCE_WEEK:
                since = DateUtils.startWeek();
                break;
            case ARG_VALUE_WORDS_SINCE_MONTH:
                since = DateUtils.startMonth();
                break;
            case ARG_VALUE_WORDS_SINCE_3_MONTHS:
                since = DateUtils.todayMinusXMonths(3);
                break;
            case ARG_VALUE_WORDS_SINCE_6_MONTHS:
                since = DateUtils.todayMinusXMonths(6);
                break;
            case ARG_VALUE_WORDS_SINCE_12_MONTHS:
                since = DateUtils.todayMinusXMonths(12);
                break;
        }
        return since;
    }
}
