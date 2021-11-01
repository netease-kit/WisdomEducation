package com.netease.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomUtil {

    private static final Random RAND = new Random();

    public static int getRand(int n) {
        return RAND.nextInt(n);
    }

    /**
     * @param prob 命中概率，取值区间：[0, 100]
     * @return 是否命中
     */
    public static boolean isHit(int prob) {
        if (prob < 0 || prob > 100)
            return false;

        int k = RAND.nextInt(100);
        return (k < prob);
    }

    /**
     * 示例1：输入列表为[60, 30, 10]，则返回0/1/2的概率分别为60%/30%/10%
     * 示例2：输入列表为[0, 10, 10]，则返回0/1/2的概率分别为0%/50%/50%
     * 
     * @param weightList 命中权重列表，列表中各项的累加值没有限制，不一定非要等于100
     * @return 按指定权重所映射的概率，返回命中的下标值
     */
    public static int selectIndex(List<Integer> weightList) {
        // 计算累加权重值
        List<Integer> weightSumList = new ArrayList<Integer>();
        for (int weight: weightList) {
            int lastSum = weightSumList.isEmpty() ? 0 : weightSumList
                .get(weightSumList.size() - 1);
            weightSumList.add(lastSum + weight);
        }

        int maxWeightSum = weightSumList.get(weightSumList.size() - 1);

        int k = RAND.nextInt(maxWeightSum);
        for (int i = 0; i < weightSumList.size(); i++) {
            if (i == 0) {
                if (k < weightSumList.get(0))
                    return i;
            } else if (k >= weightSumList.get(i - 1)
                && k < weightSumList.get(i)) {
                return i;
            }
        }

        return 0;
    }

    private static void testIsHit() {
        int times = 1000000;
        for (int i = -1; i <= 101; i++) {
            int hitCount = 0;
            for (int j = 0; j < times; j++) {
                if (isHit(i))
                    hitCount++;
            }
            System.out.println(String.format("%d: %d/%d=%.2f%%", i, hitCount,
                times, ((double) hitCount / times) * 100));
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // testIsHit();
        // testSelectIndex();
    }

}
