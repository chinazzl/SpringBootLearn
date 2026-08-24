package com.simpleWeb.enums;

/**
 * 重复数据处理方式枚举
 */
public enum DuplicateHandleType {
    
    /**
     * 覆盖已有数据
     */
    OVERRIDE("override", "覆盖已有数据"),
    
    /**
     * 跳过重复，仅新增
     */
    SKIP("skip", "跳过重复，仅新增");
    
    private final String code;
    private final String desc;
    
    DuplicateHandleType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public static DuplicateHandleType fromCode(String code) {
        for (DuplicateHandleType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return OVERRIDE;
    }
}
