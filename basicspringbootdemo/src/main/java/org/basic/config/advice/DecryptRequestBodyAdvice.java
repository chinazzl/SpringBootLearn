package org.basic.config.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.basic.config.annotation.DecryptionAnnotation;
import org.basic.constant.CryptoConstant;
import org.basic.entity.RequestBase;
import org.basic.entity.RequestData;
import org.basic.exception.ParamException;
import org.basic.utils.AESUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/12/4 17:31
 * @Description: reqeustBody 自动解密
 */
public class DecryptRequestBodyAdvice implements RequestBodyAdvice {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return methodParameter.hasMethodAnnotation(DecryptionAnnotation.class);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        return inputMessage;
    }

    @SneakyThrows
    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 获取request
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        if (servletRequestAttributes == null) {
            throw new ParamException("reqeust 参数错误");
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        ServletInputStream inputStream = request.getInputStream();
        RequestData requestData = objectMapper.readValue(inputStream, RequestData.class);
        if (requestData == null || StringUtils.isBlank(requestData.getText())) {
            throw new ParamException("参数不规范");
        }
        // 获取加密后的数据
        String text = requestData.getText();
        // 放入解密之前的属性
        request.setAttribute(CryptoConstant.INPUT_ORIGINAL_DATA,text);
        // 解密
        String decryptText = null;
        try {
            decryptText = AESUtils.decrypt(text);
        } catch (Exception e) {
            throw new ParamException("解密失败");
        }

        if (StringUtils.isBlank(decryptText)) {
            throw new ParamException("解密失败");
        }
        // 放入解密后的作用域
        request.setAttribute(CryptoConstant.INPUT_DECRYPT_DATA,decryptText);
        // 获取结果
        Object result = objectMapper.readValue(decryptText, body.getClass());
        // 强制所有实体类必须继承RequestBase类，设置时间戳
        if (result instanceof RequestBase) {
            Long currentTimeMillis = ((RequestBase) result).getCurrentTimeMillis();
            // 有效期60秒
            long effective = 60*1000;
            long expire = currentTimeMillis - effective;
            if (Math.abs(expire) > effective) {
                throw new ParamException("时间戳不合法");
            }
            return result;
        }else {
            throw new ParamException(String.format("请求参数类型：%s 未继承：%s", result.getClass().getName(), RequestBase.class.getName()));
        }
    }

    /**
     * 如果body为空，转为空对象
     * @param body Sppring解析完成的参数
     * @param inputMessage  输入参数
     * @param parameter 参数对象
     * @param targetType    参数类型
     * @param converterType 消息转换类型
     * @return  真实的参数
     */
    @SneakyThrows
    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        String typeName = targetType.getTypeName();
        Class<?> bodyClass = Class.forName(typeName);
        return bodyClass.newInstance();
    }
}
