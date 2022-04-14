package org.drools.demo.Entity.bo;

import java.io.Serializable;

/**
 * @author Julyan
 * @version V1.0
 * @Description:
 * @Date: 2022/4/14 9:15
 */
public class StudentBO implements Serializable {

    private String name;

    private String sex;

    private int minAge;
    private int maxAge;

    private String compareAgeFlag;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCompareAgeFlag() {
        return compareAgeFlag;
    }

    public void setCompareAgeFlag(String compareAgeFlag) {
        this.compareAgeFlag = compareAgeFlag;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    @Override
    public String toString() {
        return "StudentBO{" +
                "name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", minAge=" + minAge +
                ", maxAge=" + maxAge +
                ", compareAgeFlag='" + compareAgeFlag + '\'' +
                '}';
    }
}
