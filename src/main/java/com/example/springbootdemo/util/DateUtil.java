package com.example.springbootdemo.util;

import lombok.extern.slf4j.Slf4j;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Slf4j
public class DateUtil {
    private static SimpleDateFormat intDateFormat = new SimpleDateFormat("yyyyMMdd");

    public static final String DATA_STRING = "yyyy-MM-dd HH:mm:ss";

    public static final String DATA_MINUTE_STRING = "yyyy-MM-dd HH:mm";

    public static final String DATA_DATE_STRING = "yyyy-MM-dd";

    public static final String DATA_EXT_STRING = "yyyy/MM/dd HH:mm:ss";

    public static final String DATE_EXT_SHORT_STRING = "yyyy/MM/dd";

    public static final String DATE_SHOT_STRING = "yyyyMMdd";

    public static final String MONTH_SHOT_STRING = "MM";
    public static final String YEAR_SHOT_STRING = "yyyy";

    public static final String AIRFLOW_DATE_STRING = "YYYY-mm-DD HH:MM:SS";
    public static final String WorkShopDATE_STRING = "yyyy-MM-dd HH:mm:ss";

    public static String getCurrentDay() {
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(today);
    }

    public static String getYesterdayDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(date);
    }

    public static String getBeforeSevenDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(date);
    }

    public static String getBeforeDate(Integer day) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -day);
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(date);
    }

    public static String formatDate(Long date, String pattern) {
        Date today = new Date(date);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(today);
    }

    public static String getBeforeDateByTime(Long time, Integer day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        calendar.add(Calendar.DATE, -day);
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(date);
    }

    /**
     * 计算前一天分区
     *
     * @param time
     * @return
     */
    public static String formatYestorday(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        SimpleDateFormat format = new SimpleDateFormat(DATE_SHOT_STRING);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        String strDate = format.format(new Date(calendar.getTime().getTime() - 1000 * 24 * 60 * 60));
        return strDate;
    }

    /**
     * format格式的dateStr转Date
     *
     * @param dateStr
     * @param format
     * @return
     */
    public static Date strToDate(String dateStr, String format) {
        SimpleDateFormat dd = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = dd.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * date转format格式String
     *
     * @param date
     * @param format
     * @return
     */
    public static String dateTostr(Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(date);
    }

    /**
     * 入参是毫秒
     *
     * @param times
     * @return
     */
    public static String date2str(long times) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(times);
        SimpleDateFormat format = new SimpleDateFormat(DATA_STRING);
        return format.format(gc.getTime());
    }

    public static String getCurrTimeStr(String format) {
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(gc.getTime());
    }

    public static Long getFirstDayOfWeek(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        // 获取前月的第一天
        Calendar cale = Calendar.getInstance();
        Date time = new Date();
        try {
            time = format.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cale.setTime(time);
        boolean isFirstSunday = (cale.getFirstDayOfWeek() == Calendar.SUNDAY);
        int weekDay = cale.get(Calendar.DAY_OF_WEEK);
        if (isFirstSunday) {
            weekDay = weekDay - 1;
            if (weekDay == 0) {
                weekDay = 7;
            }
        }
        cale.add(Calendar.DATE, -1 * weekDay + 1);
        String lastday = format.format(cale.getTime());
        return Long.valueOf(lastday);
    }


    public static Long getTodayDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        return Long.valueOf(formatter.format(currentTime));
    }


    public static Long getLastDayOfLastWeek() {
        return getLastDayOfLastWeek(getTodayDate().toString(), "yyyyMMdd");
    }

    /**
     * 得到上周的最后一天
     */
    public static Long getLastDayOfLastWeek(String date, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Calendar cal = Calendar.getInstance();
        Date time = new Date();
        try {
            time = format.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cal.setTime(time);

        boolean isFirstSunday = (cal.getFirstDayOfWeek() == Calendar.SUNDAY);
        int weekDay = cal.get(Calendar.DAY_OF_WEEK);
        if (isFirstSunday) {
            weekDay = weekDay - 1;
            if (weekDay == 0) {
                weekDay = 7;
            }
        }
        cal.add(Calendar.DATE, -1 * weekDay);
        String lastday = format.format(cal.getTime());
        return Long.valueOf(lastday);
    }

    /**
     * 获取上月第一天
     *
     * @return
     */
    public static Date getFirstDayOfLastMonth() {
        Calendar cale = Calendar.getInstance();
        cale.setTimeInMillis(System.currentTimeMillis());

        cale.add(Calendar.MONTH, -1);
        cale.set(Calendar.DAY_OF_MONTH, 1);
        return cale.getTime();
    }

    public static Integer getFirstDayOfMonth() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar cale = Calendar.getInstance();
        cale.setTimeInMillis(System.currentTimeMillis());
        cale.set(Calendar.DAY_OF_MONTH, 1);
        String lastDay = format.format(cale.getTime());
        return Integer.valueOf(lastDay);
    }

    public static Long getLastDayOfLastMonth(String dateStr) {
        Long date = getLastDayOfLastMonthWithCustomDate(dateStr);
        return date;
    }

    /**
     * 得到上个月的最后一天
     */
    public static Long getLastDayOfLastMonthWithCustomDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar cale = Calendar.getInstance();
        try {
            // 设定为给定日期
            cale.setTime(format.parse(date));
            // 向前推一个月
            cale.add(Calendar.MONTH, -1);
        } catch (ParseException e) {
            log.error(e.getMessage());
        }
        cale.set(Calendar.DAY_OF_MONTH, cale.getActualMaximum(Calendar.DAY_OF_MONTH));
        String lastDay = format.format(cale.getTime());
        return Long.valueOf(lastDay);
    }

    public static Long getLastNMonth(String date, Integer n) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar cale = Calendar.getInstance();
        try {
            // 设定为给定日期
            cale.setTime(format.parse(date));
            // 向前推一个月
            cale.add(Calendar.MONTH, -n);
        } catch (ParseException e) {
            log.error(e.getMessage());
        }
        cale.set(Calendar.DAY_OF_MONTH, cale.getActualMaximum(Calendar.DAY_OF_MONTH));
        String lastDay = format.format(cale.getTime());
        return Long.valueOf(lastDay);
    }

    public static String getBeforeDate(Integer day, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -day);
        Date date = calendar.getTime();
        SimpleDateFormat f = new SimpleDateFormat(format);
        return f.format(date);
    }

    public static String addDays(String date, Integer day) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_SHOT_STRING);
        LocalDate localDate = LocalDate.parse(date + "", formatter);
        LocalDate result = localDate.plusDays(day);
        return result.format(formatter);
    }

    public static String timestampToDate(Long timestamp, String format) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(timestamp);
        SimpleDateFormat f = new SimpleDateFormat(format);
        return f.format(gc.getTime());
    }

    public static Long toTimestamp(Integer date) {
        SimpleDateFormat dd = new SimpleDateFormat(DATE_SHOT_STRING);
        Date d = null;
        try {
            d = dd.parse(date + "");
            return d.getTime();
        } catch (Exception e) {

        }
        return 0L;
    }

    /**
     * 获取今天是周几:1到7
     *
     * @param date
     * @return
     */
    public static Integer getDayOfWeek(Integer date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_SHOT_STRING);
        LocalDate localDate = LocalDate.parse(date + "", formatter);
        int value = localDate.getDayOfWeek().getValue();
        if (0 == value) {
            return 7;
        }

        return value;
    }

    public static Integer getDayOfMonth(Integer date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_SHOT_STRING);
        LocalDate localDate = LocalDate.parse(date + "", formatter);
        return localDate.getDayOfMonth();
    }

    public static Long datetimeToTimestamp(String dateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime localDate = LocalDateTime.parse(dateTime, formatter);
        return localDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }


    /**
     * LONG转自定义String
     *
     * @param times
     * @param formatStr
     * @return
     */
    public static String ln2Str(long times, String formatStr) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(times);
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        String strDate = format.format(gc.getTime());
        return strDate;
    }

    public static String ln2DateStr(long times) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(times);
        SimpleDateFormat format = new SimpleDateFormat(DATA_STRING);
        String strDate = format.format(gc.getTime());
        return strDate;
    }

    public static Long getLastHalfMonth(String date, Integer n) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar cale = Calendar.getInstance();
        try {
            // 设定为给定日期
            cale.setTime(format.parse(date));
            // 向前推一个月
            if (n > 0) {
                cale.add(Calendar.MONTH, -n);
            }
        } catch (ParseException e) {
            log.error(e.getMessage());
        }
        cale.set(Calendar.DAY_OF_MONTH, 16);
        String lastDay = format.format(cale.getTime());
        return Long.valueOf(lastDay);
    }

    public static List<Integer> getDateOfMonth(String start, String end, int monthDay) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        List<Integer> result = new ArrayList<>(); //保存日期集合
        try {
            Date startDate = format.parse(start);
            Date endDate = format.parse(end);
            Date date = startDate;
            Calendar calendar = Calendar.getInstance();
            while (date.getTime() <= endDate.getTime()) {
                calendar.setTime(date);
                if (calendar.get(Calendar.DAY_OF_MONTH) == monthDay) {
                    result.add(Integer.valueOf(format.format(date)));
                }
                calendar.add(Calendar.DATE, 1);
                date = calendar.getTime();
            }
        } catch (ParseException e) {
            log.error("日期转换异常,error:{}", e.getMessage());
        }
        return result;
    }


    public static List<Integer> getDateOfWeek(String start, String end, int weekDays) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        List<Integer> result = new ArrayList<>(); //保存日期集合
        try {
            Date startDate = format.parse(start);
            Date endDate = format.parse(end);
            Date date = startDate;
            Calendar calendar = Calendar.getInstance();
            while (date.getTime() <= endDate.getTime()) {
                calendar.setTime(date);
                if (calendar.get(Calendar.DAY_OF_WEEK) - 1 == weekDays) {
                    result.add(Integer.valueOf(format.format(date)));
                }
                calendar.add(Calendar.DATE, 1);
                date = calendar.getTime();
            }
        } catch (ParseException e) {
            log.error("日期转换异常,error:{}", e.getMessage());
        }
        return result;
    }

    public static List<Integer> getDateOfHalfMonth(String start, String end, List<Integer> monthDays) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        List<Integer> result = new ArrayList<>(); //保存日期集合
        try {
            Date startDate = format.parse(start);
            Date endDate = format.parse(end);
            Date date = startDate;
            Calendar calendar = Calendar.getInstance();
            while (date.getTime() <= endDate.getTime()) {
                calendar.setTime(date);
                if (monthDays.contains(calendar.get(Calendar.DAY_OF_MONTH))) {
                    result.add(Integer.valueOf(format.format(date)));
                }
                calendar.add(Calendar.DATE, 1);
                date = calendar.getTime();
            }
        } catch (ParseException e) {
            log.error("日期转换异常,error:{}", e.getMessage());
        }
        return result;
    }
    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }
}
