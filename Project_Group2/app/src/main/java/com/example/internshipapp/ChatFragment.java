package com.example.internshipapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerChat;
    private EditText edtMessage;
    private Button btnSend;

    private List<ChatMessage> messageList = new ArrayList<>();
    private ChatAdapter chatAdapter;

    private String studentId;
    private String recruiterId;

    private DatabaseReference chatRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_screen_fragment, container, false);

        recyclerChat = view.findViewById(R.id.recyclerChat);
        edtMessage = view.findViewById(R.id.edtMessage);
        btnSend = view.findViewById(R.id.btnSend);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUid = currentUser.getUid();
            Bundle args = getArguments();

            if (args != null && args.containsKey("recruiterId")) {
                // Case: student -> recruiter
                studentId = currentUid;
                recruiterId = args.getString("recruiterId");
            } else if (args != null && args.containsKey("receiverId")) {
                // Case: recruiter -> student
                recruiterId = currentUid;
                studentId = args.getString("receiverId");
            }

            if (studentId != null && recruiterId != null) {
                setupChat();
            } else {
                Toast.makeText(getContext(), "Không đủ thông tin để bắt đầu cuộc trò chuyện", Toast.LENGTH_SHORT).show();
            }
        }

        return view;
    }


    private void setupChat() {
        String chatId = studentId + "_" + recruiterId;

        chatRef = FirebaseDatabase.getInstance().getReference("Chats")
                .child(chatId).child("messages");

        chatAdapter = new ChatAdapter(messageList, FirebaseAuth.getInstance().getCurrentUser().getUid());
        recyclerChat.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerChat.setAdapter(chatAdapter);

        listenForMessages();

        btnSend.setOnClickListener(v -> {
            String message = edtMessage.getText().toString().trim();
            if (!TextUtils.isEmpty(message)) {
                sendMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),  // người gửi là current user
                        FirebaseAuth.getInstance().getCurrentUser().getUid().equals(studentId) ? recruiterId : studentId,  // người nhận
                        message);
                edtMessage.setText("");
            }
        });
    }


    private void listenForMessages() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    ChatMessage msg = data.getValue(ChatMessage.class);
                    if (msg != null) {
                        messageList.add(msg);
                    }
                }
                chatAdapter.notifyDataSetChanged();
                recyclerChat.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CHAT_DEBUG", "listenForMessages cancelled", error.toException());
            }
        });
    }

    private void sendMessage(String senderId, String receiverId, String message) {
        ChatMessage chatMessage = new ChatMessage(senderId, receiverId, message, System.currentTimeMillis());
        chatRef.push().setValue(chatMessage)
                .addOnSuccessListener(aVoid -> Log.d("CHAT_DEBUG", "Message sent successfully"))
                .addOnFailureListener(e -> Log.e("CHAT_DEBUG", "Failed to send message", e));
    }


    }

