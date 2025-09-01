package com.example.skincare.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.skincare.R;
import com.example.skincare.model.Doctor;
import com.example.skincare.model.Product;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsFragment extends Fragment {

    private ImageButton btnBack;
    private ImageView productImage;
    private TextView productName, recommendedFor, productDescription, productUsageContent,
            contentExpectedResults, timeOfUse, shelfLife, tvRecommendedBy;
    private ChipGroup chipGroupIncompatible;

    private Product product;

    public ProductDetailsFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBack = view.findViewById(R.id.btn_back);
        productImage = view.findViewById(R.id.product_image);
        productName = view.findViewById(R.id.product_name);
        recommendedFor = view.findViewById(R.id.recommended_for);
        productDescription = view.findViewById(R.id.product_description);
        productUsageContent = view.findViewById(R.id.product_usage_content);
        contentExpectedResults = view.findViewById(R.id.content_expected_results);
        timeOfUse = view.findViewById(R.id.time_of_use);
        shelfLife = view.findViewById(R.id.shelf_life);
        chipGroupIncompatible = view.findViewById(R.id.chipgroup_incompatible_products);
        tvRecommendedBy = view.findViewById(R.id.tv_recommended_by);

        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Retrieve product data from arguments
        if (getArguments() != null) {
            product = new Product(
                    getArguments().getInt("id"),
                    getArguments().getString("name"),
                    null, // slug not used here
                    getArguments().getString("description"),
                    getArguments().getString("expectedResults"),
                    getArguments().getString("usageInstructions"),
                    getArguments().getString("timeOfUse"),
                    getArguments().getString("shelfLife"),
                    getArguments().getString("incompatibleProducts"),
                    getArguments().getString("image"),
                    getArguments().getString("recommendedFor"),
                    new ArrayList<>() // doctors can be passed separately if needed
            );
        }

        if (product != null) {
            productName.setText(product.getName());
            recommendedFor.setText("Skin type: " + product.getRecommendedFor());
            productDescription.setText(product.getDescription());
            productUsageContent.setText(product.getUsageInstructions());
            contentExpectedResults.setText(product.getExpectedResults());
            timeOfUse.setText(product.getTimeOfUse());
            shelfLife.setText(product.getShelfLife());

            // Load image with Glide
            String imageUrl = product.getImage();
            if (imageUrl != null && !imageUrl.startsWith("http")) {
                imageUrl = "http://192.168.10.5:8000/image/product/" + imageUrl;
            }

            Glide.with(getContext())
                    .load(imageUrl != null ? imageUrl : R.drawable.amcare)
                    .placeholder(R.drawable.amcare)
                    .into(productImage);

            // Populate incompatible products chips
            chipGroupIncompatible.removeAllViews();
            if (product.getIncompatibleProducts() != null && !product.getIncompatibleProducts().isEmpty()) {
                String[] items = product.getIncompatibleProducts().split(",");
                for (String item : items) {
                    Chip chip = new Chip(getContext());
                    chip.setText(item.trim());
                    chip.setTextColor(Color.WHITE);
                    chip.setChipBackgroundColorResource(R.color.primary_color);
                    chip.setChipCornerRadius(16f);
                    chip.setClickable(false);
                    chipGroupIncompatible.addView(chip);
                }
            }

            // Populate recommended by doctors (optional: pass doctors list separately)
            List<Doctor> doctors = product.getRecommendedByDoctors();
            if (doctors != null && !doctors.isEmpty()) {
                StringBuilder names = new StringBuilder();
                for (Doctor d : doctors) {
                    if (names.length() > 0) names.append(", ");
                    names.append(d.getName());
                }
                tvRecommendedBy.setText("Recommended by: " + names);
            } else {
                tvRecommendedBy.setText(""); // hide if none
            }
        }
    }
}
