package com.example.skincare.model;

import java.io.Serializable;
import java.util.List;

public class Doctor implements Serializable {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String specialization;
    private List<Product> products; // optional, can store recommended products

    public Doctor() {}

    public Doctor(int id, String name, String email, String phone, String specialization) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getSpecialization() { return specialization; }
    public List<Product> getProducts() { return products; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setProducts(List<Product> products) { this.products = products; }
}
