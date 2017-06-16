package com.app.todo.model;

public class UserInfoModel {
    private String name;
    private String email;
    private String password;
    private String mobile;
    private String profile_pic;


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

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

}
