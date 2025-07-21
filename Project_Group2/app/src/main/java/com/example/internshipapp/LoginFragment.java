package com.example.internshipapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LoginFragment extends Fragment {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvToRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        etEmail = view.findViewById(R.id.etEmailLogin);
        etPassword = view.findViewById(R.id.etPasswordLogin);
        btnLogin = view.findViewById(R.id.btnLogin);
        tvToRegister = view.findViewById(R.id.tvToRegister);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(v -> loginUser());
        tvToRegister.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_login_to_register));

        return view;
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = Objects.requireNonNull(authResult.getUser()).getUid();
                    db.collection("users").document(uid).get().addOnSuccessListener(doc -> {
                        String role = doc.getString("role");
                        if ("student".equals(role)) {
                            Navigation.findNavController(requireView()).navigate(R.id.action_login_to_studentHome);
                        } else {
                            Navigation.findNavController(requireView()).navigate(R.id.action_login_to_recruiterHome);
                        }
                    });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
