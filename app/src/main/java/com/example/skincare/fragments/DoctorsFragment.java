package com.example.skincare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skincare.R;
import com.example.skincare.adapter.DoctorAdapter;
import com.example.skincare.model.ApiResponse;
import com.example.skincare.model.Doctor;
import com.example.skincare.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorsFragment extends Fragment {

    private RecyclerView recyclerDoctors;
    private DoctorAdapter doctorAdapter;
    private ImageButton btnBack;
    private List<Doctor> doctorList = new ArrayList<>();

    public DoctorsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctors, container, false);

        btnBack = view.findViewById(R.id.btn_back_doctor);
        recyclerDoctors = view.findViewById(R.id.recycler_doctors);
        recyclerDoctors.setLayoutManager(new GridLayoutManager(getContext(), 1));

        doctorAdapter = new DoctorAdapter(getContext());
        recyclerDoctors.setAdapter(doctorAdapter);

        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        doctorAdapter.setOnItemClickListener(doctor -> {
            Bundle bundle = new Bundle();
            bundle.putInt("id", doctor.getId());
            bundle.putString("name", doctor.getName());
            bundle.putString("email", doctor.getEmail());
            bundle.putString("phone", doctor.getPhone());
            bundle.putString("specialization", doctor.getSpecialization());

            DoctorProfileFragment profileFragment = new DoctorProfileFragment();
            profileFragment.setArguments(bundle);

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameFragmentLayout, profileFragment)
                    .addToBackStack(null)
                    .commit();
        });

        fetchDoctors();

        return view;
    }

    private void fetchDoctors() {
        Call<ApiResponse<List<Doctor>>> call = RetrofitClient.getApi(getContext()).getDoctors();
        call.enqueue(new Callback<ApiResponse<List<Doctor>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Doctor>>> call,
                                   @NonNull Response<ApiResponse<List<Doctor>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    doctorList = response.body().getData();
                    doctorAdapter.setDoctorList(doctorList);
                } else {
                    Toast.makeText(getContext(), "Failed to load doctors", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Doctor>>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
