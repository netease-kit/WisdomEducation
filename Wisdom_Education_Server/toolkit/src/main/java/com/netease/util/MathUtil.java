package com.netease.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MathUtil {

    public static final double DOUBLE_UNIT = 0.01d;

    private static final ThreadLocal<DecimalFormat> DECIMAL_FORMAT = ThreadLocal.withInitial(() -> new DecimalFormat("#.##"));

    public static DecimalFormat getDecimalFormat() {
        return DECIMAL_FORMAT.get();
    }

    public static String format(double value) {
        return getDecimalFormat().format(value);
    }

    /**
     * 精确加法运算
     *
     * @param value1 被加数
     * @param value2 加数
     * @return 两个参数的和
     */
    public static double add(double value1, double value2) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        return b1.add(b2).doubleValue();
    }

    /**
     * 精确减法运算
     *
     * @param value1 被减数
     * @param value2 减数
     * @return 两个参数的差
     */
    public static double subtract(double value1, double value2) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 精确乘法运算
     *
     * @param value1 被乘数
     * @param value2 乘数
     * @return 两个参数的积
     */
    public static double multiply(double value1, double value2) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 精确除法运算,精确到小数点后两位,向负无限大方向舍入
     *
     * @param value1 被除数
     * @param value2 除数
     * @return 两个参数的商
     */
    public static double divide(double value1, double value2) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        return b1.divide(b2, 2, RoundingMode.FLOOR).doubleValue();
    }

    /**
     * 精确除法运算
     *
     * @param value1       被除数
     * @param value2       除数
     * @param scale        精确范围
     * @param roundingMode 舍入模式
     * @return 两个参数的商
     * @throws IllegalArgumentException
     */
    public static double divide(double value1, double value2, int scale, RoundingMode roundingMode) throws IllegalArgumentException {
        if (scale < 0) {
            //如果精确范围小于0，抛出异常信息
            throw new IllegalArgumentException("scale must be greater than 0");
        }
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        return b1.divide(b2, scale, roundingMode).doubleValue();
    }
}
