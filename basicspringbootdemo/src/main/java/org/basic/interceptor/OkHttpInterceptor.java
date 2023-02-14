package org.basic.interceptor;

import okhttp3.Interceptor;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/12/21 22:43
 * @Description:
 */
public class OkHttpInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(OkHttpInterceptor.class);

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        logger.info("okhttp method:{}",chain.request().method());
        logger.info("okhttp request:{}",chain.request().body());
        return chain.proceed(chain.request());
    }
}
