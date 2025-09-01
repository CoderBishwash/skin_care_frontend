package com.example.skincare.requests;

public class RegisterRequest {
    private String username, email, gender, password, password_confirmation;
    private int age;

    public RegisterRequest(String username, String email, int age, String gender, String password, String password_confirmation){
        this.username = username;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.password = password;
        this.password_confirmation = password_confirmation;
    }
}
