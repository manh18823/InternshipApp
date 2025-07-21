package com.example.internshipapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecruiterHomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recruiter_home, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView tv = view.findViewById(R.id.tvWelcomeRecruiter);

        if (user != null) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String name = documentSnapshot.getString("name");
                        tv.setText("Welcome, " + name + " (Recruiter)");
                    });
        }

        LinearLayout btnLogout = view.findViewById(R.id.btnLogoutRecruiterContainer);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Navigation.findNavController(view).navigate(R.id.loginFragment);
        });

        LinearLayout btnSchedule = view.findViewById(R.id.btnScheduleInterviewContainer);
        btnSchedule.setOnClickListener(v -> {
            if (user == null) return;
            String userId = user.getUid();

            FirebaseFirestore.getInstance().collection("applications")
                    .limit(1)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                            String internshipId = doc.getString("internshipId");
                            String studentId = doc.getString("userId");

                            if (internshipId == null || studentId == null) {
                                Toast.makeText(getContext(), "Missing data", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Bundle bundle = new Bundle();
                            bundle.putString("internshipId", internshipId);
                            bundle.putString("studentId", studentId);

                            Navigation.findNavController(v)
                                    .navigate(R.id.action_recruiterHome_to_interviewScheduleFragment, bundle);
                        } else {
                            Toast.makeText(getContext(), "No application found.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error loading application", Toast.LENGTH_SHORT).show());
        });

        LinearLayout btnChat = view.findViewById(R.id.btnChatStudentContainer);
        btnChat.setOnClickListener(v -> {
            FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
            if (user1 == null) return;

            String recruiterId1 = user1.getUid();
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

            // Bước 1: Lấy tất cả internship thuộc recruiter
            FirebaseFirestore.getInstance().collection("internships")
                    .whereEqualTo("recruiterId", recruiterId1)
                    .get()
                    .addOnSuccessListener(internshipSnapshot -> {
                        List<String> internshipIds = new ArrayList<>();
                        for (DocumentSnapshot doc : internshipSnapshot.getDocuments()) {
                            String internshipId = doc.getString("title") + "_" + doc.getString("company");
                            internshipIds.add(internshipId);
                        }

                        if (internshipIds.isEmpty()) {
                            Toast.makeText(getContext(), "No internships found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Bước 2: Lấy ứng viên đã Apply vào internship này
                        FirebaseFirestore.getInstance().collection("applications")
                                .whereIn("internshipId", internshipIds)
                                .whereEqualTo("status", "Accepted") // Chỉ lấy ứng viên đã nộp
                                .get()
                                .addOnSuccessListener(appSnapshot -> {
                                    List<String> studentIds = new ArrayList<>();
                                    for (DocumentSnapshot doc : appSnapshot.getDocuments()) {
                                        String studentId = doc.getString("userId");
                                        if (studentId != null) studentIds.add(studentId);
                                    }

                                    if (studentIds.isEmpty()) {
                                        Toast.makeText(getContext(), "No applicants found", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    // Bước 3: Duyệt qua studentIds để kiểm tra ai đã gửi tin nhắn (dùng Realtime DB)
                                    for (String studentId : studentIds) {
                                        String chatId = studentId + "_" + recruiterId1;
                                        DatabaseReference chatRef = dbRef.child("Chats").child(chatId).child("messages");

                                        chatRef.limitToFirst(1).get().addOnSuccessListener(snapshot -> {
                                            if (snapshot.exists()) {
                                                // Nếu có tin nhắn thì mở chat với student này
                                                Bundle bundle = new Bundle();
                                                bundle.putString("receiverId", studentId);  // recruiter trả lời student
                                                Navigation.findNavController(v)
                                                        .navigate(R.id.action_recruiterHome_to_chatFragment, bundle);
                                            }
                                        });
                                    }
                                });
                    });
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        NotificationService.createNotificationChannel(requireContext());

        SharedPreferences prefs = requireContext().getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        int previousCount = prefs.getInt("pendingAppCount", 0);

        FirebaseFirestore.getInstance().collection("applications")
                .whereEqualTo("status", "Pending")
                .get()
                .addOnSuccessListener(query -> {
                    int currentCount = query.size();

                    if (currentCount > previousCount) {
//                        NotificationService.showNotification(
//                                requireContext(),
//                                "New Applications",
//                                "📥 You have " + currentCount + " new application(s).",
//                                3
//                        );

                        prefs.edit().putInt("pendingAppCount", currentCount).apply();
                    }
                });
    }
}
