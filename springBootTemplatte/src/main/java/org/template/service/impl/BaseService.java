package org.template.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**********************************
 * @author zhang zhao lin
 * @date 2023年08月23日 10:08
 * @Description:
 **********************************/
public class BaseService {

    @Resource
    private Validator validator;

    /**
     * 校验参数是否正确
     *
     * @param obj        校验对象
     * @param groupClass 分组校验
     * @param <T>
     * @return 校验结果 key 校验结果， value 错误信息
     */
    public <T> Pair<Boolean, String> validate(T obj, Class<?> groupClass) {
        Set<ConstraintViolation<T>> violationSet = validator.validate(obj, groupClass);
        if (CollectionUtil.isEmpty(violationSet)) {
            return new Pair<>(true, "");
        }
        List<String> errorMessages = violationSet.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        return new Pair<>(false, StringUtils.join(errorMessages, ","));
    }


}
