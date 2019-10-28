package com.blcultra.util;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 时间格式化工具类
 */
public class DateFormatUtil {
    public static final String date_pattern_1 =  "yyyy-MM-dd HH:mm:ss";
    public static final String date_pattern_2 =  "yyyy-MM-dd";

    public static List<String> getDays(String startTime, String endTime) {

        List<String> days = new ArrayList<String>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date start = dateFormat.parse(startTime);
            Date end = dateFormat.parse(endTime);

            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);
            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
            while (tempStart.before(tempEnd)) {
                days.add(dateFormat.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return days;

    }

    /**
     * 比较两个时间字符串返回时近的日期
     * @param date1
     * @param date2
     * @return
     * @throws Exception
     */
    public static String DataCompareReturnEarly(String date1,String date2) throws  Exception{
        return DataCompareReturnEarly(date1,date2,date_pattern_1);
    }

    /**
     * 比较两个时间字符串返回时晚的日期
     * @param date1
     * @param date2
     * @return
     * @throws Exception
     */
    public static String DataCompareReturnLate(String date1,String date2) throws  Exception{
        return DataCompareReturnLate(date1,date2,date_pattern_1);
    }

    /**
     * 比较两个时间字符串返回时近的日期
     * @param date1
     * @param date2
     * @param pattern
     * @return
     * @throws Exception
     */
    public static String DataCompareReturnEarly(String date1,String date2,String pattern) throws  Exception{
        if(null == pattern){
           return DataCompareReturnEarly(date1,date2);
        }else{
            DateFormat format = new SimpleDateFormat(pattern);
            if(format.parse(date1).before(format.parse(date2)))
                return date2;
            else
                return date1;
        }
    }

    /**
     * 比较两个时间字符串返回时晚的日期
     * @param date1
     * @param date2
     * @param pattern
     * @return
     * @throws Exception
     */
    public static String DataCompareReturnLate(String date1,String date2,String pattern) throws  Exception{
        if(null == pattern){
            return DataCompareReturnEarly(date1,date2);
        }else{
            DateFormat format = new SimpleDateFormat(pattern);
            if(format.parse(date1).after(format.parse(date2)))
                return date2;
            else
                return date1;
        }
    }

    public static String DateFormat(Date date,String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        if(date == null){
            date = new Date();
        }
        return sdf.format(date);
    }
    public static String DateFormat(String pattern){
        return DateFormat(null,pattern);
    }

    public static String DateFormat(){
        return DateFormat(null,date_pattern_1);
    }

    public static String getLongTime(){
        return new Date().getTime() + "";
    }

    public static String getFetureDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result = format.format(today);
        return result;
    }
    public static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result = format.format(today);
        return result;
    }
    public static String getBasePastDate(String time ,int past) throws ParseException {
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date =sdf.parse(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result = format.format(today);
        return result;
    }

    public static String removeTimeFromstandardDate(String date){
        if (!StringUtil.empty(date)){
            return date.substring(0,10);
        }else {
            return date;
        }
    }


    public static void main(String[] args) throws ParseException {
//        String longTime = DateFormatUtil.getLongTime();
//        String fetureDate = getPastDate(7);
        String basePastDate = getBasePastDate("2019-04-10 11:23:38", 7);
        System.out.println(basePastDate);

    }


}
