package org.drools.demo.Entity.po;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2023/2/15 15:17
 * @Description:
 */
@Entity
@Table(name = "BLUE_OS_DATA")
public class BlueOsData {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "ip", nullable = true)
    private String ip;

    @Column(name = "dataTime", nullable = true)
    private String dataTime;

    @Column(name = "totalUsage", nullable = true)
    private Double totalUsage;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }

    public Double getTotalUsage() {
        return totalUsage;
    }

    public void setTotalUsage(Double totalUsage) {
        this.totalUsage = totalUsage;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "BlueOsData{" +
                "id='" + id + '\'' +
                ", ip='" + ip + '\'' +
                ", dataTime='" + dataTime + '\'' +
                ", totalUsage=" + totalUsage +
                '}';
    }
}
