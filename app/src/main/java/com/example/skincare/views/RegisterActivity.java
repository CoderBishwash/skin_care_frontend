package com.example.skincare.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.skincare.R;
import com.example.skincare.imp.PreferenceManager;
import com.example.skincare.model.AuthResponse;
import com.example.skincare.network.RetrofitClient;
import com.example.skincare.requests.RegisterRequest;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private EditText usernameEt, emailEt, ageEt, passwordEt, retypeEt;
    private RadioGroup genderGroup;
    private MaterialButton registerButton;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        preferenceManager = new PreferenceManager(this);

        usernameEt = findViewById(R.id.username);
        emailEt = findViewById(R.id.email);
        ageEt = findViewById(R.id.age);
        passwordEt = findViewById(R.id.password);
        retypeEt = findViewById(R.id.retypepassword);
        genderGroup = findViewById(R.id.gender_radio_group);
        registerButton = findViewById(R.id.btRegister);

        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String username = usernameEt.getText().toString().trim();
        String email = emailEt.getText().toString().trim();
        String ageStr = ageEt.getText().toString().trim();
        String password = passwordEt.getText().toString();
        String retype = retypeEt.getText().toString();

        if (username.isEmpty()) {
            usernameEt.setError("Username required");
            usernameEt.requestFocus();
            return;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEt.setError("Valid email required");
            emailEt.requestFocus();
            return;
        }

        if (ageStr.isEmpty()) {
            ageEt.setError("Age required");
            ageEt.requestFocus();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            ageEt.setError("Enter a valid age");
            ageEt.requestFocus();
            return;
        }

        int selectedGenderId = genderGroup.getCheckedRadioButtonId();
        if (selectedGenderId == -1) {
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton selectedGender = findViewById(selectedGenderId);
        String gender = selectedGender.getText().toString();

        if (password.isEmpty()) {
            passwordEt.setError("Password required");
            passwordEt.requestFocus();
            return;
        }

        if (!password.equals(retype)) {
            retypeEt.setError("Passwords do not match");
            retypeEt.requestFocus();
            return;
        }

        // 🚨 Make sure Laravel expects "password_confirmation"
        RegisterRequest request = new RegisterRequest(
                username,
                email,
                age,
                gender,
                password,
                retype // <-- In Laravel, change field name to password_confirmation
        );

        Log.d(TAG, "Register request: username=" + username +
                ", email=" + email + ", age=" + age + ", gender=" + gender);

        RetrofitClient.getApi(this).registerUser(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                Log.d(TAG, "Response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();
                    String token = auth.getAccessToken();
                    preferenceManager.saveToken(token);

                    Log.d(TAG, "Registration success. Token: " + token);
                    Toast.makeText(RegisterActivity.this, "Registered successfully!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    String errorMsg = "Registration failed";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading errorBody", e);
                    }
                    Log.e(TAG, "Registration failed: " + errorMsg);
                    Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e(TAG, "Network error: ", t);
                Toast.makeText(RegisterActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
