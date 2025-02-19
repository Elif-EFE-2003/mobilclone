package com.softwarelif.javainstagramclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.softwarelif.javainstagramclone.databinding.RecyclerRowBinding;
import com.softwarelif.javainstagramclone.model.Post;
import com.softwarelif.javainstagramclone.view.FeedActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {
    private ArrayList<Post> postArrayList;
    private FeedActivity feedActivity;


    public PostAdapter(ArrayList<Post> postArrayList, FeedActivity feedActivity) {
        this.postArrayList = postArrayList;
        this.feedActivity = feedActivity;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PostHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        Post post = postArrayList.get(position);

        holder.recyclerRowBinding.recyclerViewUserEmailText.setText(post.email);
        holder.recyclerRowBinding.recyclerViewCommentText.setText(post.comment);
        Picasso.get().load(post.downloadUrl).into(holder.recyclerRowBinding.recyclerViewImageView);


        holder.recyclerRowBinding.recyclerViewCommentList.removeAllViews();
        for (String comment : post.comments) {
            TextView commentTextView = new TextView(holder.itemView.getContext());
            commentTextView.setText(comment);
            holder.recyclerRowBinding.recyclerViewCommentList.addView(commentTextView);
        }


        holder.recyclerRowBinding.recyclerViewSendCommentButton.setOnClickListener(view -> {
            String commentText = holder.recyclerRowBinding.recyclerViewAddCommentEditText.getText().toString();


            if (!commentText.isEmpty()) {
                feedActivity.addCommentToPost(post.id, commentText);
                holder.recyclerRowBinding.recyclerViewAddCommentEditText.setText("");
            } else {
                Toast.makeText(view.getContext(), "Yorum boş olamaz!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder {
        RecyclerRowBinding recyclerRowBinding;

        public PostHolder(RecyclerRowBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;
        }
    }
}


