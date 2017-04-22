package com.app.todo.model;

/**
 * Created by bridgeit on 15/4/17.
 */

public class UserInfoModel {
    String name;
    String email;
    String password;
    String mobile;

    public UserInfoModel() {
    }
    public UserInfoModel(String Name,String Email,String Password,String Mobile){
        this.mobile =Mobile;
        this.name =Name;
        this.password =Password;
        this.email =Email;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
       this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}
