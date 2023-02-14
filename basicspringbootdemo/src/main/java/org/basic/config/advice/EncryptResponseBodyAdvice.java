package org.basic.config.advice;

import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.basic.config.annotation.EncryptionAnnotation;
import org.basic.entity.RequestBase;
import org.basic.entity.Result;
import org.basic.exception.CryptoException;
import org.basic.utils.AESUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Type;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/12/4 19:29
 * @Description:
 */
@ControllerAdvice
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Result<?>> {
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) returnType.getGenericParameterType();
        // 如果直接是Result并且有解密的注解，则处理
        if (parameterizedType.getRawType() == Result.class && returnType.hasMethodAnnotation(EncryptionAnnotation.class)) {
            return true;
        }
        // 如果不是RespinseBody或者是result，则放行
        if (parameterizedType.getRawType() != ResponseEntity.class) {
            return false;
        }

        // 如果是ResponseEntity<Result>并且有解密注解，则处理
        for (Type type : parameterizedType.getActualTypeArguments()) {
            if (((ParameterizedTypeImpl) type).getRawType() == Result.class && returnType.hasMethodAnnotation(EncryptionAnnotation.class)) {
                return true;
            }
        }
        return false;

    }

    @SneakyThrows
    @Override
    public Result<?> beforeBodyWrite(Result<?> body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // 真实数据
        Object data = body.getData();
        if (data == null) {
            return body;
        }
        // 如果是实体，并且继承了Request，则放入时间戳
        if (data instanceof RequestBase) {
            ((RequestBase)data).setCurrentTimeMillis(System.currentTimeMillis());
        }
        String dataText = JSONUtil.toJsonStr(data);
        if (StringUtils.isBlank(dataText)) {
            return body;
        }
        // 如果位数小于16，报错
        // if (dataText.length() < 16) {
        //     throw new CryptoException("加密失败，数据小于16位");
        // }
        String encryptText = AESUtils.encryptHex(dataText);
        return Result.builder()
                .status(body.getStatus())
                .data(encryptText)
                .message(body.getMessage())
                .build();
    }
}
