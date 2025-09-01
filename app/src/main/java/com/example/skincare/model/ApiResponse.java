package com.example.skincare.model;

public class ApiResponse<T> {

    private boolean success;
    private T data;

    // Getters
    public boolean isSuccess() { return success; }
    public T getData() { return data; }

    // Setters
    public void setSuccess(boolean success) { this.success = success; }
    public void setData(T data) { this.data = data; }
}
