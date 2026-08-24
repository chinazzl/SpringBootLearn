package com.simpleWeb.controller;

import cn.hutool.core.util.StrUtil;
import com.simpleWeb.entity.dto.KafkaExcelImportDTO;
import com.simpleWeb.entity.vo.KafkaImportResultVO;
import com.simpleWeb.enums.DuplicateHandleType;
import com.simpleWeb.service.KafkaImportService;
import com.simpleWeb.util.ExcelParseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * Kafka Excel导入控制器
 */
@Slf4j
@RestController
@RequestMapping("/kafka")
public class KafkaImportController {
    
    @Resource
    private KafkaImportService kafkaImportService;
    
    /**
     * 上传并导入Excel文件
     * 
     * @param file Excel文件
     * @param duplicateHandle 重复数据处理方式：override-覆盖已有数据，skip-跳过重复仅新增
     * @return 导入结果
     */
    @PostMapping("/import")
    public KafkaImportResultVO importExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "duplicateHandle", defaultValue = "override") String duplicateHandle) {
        
        try {
            if (!ExcelParseUtil.validateFileType(file)) {
                return KafkaImportResultVO.builder()
                        .success(false)
                        .message("文件格式不支持，仅支持：" + ExcelParseUtil.getSupportedFormats())
                        .totalCount(0)
                        .successCount(0)
                        .failCount(0)
                        .build();
            }
            
            if (!ExcelParseUtil.validateFileSize(file)) {
                return KafkaImportResultVO.builder()
                        .success(false)
                        .message("文件大小超过限制，最大支持10MB")
                        .totalCount(0)
                        .successCount(0)
                        .failCount(0)
                        .build();
            }
            
            List<KafkaExcelImportDTO> dataList = ExcelParseUtil.parseExcel(file);
            
            DuplicateHandleType handleType = DuplicateHandleType.fromCode(duplicateHandle);
            
            KafkaImportResultVO result = kafkaImportService.importKafkaData(dataList, handleType);
            
            log.info("Excel导入完成，文件名：{}，总数：{}，成功：{}，失败：{}", 
                    file.getOriginalFilename(), 
                    result.getTotalCount(), 
                    result.getSuccessCount(), 
                    result.getFailCount());
            
            return result;
            
        } catch (Exception e) {
            log.error("Excel导入异常", e);
            return KafkaImportResultVO.builder()
                    .success(false)
                    .message("导入失败：" + e.getMessage())
                    .totalCount(0)
                    .successCount(0)
                    .failCount(0)
                    .build();
        }
    }
    
    /**
     * 获取导入模板说明
     */
    @GetMapping("/template/info")
    public String getTemplateInfo() {
        return "Excel模板列名：魔方名称、主题名称、用户权限、用户名称、设置密码\n" +
               "支持格式：" + ExcelParseUtil.getSupportedFormats() + "\n" +
               "重复数据处理参数：\n" +
               "  - override：覆盖已有数据（默认）\n" +
               "  - skip：跳过重复，仅新增";
    }
}
