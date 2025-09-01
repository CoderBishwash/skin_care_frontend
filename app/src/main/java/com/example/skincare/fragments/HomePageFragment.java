package com.example.skincare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skincare.R;
import com.example.skincare.adapter.ProductAdapter;
import com.example.skincare.model.ApiResponse;
import com.example.skincare.model.Doctor;
import com.example.skincare.model.Product;
import com.example.skincare.network.RetrofitClient;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePageFragment extends Fragment {

    private RecyclerView recyclerProducts;
    private ProductAdapter productAdapter;
    private Spinner spinnerFilter;
    private List<Product> allProducts = new ArrayList<>();

    public HomePageFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        recyclerProducts = view.findViewById(R.id.recycler_products);
        recyclerProducts.setLayoutManager(new GridLayoutManager(getContext(), 3));

        spinnerFilter = view.findViewById(R.id.spinner_filter);

        productAdapter = new ProductAdapter(getContext());
        recyclerProducts.setAdapter(productAdapter);

        // Click listener to navigate to ProductDetailsFragment
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

            ArrayList<String> doctorNames = new ArrayList<>();
            List<Doctor> doctors = product.getRecommendedByDoctors();
            if (doctors != null) {
                for (Doctor d : doctors) {
                    doctorNames.add(d.getName());
                }
            }
            bundle.putStringArrayList("recommendedByDoctors", doctorNames);

            ProductDetailsFragment detailsFragment = new ProductDetailsFragment();
            detailsFragment.setArguments(bundle);

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameFragmentLayout, detailsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Spinner selection listener
        spinnerFilter.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if (selected.equalsIgnoreCase("All")) {
                    productAdapter.setProductList(allProducts);
                } else {
                    List<Product> filtered = new ArrayList<>();
                    for (Product p : allProducts) {
                        if (p.getRecommendedFor() != null &&
                                p.getRecommendedFor().trim().equalsIgnoreCase(selected.trim())) {
                            filtered.add(p);
                        }
                    }
                    productAdapter.setProductList(filtered);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        fetchProducts();

        return view;
    }

    private void fetchProducts() {
        Call<ApiResponse<List<Product>>> call = RetrofitClient.getApi(getContext()).getProducts();
        call.enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Product>>> call,
                                   @NonNull Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    allProducts = response.body().getData();
                    productAdapter.setProductList(allProducts);

                    // Populate spinner with unique "recommendedFor" values
                    Set<String> recommendedSet = new LinkedHashSet<>();
                    recommendedSet.add("All");
                    for (Product p : allProducts) {
                        if (p.getRecommendedFor() != null && !p.getRecommendedFor().trim().isEmpty()) {
                            recommendedSet.add(p.getRecommendedFor().trim());
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                            R.layout.spinner_selected_item, new ArrayList<>(recommendedSet)) {
                        @Override
                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            View view = super.getDropDownView(position, convertView, parent);
                            TextView text = (TextView) view.findViewById(R.id.spinner_text);
                            if (text == null) text = (TextView) view;
                            text.setTextColor(ContextCompat.getColor(getContext(), R.color.white)); // dropdown text
                            text.setPadding(24, 24, 24, 24); // dropdown padding
                            text.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary_color));
                            return view;
                        }
                    };
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    spinnerFilter.setAdapter(adapter);

                    spinnerFilter.setSelection(0, false);

                } else {
                    Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Product>>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
