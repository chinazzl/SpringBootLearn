package com.simpleWeb.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.simpleWeb.entity.dto.KafkaExcelImportDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * Excel解析工具类
 */
@Slf4j
public class ExcelParseUtil {
    
    private static final String[] ALLOWED_EXTENSIONS = {".xlsx", ".xls", ".csv"};
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    /**
     * 校验文件格式
     */
    public static boolean validateFileType(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        String originalFilename = file.getOriginalFilename();
        if (StrUtil.isBlank(originalFilename)) {
            return false;
        }
        
        String extension = FileUtil.extName(originalFilename);
        if (StrUtil.isBlank(extension)) {
            return false;
        }
        
        String extWithDot = "." + extension.toLowerCase();
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equals(extWithDot)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 校验文件大小
     */
    public static boolean validateFileSize(MultipartFile file) {
        return file != null && file.getSize() <= MAX_FILE_SIZE;
    }
    
    /**
     * 解析Excel文件为DTO列表
     */
    public static List<KafkaExcelImportDTO> parseExcel(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            ExcelReader reader = ExcelUtil.getReader(inputStream);
            
            reader.addHeaderAlias("魔方名称", "magicName");
            reader.addHeaderAlias("主题名称", "topicName");
            reader.addHeaderAlias("用户权限", "permissionType");
            reader.addHeaderAlias("用户名称", "userName");
            reader.addHeaderAlias("设置密码", "userPwd");
            
            List<KafkaExcelImportDTO> dataList = reader.readAll(KafkaExcelImportDTO.class);
            
            log.info("成功解析Excel文件，共{}条数据", dataList.size());
            return dataList;
            
        } catch (Exception e) {
            log.error("解析Excel文件失败", e);
            throw new RuntimeException("Excel文件解析失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取支持的文件格式说明
     */
    public static String getSupportedFormats() {
        return String.join(", ", ALLOWED_EXTENSIONS);
    }
}
