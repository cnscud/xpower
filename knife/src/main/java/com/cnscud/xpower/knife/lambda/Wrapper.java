package com.cnscud.xpower.knife.lambda;

import com.google.common.base.CaseFormat;
import com.cnscud.xpower.knife.IFunction;
import org.apache.commons.lang.StringUtils;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author mie 2021-02-22 16:33
 * @version 1.0.0
 */
class Wrapper {

    private final Map<String, String> FUNC_CACHE = new ConcurrentHashMap<>();

    <T> String resolve(IFunction<T, ?> func) {
        return Optional.ofNullable(FUNC_CACHE.get(resolves(func)))
                .orElseGet(() -> {
                    String lambda = resolves(func);
                    FUNC_CACHE.put(lambda, lambda);
                    return lambda;
                });
    }

    private <T> String resolves(IFunction<T, ?> lambda) {
        if (!lambda.getClass().isSynthetic()) {
            throw new RuntimeException("该方法仅能传入 lambda 表达式产生的合成类");
        }
        try {
            Method method = lambda.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            java.lang.invoke.SerializedLambda serializedLambda = (SerializedLambda) method.invoke(lambda);
            String serializedLambdaName = serializedLambda.getImplMethodName();
            return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, resolvesFiled(serializedLambdaName));
        } catch (Exception e) {
            throw new RuntimeException("This is impossible to happen", e);
        }
    }

    private String resolvesFiled(String serializedLambdaName) {
        if (serializedLambdaName.startsWith("get")) {
            return StringUtils.remove(serializedLambdaName, "get");
        } else if (serializedLambdaName.startsWith("is")) {
            return StringUtils.remove(serializedLambdaName, "is");
        }
        return serializedLambdaName;
    }


}
