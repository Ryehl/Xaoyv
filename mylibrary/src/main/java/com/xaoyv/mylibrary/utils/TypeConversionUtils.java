package com.xaoyv.mylibrary.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>Project's name:维度商城</p>
 * <p>tag:类型转换工具类</p>
 *
 * @author Xaoyv
 * date 2020/11/1 19:02
 */
public class TypeConversionUtils {
    /**
     * long to String
     * step:
     * 1.long to data
     * 2.data to String
     *
     * @param l long
     * @return string
     */
    public static String long2String(long l) {
        //long to date
        Date date = new Date(l);
        //use simpleDataFormat class
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        //string info of date
        String stringOfDate = sdf.format(date);
        return stringOfDate;
    }

    /**
     * long to String 1hour ago
     *
     * @param l long
     * @return string
     */
    public static String long2StringAgo(long l) {
        //long to date
        Date date = new Date(l);
        //use simpleDataFormat class
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        //string info of date
        String stringOfDate = sdf.format(date);
        return stringOfDate;
    }
}
