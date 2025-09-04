package com.example.skincare.network;

import com.example.skincare.model.ApiResponse;
import com.example.skincare.model.AuthResponse;
import com.example.skincare.model.Doctor;
import com.example.skincare.model.Product;
import com.example.skincare.model.UpdateProfileRequest;
import com.example.skincare.model.UserResponse;
import com.example.skincare.requests.LoginRequest;
import com.example.skincare.requests.RegisterRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @POST("register")
    Call<AuthResponse> registerUser(@Body RegisterRequest request);

    @POST("login")
    Call<AuthResponse> loginUser(@Body LoginRequest request);

    @POST("logout")
    Call<Void> logoutUser();

    // Get all products
    @GET("products")
    Call<ApiResponse<List<Product>>> getProducts();

    @GET("products/{id}")
    Call<ApiResponse<Product>> getProductDetails(@Path("id") int productId);

    @GET("doctors")
    Call<ApiResponse<List<Doctor>>> getDoctors(); // fetch all doctors

    @GET("doctors/{id}/products")
    Call<ApiResponse<List<Product>>> getProductsByDoctor(@Path("id") int doctorId); // fetch products recommended by a doctor

    // Fetch logged-in user's profile
    @GET("profile")
    Call<UserResponse> getProfile();

    // Update logged-in user's profile
    @PUT("profile")
    Call<UserResponse> updateProfile(@Body UpdateProfileRequest updateProfileRequest);


}
