package com.chl.displayscreen.database.ormHelper;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2018/8/1.
 * 该类参照 FTPFile 类
 */

@Entity
public class PhotoInfo implements Serializable {
    static final long serialVersionUID = 42L;

    @Id(autoincrement = true) //id自动增长
    private Long id;

    private String name;//图片的完整路径
    private Date modifiedDate; //图片的修改时间
    private int type; //文件类型 目录、文件...
    @Generated(hash = 1779141510)
    public PhotoInfo(Long id, String name, Date modifiedDate, int type) {
        this.id = id;
        this.name = name;
        this.modifiedDate = modifiedDate;
        this.type = type;
    }
    @Generated(hash = 2143356537)
    public PhotoInfo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Date getModifiedDate() {
        return this.modifiedDate;
    }
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
}
