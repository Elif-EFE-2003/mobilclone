package com.softwarelif.javainstagramclone.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.softwarelif.javainstagramclone.R;
import com.softwarelif.javainstagramclone.adapter.PostAdapter;
import com.softwarelif.javainstagramclone.databinding.ActivityFeedBinding;
import com.softwarelif.javainstagramclone.model.Post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

    public class FeedActivity extends AppCompatActivity {

        private FirebaseAuth auth;
        private FirebaseFirestore firebaseFirestore;
        ArrayList<Post> postArrayList;
        private ActivityFeedBinding binding;
        PostAdapter postAdapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            binding = ActivityFeedBinding.inflate(getLayoutInflater());
            View view = binding.getRoot();
            setContentView(view);

            postArrayList = new ArrayList<>();
            auth = FirebaseAuth.getInstance();
            firebaseFirestore = FirebaseFirestore.getInstance();

            getData();

            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));


            postAdapter = new PostAdapter(postArrayList, this);
            binding.recyclerView.setAdapter(postAdapter);
        }

        private void getData() {
            firebaseFirestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(FeedActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                    if (value != null) {
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            Map<String, Object> data = snapshot.getData();
                            String postId = snapshot.getId();
                            String userEmail = (String) data.get("userEmail");
                            String comment = (String) data.get("comment");
                            String downloadUrl = (String) data.get("downloadUrl");

                            Post post = new Post(postId, userEmail, comment, downloadUrl);


                            firebaseFirestore.collection("Posts")
                                    .document(postId)
                                    .collection("Comments")
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                                            if (e != null) {
                                                Log.e("Firestore", "Hata oluştu: " + e.getLocalizedMessage());
                                                return;
                                            }

                                            if (snapshot != null && !snapshot.isEmpty()) {
                                                post.comments.clear();


                                                for (DocumentSnapshot commentSnapshot : snapshot.getDocuments()) {
                                                    String commentText = (String) commentSnapshot.get("commentText");
                                                    post.comments.add(commentText);
                                                }

                                                postArrayList.add(post);


                                                postAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    });


                        }
                    }
                }
            });
        }


        public void addCommentToPost(String postId, String commentText) {

            if (commentText.isEmpty()) {
                Toast.makeText(this, "Yorum boş olamaz!", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> commentData = new HashMap<>();
            commentData.put("commentText", commentText);
            commentData.put("userEmail", auth.getCurrentUser().getEmail());
            commentData.put("timestamp", FieldValue.serverTimestamp());

            firebaseFirestore.collection("Posts")
                    .document(postId)
                    .collection("Comments")
                    .add(commentData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(FeedActivity.this, "Yorum gönderildi!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(FeedActivity.this, "Yorum gönderilirken hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater menuInflater=getMenuInflater();
            menuInflater.inflate(R.menu.option_menu,menu);

            return super.onCreateOptionsMenu(menu);
        }

        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {

            if(item.getItemId()==R.id.add_post){
                Intent intentToUpload=new Intent(FeedActivity.this,UploadActivity.class);
                startActivity(intentToUpload);

            } else if (item.getItemId()==R.id.signout) {
                auth.signOut();

                Intent intentToMain=new Intent(FeedActivity.this,MainActivity.class);
                startActivity(intentToMain);
                finish();
            }

            return super.onOptionsItemSelected(item);
        }

    }
