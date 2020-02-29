package com.jeanboy.app.hooktrainning.reflect;

/**
 * Created by jeanboy on 2020/02/27 4:02 PM.
 */
public class UserModel {

    private String username;
    private int age;
    public long createAt;

    public void setUsername(String username) {
        this.username = username;
    }

    private void update() {
        createAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "username='" + username + '\'' +
                ", age=" + age +
                ", createAt=" + createAt +
                '}';
    }
}
