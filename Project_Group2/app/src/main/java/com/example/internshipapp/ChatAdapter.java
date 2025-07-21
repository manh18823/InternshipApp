package com.example.internshipapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.internshipapp.ChatMessage;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> messageList;
    private String currentUserId;
    private Map<String, String> userNames = new HashMap<>();

    public ChatAdapter(List<ChatMessage> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage msg = messageList.get(position);
        boolean isCurrentUser = msg.getSenderId().equals(currentUserId);

        if (isCurrentUser) {
            holder.leftContainer.setVisibility(View.GONE);
            holder.rightContainer.setVisibility(View.VISIBLE);
            holder.rightMessage.setText(msg.getMessage());

            fetchUserName(msg.getSenderId(), name -> holder.rightName.setText(name));
        } else {
            holder.rightContainer.setVisibility(View.GONE);
            holder.leftContainer.setVisibility(View.VISIBLE);
            holder.leftMessage.setText(msg.getMessage());

            fetchUserName(msg.getSenderId(), name -> holder.leftName.setText(name));
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftContainer, rightContainer;
        TextView leftMessage, rightMessage, leftName, rightName;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            leftContainer = itemView.findViewById(R.id.leftContainer);
            rightContainer = itemView.findViewById(R.id.rightContainer);
            leftMessage = itemView.findViewById(R.id.leftMessage);
            rightMessage = itemView.findViewById(R.id.rightMessage);
            leftName = itemView.findViewById(R.id.leftName);
            rightName = itemView.findViewById(R.id.rightName);
        }
    }

    private void fetchUserName(String uid, Consumer<String> callback) {
        if (userNames.containsKey(uid)) {
            callback.accept(userNames.get(uid));
            return;
        }

        FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    String name = doc.getString("name");
                    if (name == null) name = "Unknown";
                    userNames.put(uid, name);
                    callback.accept(name);
                })
                .addOnFailureListener(e -> callback.accept("Unknown"));
    }
}
