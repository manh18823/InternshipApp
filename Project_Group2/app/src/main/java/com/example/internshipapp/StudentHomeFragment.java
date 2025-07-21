package com.example.internshipapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentHomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_home, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView tv = view.findViewById(R.id.tvWelcomeStudent);

        if (user != null) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String name = documentSnapshot.getString("name");
                        tv.setText("Welcome, " + name + " (Student)");
                    });
        }

        // Custom ViewInternships Button
        LinearLayout btnViewInternships = view.findViewById(R.id.customButtonViewInternships);
        btnViewInternships.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_studentHome_to_internshipListFragment));


        LinearLayout btnViewHistory = view.findViewById(R.id.customButtonViewHistory);
        btnViewHistory.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.applicationHistoryFragment));

        // Logout Button (uses styled Button)
        LinearLayout btnLogout = view.findViewById(R.id.customButtonLogout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Navigation.findNavController(view).navigate(R.id.loginFragment);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        NotificationService.createNotificationChannel(requireContext());

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        SharedPreferences prefs = requireContext().getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        boolean notifiedAccepted = prefs.getBoolean("notifiedAccepted", false);
        boolean notifiedDeclined = prefs.getBoolean("notifiedDeclined", false);

        FirebaseFirestore.getInstance().collection("applications")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(query -> {
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        String status = doc.getString("status");

                        if ("Accepted".equals(status) && !notifiedAccepted) {
//                            NotificationService.showNotification(requireContext(), "Interview Accepted", "🎉 Your interview has been accepted!", 1);
                            prefs.edit().putBoolean("notifiedAccepted", true).apply();
                            break;
                        } else if ("Declined".equals(status) && !notifiedDeclined) {
//                            NotificationService.showNotification(requireContext(), "Interview Declined", "❌ Your interview was declined.", 2);
                            prefs.edit().putBoolean("notifiedDeclined", true).apply();
                            break;
                        }
                    }
                });
    }
}