package com.example.skincare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skincare.R;
import com.example.skincare.model.UserResponse;
import com.example.skincare.network.ApiService;
import com.example.skincare.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileFragment extends Fragment {

    private ImageView profileImage;
    private TextView profileName, profileEmail, profileAge, profileGender,
            profileSkinType, profileConcerns, profileRoutine, profileEmailStatus;
    private MaterialButton btnEditProfile;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout once
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        // Bind views
        profileImage = view.findViewById(R.id.profile_image);
        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);
        profileAge = view.findViewById(R.id.profile_age);
        profileGender = view.findViewById(R.id.profile_gender);
        profileSkinType = view.findViewById(R.id.profile_skin_type);
        profileConcerns = view.findViewById(R.id.profile_concerns);
        profileRoutine = view.findViewById(R.id.profile_routine);
        profileEmailStatus = view.findViewById(R.id.profile_email_status);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);

        // Set Edit Profile button click
        btnEditProfile.setOnClickListener(v -> {
            EditUserProfileFragment editFragment = new EditUserProfileFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.frameFragmentLayout, editFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Fetch and populate user data
        fetchUserProfile();

        return view;
    }

    private void fetchUserProfile() {
        ApiService apiService = RetrofitClient.getApi(requireContext());
        Call<UserResponse> call = apiService.getProfile();

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse.User user = response.body().getUser();
                    populateProfile(user);
                } else {
                    Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateProfile(UserResponse.User user) {
        profileName.setText(user.getUsername());
        profileEmail.setText(user.getEmail());
        profileAge.setText(user.getAge() != null ? String.valueOf(user.getAge()) : "N/A");
        profileGender.setText(user.getGender() != null ? user.getGender() : "N/A");
        profileSkinType.setText("Normal to Dry"); // Replace with backend field if available
        profileConcerns.setText("Acne, Pigmentation"); // Replace with backend field
        profileRoutine.setText("Morning: Cleanser, Vitamin C, Sunscreen\nEvening: Cleanser, Moisturizer, Retinol"); // Replace if backend data
        profileEmailStatus.setText(user.getEmail_verified_at() != null ? "Verified" : "Not Verified");

        // Optional: load profile image if backend provides URL
        // Glide.with(this).load(user.getProfileImageUrl()).into(profileImage);
    }
}
