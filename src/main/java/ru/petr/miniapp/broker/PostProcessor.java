package ru.petr.miniapp.broker;

import org.springframework.beans.BeansException;
import org.springframework.beans.Mergeable;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PostProcessor implements BeanPostProcessor {
    protected final static Map<Method, Object> METHOD_2_BEEN = new HashMap<>();
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Arrays.stream(bean.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(Sub.class))
                .forEach(method -> METHOD_2_BEEN.put(method, bean));
        return bean;
    }
}
