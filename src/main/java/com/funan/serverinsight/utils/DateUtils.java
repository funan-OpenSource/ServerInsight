package com.funan.serverinsight.utils;


import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间工具类
 *
 * @author funan
 */
public class DateUtils {

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取当前LocalDateTime型日期时间
     *
     * @return LocalDateTime() 当前日期时间
     */
    public static LocalDateTime getNowDate() {
        return LocalDateTime.now();
    }

    public static String parseDateToStr(final String format, final LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * 获取服务器启动时间
     */
    public static LocalDateTime getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(time), java.time.ZoneId.systemDefault());
    }

    /**
     * 计算时间差
     *
     * @param startTime 开始时间
     * @param endTime   最后时间
     * @return 时间差（天/小时/分钟）
     */
    public static String timeDistance(LocalDateTime startTime, LocalDateTime endTime) {
        Duration duration = Duration.between(startTime, endTime);
        long day = duration.toDays();
        long hour = duration.toHours() % 24;
        long min = duration.toMinutes() % 60;
        return day + "天" + hour + "小时" + min + "分钟";
    }
}
