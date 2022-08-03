package org.basic.entity;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/8/3 14:52
 * @Description:
 */
public class FileInfo {

    private Long id;
    private Integer storageType;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStorageType() {
        return storageType;
    }

    public void setStorageType(Integer storageType) {
        this.storageType = storageType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "id=" + id +
                ", storageType=" + storageType +
                ", name='" + name + '\'' +
                '}';
    }
}
