package com.xaoyv.mylibrary.utils;

/**
 * <p>Project's name:Xaoyv</p>
 * <p>说明:贝塞尔曲线</p>
 *
 * @author Xaoyv
 * date 12/25/2020 4:01 PM
 */
public class BezierUtils {
    /**
     * 根据德卡斯特里奥算法计算t时刻贝塞尔曲线的点的值 {x或y}，非递归实现
     *
     * @param u      时间 {0~1}
     * @param values 贝塞尔点（控制点和终点）集合 {x或y}
     * @return u时刻贝塞尔曲线所处的点坐标
     */
    private float deCasteljau(float u, float... values) {
        final int n = values.length;
        for (int k = 1; k < n; k++) {
            for (int j = 0; j < k; j++) {
                values[j] = values[j] + (values[j + 1] - values[j]) * u;
            }
        }
        //运算结果保存第一位
        return values[0];
    }

    /**
     * 根据德卡斯特里奥算法计算t时刻贝塞尔曲线的点的值 {x或y}，非递归实现
     *
     * @param endIndex   贝塞尔曲线结束点集合下标
     * @param startIndex 贝塞尔曲线起点集合下标
     * @param u          时间 {0~1}
     * @param values     贝塞尔点（控制点和终点）集合 {x或y}
     * @return u时刻贝塞尔曲线点坐标 {x或y}
     */
    private float deCasteljau2(int startIndex, int endIndex, float u, float... values) {
        if (endIndex == 1) {
            return (1 - u) * values[startIndex] + u * values[startIndex + 1];
        } else {
            return (1 - u) * deCasteljau2(startIndex, endIndex - 1, u, values) + u * deCasteljau2(startIndex + 1, endIndex - 1, u, values);
        }
    }

}
