package com.example.skincare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skincare.R;
import com.example.skincare.adapter.ProductAdapter;
import com.example.skincare.model.ApiResponse;
import com.example.skincare.model.Product;
import com.example.skincare.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorProfileFragment extends Fragment {

    private ImageButton btnBack;
    private ImageView imgDoctor;
    private TextView tvName, tvSpecialization, tvEmail, tvPhone;
    private RecyclerView recyclerProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private int doctorId;

    public DoctorProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_doctor_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBack = view.findViewById(R.id.btn_back_doctor);
        imgDoctor = view.findViewById(R.id.img_doctor_profile);
        tvName = view.findViewById(R.id.tv_doctor_name_profile);
        tvSpecialization = view.findViewById(R.id.tv_doctor_specialization_profile);
        tvEmail = view.findViewById(R.id.tv_doctor_email);
        tvPhone = view.findViewById(R.id.tv_doctor_phone);
        recyclerProducts = view.findViewById(R.id.recycler_doctor_products);

        recyclerProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productAdapter = new ProductAdapter(getContext());
        recyclerProducts.setAdapter(productAdapter);

        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        if (getArguments() != null) {
            doctorId = getArguments().getInt("id");
            tvName.setText(getArguments().getString("name"));
            tvSpecialization.setText(getArguments().getString("specialization"));
            tvEmail.setText("Email: " + getArguments().getString("email"));
            tvPhone.setText("Phone: " + getArguments().getString("phone"));
        }

        fetchRecommendedProducts();
    }

    private void fetchRecommendedProducts() {
        Call<ApiResponse<List<Product>>> call = RetrofitClient.getApi(getContext()).getProductsByDoctor(doctorId);
        call.enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Product>>> call,
                                   @NonNull Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    productList = response.body().getData();
                    productAdapter.setProductList(productList);

                    productAdapter.setOnItemClickListener(product -> {
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", product.getId());
                        bundle.putString("name", product.getName());
                        bundle.putString("description", product.getDescription());
                        bundle.putString("expectedResults", product.getExpectedResults());
                        bundle.putString("usageInstructions", product.getUsageInstructions());
                        bundle.putString("timeOfUse", product.getTimeOfUse());
                        bundle.putString("shelfLife", product.getShelfLife());
                        bundle.putString("incompatibleProducts", product.getIncompatibleProducts());
                        bundle.putString("image", product.getImage());
                        bundle.putString("recommendedFor", product.getRecommendedFor());

                        ProductDetailsFragment detailsFragment = new ProductDetailsFragment();
                        detailsFragment.setArguments(bundle);

                        getParentFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frameFragmentLayout, detailsFragment)
                                .addToBackStack(null)
                                .commit();
                    });

                } else {
                    Toast.makeText(getContext(), "Failed to load recommended products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Product>>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
