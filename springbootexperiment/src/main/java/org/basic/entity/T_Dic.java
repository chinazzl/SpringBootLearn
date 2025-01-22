package org.basic.entity;


/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/8/4 15:32
 * @Description: 字典表
 */
public class T_Dic {

    private String id;
    private String code;
    private String k;
    private String v;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    @Override
    public String toString() {
        return "T_Dic{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", k='" + k + '\'' +
                ", v='" + v + '\'' +
                '}';
    }
}
