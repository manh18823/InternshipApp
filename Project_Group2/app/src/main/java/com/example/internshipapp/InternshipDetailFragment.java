package com.example.internshipapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class InternshipDetailFragment extends Fragment {

    private TextView tvTitle, tvCompany, tvLocation, tvDuration, tvField, tvDatePosted, tvDescription, tvStatus;
    private EditText etResumeText;
    private Button btnUploadResume, btnApply, btnChat;
    private Uri resumeUri = null;
    private static final int PICK_RESUME_REQUEST = 1001;
    private String recruiterId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_internship_detail, container, false);

        // Ánh xạ view
        tvTitle = view.findViewById(R.id.tvTitleDetail);
        tvCompany = view.findViewById(R.id.tvCompanyDetail);
        tvLocation = view.findViewById(R.id.tvLocationDetail);
        tvDuration = view.findViewById(R.id.tvDurationDetail);
        tvField = view.findViewById(R.id.tvFieldDetail);
        tvDatePosted = view.findViewById(R.id.tvDatePostedDetail);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvStatus = view.findViewById(R.id.tvApplicationStatus);
        etResumeText = view.findViewById(R.id.etResumeText);
        btnUploadResume = view.findViewById(R.id.btnUploadResume);
        btnApply = view.findViewById(R.id.btnApply);
        btnChat = view.findViewById(R.id.btnChat);

        // Lấy dữ liệu từ Bundle
        Bundle args = getArguments();
        if (args != null) {
            tvTitle.setText(args.getString("title"));
            setLabelAndValue(tvCompany, "🏢 Company: ", args.getString("company"));
            setLabelAndValue(tvLocation, "📍 Location: ", args.getString("location"));
            setLabelAndValue(tvDuration, "⏳ Duration: ", args.getString("duration"));
            setLabelAndValue(tvField, "🧠 Job: ", args.getString("field"));
            setLabelAndValue(tvDatePosted, "📆 Posted: ", args.getString("datePosted"));
            tvDescription.setText(args.getString("description"));
            recruiterId = args.getString("recruiterId");
        }

        // Kiểm tra nếu đã apply
        String internshipId = args.getString("title") + "_" + args.getString("company");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("applications")
                .document(userId + "_" + internshipId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String status = doc.getString("status");
                        tvStatus.setText("Application Status: " + status);
                        btnApply.setEnabled(false);
                    }
                });

        // Chọn file resume
        btnUploadResume.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(Intent.createChooser(intent, "Select Resume PDF"), PICK_RESUME_REQUEST);
        });

        // Nộp đơn
        btnApply.setOnClickListener(v -> {
            String resumeText = etResumeText.getText().toString();
            if (resumeText.isEmpty() && resumeUri == null) {
                Toast.makeText(getContext(), "Please provide resume text or upload file", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> application = new HashMap<>();
            application.put("userId", userId);
            application.put("internshipId", internshipId);
            application.put("resumeText", resumeText);
            application.put("status", "Pending");

            FirebaseFirestore.getInstance().collection("applications")
                    .document(userId + "_" + internshipId)
                    .set(application)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(getContext(), "Application Submitted", Toast.LENGTH_SHORT).show();
                        tvStatus.setText("Application Status: Pending");
                        btnApply.setEnabled(false);
                    });
        });

        // Mở bản đồ
        LinearLayout btnViewMapContainer = view.findViewById(R.id.btnViewMapContainer);
        btnViewMapContainer.setOnClickListener(v -> {
            String location = tvLocation.getText().toString();
            String internshipTitle = tvTitle.getText().toString();

            Bundle mapBundle = new Bundle();
            mapBundle.putString("location", location);
            mapBundle.putString("title", internshipTitle);

            Navigation.findNavController(v).navigate(R.id.action_internshipDetailFragment_to_mapSingleFragment, mapBundle);
        });

        btnChat.setOnClickListener(v -> {
            if (recruiterId == null || recruiterId.isEmpty()) {
                Toast.makeText(getContext(), "Không tìm thấy recruiter", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra xem đã apply chưa (status = "Apply")
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) return;

            String studentId = currentUser.getUid();

            FirebaseFirestore.getInstance().collection("applications")
                    .whereEqualTo("userId", studentId)
                    .whereEqualTo("internshipId", internshipId)
                    .whereEqualTo("status", "Accepted")
                    .limit(1)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (!snapshot.isEmpty()) {
                            Bundle chatBundle = new Bundle();
                            chatBundle.putString("recruiterId", recruiterId);

                            Navigation.findNavController(v).navigate(R.id.action_internshipDetailFragment_to_chatFragment, chatBundle);
                        } else {
                            Toast.makeText(getContext(), "Bạn cần apply trước khi mở chat", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Lỗi khi kiểm tra trạng thái ứng tuyển", Toast.LENGTH_SHORT).show();
                    });
        });

        return view;
    }

    // Gọi khi chọn file thành công
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_RESUME_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            resumeUri = data.getData();
            Toast.makeText(getContext(), "Resume selected", Toast.LENGTH_SHORT).show();
        }
    }

    // Hàm hỗ trợ format text có label
    private void setLabelAndValue(TextView textView, String label, String value) {
        String fullText = label + value;
        SpannableString spannable = new SpannableString(fullText);
        spannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                0, label.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new android.text.style.ForegroundColorSpan(android.graphics.Color.parseColor("#1F1F1F")),
                label.length(), fullText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
    }
}

