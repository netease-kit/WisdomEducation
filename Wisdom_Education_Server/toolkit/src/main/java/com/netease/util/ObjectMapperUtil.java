package com.netease.util;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import java.util.List;

/**
 * 对象映射工具
 *
 * @author shenxiangyu on 2020/08/06
 */
public class ObjectMapperUtil {

    private static MapperFacade mapper = new DefaultMapperFactory.Builder().build().getMapperFacade();

    /**
     * 对象映射
     *
     * @param source source
     * @param destinationClass destinationClass
     * @param <S> s
     * @param <D> d
     * @return d
     */
    public static <S, D> D map(S source, Class<D> destinationClass) {
        if (source == null) {
            return null;
        }
        return mapper.map(source, destinationClass);
    }

    /**
     * 将对象转成 List
     *
     * @param source 输入对象
     * @param destinationClass 目标类
     * @param <S> 输入对象类
     * @param <D> 目标对象类
     * @return List 对象
     */
    public static <S, D> List<D> mapAsList(Iterable<S> source, Class<D> destinationClass) {
        if (source == null) {
            return null;
        }
        return mapper.mapAsList(source, destinationClass);
    }
}
