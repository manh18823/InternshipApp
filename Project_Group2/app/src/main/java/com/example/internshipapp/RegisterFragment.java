package com.example.internshipapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterFragment extends Fragment {
    private EditText etName, etOrganization, etEmail, etPassword;
    private RadioGroup rgRole;
    private Button btnRegister;
    private TextView tvToLogin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        etName = view.findViewById(R.id.etName);
        etOrganization = view.findViewById(R.id.etOrganization);
        etEmail = view.findViewById(R.id.etEmailRegister);
        etPassword = view.findViewById(R.id.etPasswordRegister);
        rgRole = view.findViewById(R.id.rgRole);
        btnRegister = view.findViewById(R.id.btnRegister);
        tvToLogin = view.findViewById(R.id.tvToLogin);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(v -> registerUser());
        tvToLogin.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_register_to_login));

        return view;
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String org = etOrganization.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        int roleId = rgRole.getCheckedRadioButtonId();

        if (!validateInputs(name, org, email, password, roleId)) return;

        String role = roleId == R.id.rbStudent ? "student" : "recruiter";

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = Objects.requireNonNull(authResult.getUser()).getUid();
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("name", name);
                    userData.put("organization", org);
                    userData.put("email", email);
                    userData.put("role", role);

                    db.collection("users").document(uid).set(userData)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(requireContext(), "Registered successfully. Please log in.", Toast.LENGTH_SHORT).show();

                                // Điều hướng về Login sau khi đăng ký thành công
                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    NavController navController = NavHostFragment.findNavController(RegisterFragment.this);
                                    navController.navigate(R.id.action_register_to_login);
                                }, 1000);
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }


    private boolean validateInputs(String name, String org, String email, String password, int roleId) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(org) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Invalid email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (roleId == -1) {
            Toast.makeText(getContext(), "Please select a role", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
