package com.example.internshipapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            NavController navController = NavHostFragment.findNavController(SplashFragment.this);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user == null) {
                navController.navigate(R.id.action_splash_to_login);
            } else {
                String uid = user.getUid();
                FirebaseFirestore.getInstance().collection("users").document(uid)
                        .get()
                        .addOnSuccessListener(doc -> {
                            if (doc.exists()) {
                                String role = doc.getString("role");
                                if ("student".equals(role)) {
                                    navController.navigate(R.id.action_splash_to_studentHome);
                                } else {
                                    navController.navigate(R.id.action_splash_to_recruiterHome);
                                }
                            } else {
                                FirebaseAuth.getInstance().signOut();
                                navController.navigate(R.id.action_splash_to_login);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            navController.navigate(R.id.action_splash_to_login);
                        });
            }
        }, 1000);

        return view;
    }
}
