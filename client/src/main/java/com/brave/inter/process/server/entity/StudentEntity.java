package com.brave.inter.process.server.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class StudentEntity implements Parcelable {

    private Long id;

    private String name;

    private int age;

    private int sex;

    public StudentEntity(Long id, String name, int age, int sex) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.sex = sex;
    }

    public StudentEntity() {
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

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return this.sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.age);
        dest.writeInt(this.sex);
    }

    public void readFromParcel(Parcel source) {
        this.id = (Long) source.readValue(Long.class.getClassLoader());
        this.name = source.readString();
        this.age = source.readInt();
        this.sex = source.readInt();
    }

    protected StudentEntity(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.name = in.readString();
        this.age = in.readInt();
        this.sex = in.readInt();
    }

    public static final Creator<StudentEntity> CREATOR
            = new Creator<StudentEntity>() {
        @Override
        public StudentEntity createFromParcel(Parcel source) {
            return new StudentEntity(source);
        }

        @Override
        public StudentEntity[] newArray(int size) {
            return new StudentEntity[size];
        }
    };
}