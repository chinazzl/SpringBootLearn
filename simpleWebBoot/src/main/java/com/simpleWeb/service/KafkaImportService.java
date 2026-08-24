package com.simpleWeb.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.simpleWeb.entity.db.kafka.*;
import com.simpleWeb.entity.dto.KafkaExcelImportDTO;
import com.simpleWeb.entity.vo.KafkaImportResultVO;
import com.simpleWeb.enums.DuplicateHandleType;
import com.simpleWeb.mapper.kafka.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Kafka Excel导入服务
 */
@Slf4j
@Service
public class KafkaImportService {
    
    @Resource
    private MagicCubeMapper magicCubeMapper;
    
    @Resource
    private KafkaTopicMapper kafkaTopicMapper;
    
    @Resource
    private KafkaMngUserMapper kafkaMngUserMapper;
    
    @Resource
    private KafkaTopicPermissionMapper kafkaTopicPermissionMapper;
    
    /**
     * 批量导入Kafka权限数据
     */
    @Transactional(rollbackFor = Exception.class)
    public KafkaImportResultVO importKafkaData(List<KafkaExcelImportDTO> dataList, DuplicateHandleType handleType) {
        
        if (CollUtil.isEmpty(dataList)) {
            return KafkaImportResultVO.builder()
                    .success(false)
                    .message("导入数据为空")
                    .totalCount(0)
                    .successCount(0)
                    .failCount(0)
                    .build();
        }
        
        int totalCount = dataList.size();
        int successCount = 0;
        int failCount = 0;
        StringBuilder errorDetail = new StringBuilder();
        
        for (int i = 0; i < dataList.size(); i++) {
            KafkaExcelImportDTO dto = dataList.get(i);
            try {
                if (!validateData(dto)) {
                    failCount++;
                    errorDetail.append(String.format("第%d行数据校验失败：字段不能为空\n", i + 2));
                    continue;
                }
                
                boolean result = processData(dto, handleType);
                if (result) {
                    successCount++;
                } else {
                    log.info("第{}行数据被跳过（重复数据）", i + 2);
                }
            } catch (Exception e) {
                failCount++;
                log.error("处理第{}行数据失败", i + 2, e);
                errorDetail.append(String.format("第%d行数据处理失败：%s\n", i + 2, e.getMessage()));
            }
        }
        
        boolean success = successCount > 0;
        String message = success 
                ? String.format("导入成功，新增/覆盖%d条", successCount) 
                : "导入失败，新增/覆盖0条";
        
        return KafkaImportResultVO.builder()
                .success(success)
                .message(message)
                .totalCount(totalCount)
                .successCount(successCount)
                .failCount(failCount)
                .errorDetail(errorDetail.length() > 0 ? errorDetail.toString() : null)
                .build();
    }
    
    /**
     * 校验数据
     */
    private boolean validateData(KafkaExcelImportDTO dto) {
        return StrUtil.isNotBlank(dto.getMagicName())
                && StrUtil.isNotBlank(dto.getTopicName())
                && StrUtil.isNotBlank(dto.getUserName())
                && StrUtil.isNotBlank(dto.getPermissionType());
    }
    
    /**
     * 处理单条数据
     */
    private boolean processData(KafkaExcelImportDTO dto, DuplicateHandleType handleType) {
        Long magicId = getOrCreateMagicId(dto.getMagicName());
        if (magicId == null) {
            throw new RuntimeException("魔方名称不存在且无法创建：" + dto.getMagicName());
        }
        
        Long topicId = getOrCreateTopicId(dto.getTopicName());
        Long userId = getOrCreateUserId(dto.getUserName(), dto.getUserPwd());
        
        KafkaTopicPermissionDO existPermission = kafkaTopicPermissionMapper.selectByMagicTopicUser(
                magicId, topicId, userId);
        
        if (existPermission != null) {
            if (DuplicateHandleType.SKIP.equals(handleType)) {
                return false;
            }
            return updatePermission(existPermission, dto);
        } else {
            return insertPermission(magicId, topicId, userId, dto);
        }
    }
    
    /**
     * 获取或创建魔方ID（魔方应该预先存在，这里只查询不创建）
     */
    private Long getOrCreateMagicId(String magicName) {
        MagicCubeDO magicCube = magicCubeMapper.selectByMagicName(magicName);
        if (magicCube == null) {
            throw new RuntimeException("魔方名称不存在：" + magicName);
        }
        return magicCube.getId();
    }
    
    /**
     * 获取或创建主题ID
     */
    private Long getOrCreateTopicId(String topicName) {
        KafkaTopicDO topic = kafkaTopicMapper.selectByTopicName(topicName);
        
        if (topic == null) {
            topic = new KafkaTopicDO();
            topic.setTopicName(topicName);
            topic.setCreateTime(new Date());
            topic.setUpdateTime(new Date());
            topic.setVersion(1);
            kafkaTopicMapper.insert(topic);
        }
        
        return topic.getId();
    }
    
    /**
     * 获取或创建用户ID
     */
    private Long getOrCreateUserId(String userName, String userPwd) {
        KafkaMngUserDO user = kafkaMngUserMapper.selectByUserName(userName);
        
        if (user == null) {
            user = new KafkaMngUserDO();
            user.setUserName(userName);
            user.setUserPwd(StrUtil.isNotBlank(userPwd) ? userPwd : "123456");
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());
            user.setVersion(1);
            kafkaMngUserMapper.insert(user);
        }
        
        return user.getId();
    }
    
    /**
     * 插入权限记录
     */
    private boolean insertPermission(Long magicId, Long topicId, Long userId, KafkaExcelImportDTO dto) {
        KafkaTopicPermissionDO permission = new KafkaTopicPermissionDO();
        permission.setMagicId(magicId);
        permission.setTopicId(topicId);
        permission.setUserId(userId);
        permission.setPermissionType(dto.getPermissionType());
        permission.setCreateTime(new Date());
        permission.setUpdateTime(new Date());
        permission.setVersion(1);
        
        return kafkaTopicPermissionMapper.insert(permission) > 0;
    }
    
    /**
     * 更新权限记录
     */
    private boolean updatePermission(KafkaTopicPermissionDO existPermission, KafkaExcelImportDTO dto) {
        existPermission.setPermissionType(dto.getPermissionType());
        existPermission.setUpdateTime(new Date());
        existPermission.setVersion(existPermission.getVersion() + 1);
        
        return kafkaTopicPermissionMapper.updateById(existPermission) > 0;
    }
}
