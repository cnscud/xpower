package com.cnscud.xpower.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 关于数字的一些工具类
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2017年4月27日
 */
public class NumberUtils {

    /**
     * 四舍五入保留两位小数
     * <ul>
     * <li>4.345 ≈ 4.35</li>
     * <li>4.344 ≈ 4.34</li>
     * <li>-4.345 ≈ -4.35</li>
     * </ul>
     * 
     * @param v
     *            double数字
     * @return 新的double值
     */
    public static double scale(double v) {
        return scale(v, 2);
    }

    /**
     * 四舍五入数字
     * 
     * @param v
     *            double数字
     * @param scaleNum
     *            保留小数位数 大于等于0
     * @return 新的double值
     */
    public static double scale(double v, int scaleNum) {
        return scale(v, scaleNum, RoundingMode.HALF_UP);
    }

    /**
     * double格式化
     * 
     * @param v
     *            double数字
     * @param scaleNum
     *            保留小数位数 大于等于0
     * @param mode
     *            小数保留方法
     * @return 新的double值
     */
    public static double scale(double v, int scaleNum, RoundingMode mode) {
        return BigDecimal.valueOf(v).setScale(scaleNum, mode).doubleValue();
    }

    public static int div(int sum, int count) {
        return count == 0 ? sum : sum / count;
    }

    public static double div(double sum, double count) {
        return count == 0 ? sum : sum / count;
    }
    public static float div(float sum, float count) {
        return count == 0 ? sum : sum / count;
    }
}
