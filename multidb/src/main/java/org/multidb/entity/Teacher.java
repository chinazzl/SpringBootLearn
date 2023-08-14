package org.multidb.entity;

/**
 * Created by Felix on 2017/7/12.
 */
public class Teacher {
    private String sid;
    private byte[] image;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
