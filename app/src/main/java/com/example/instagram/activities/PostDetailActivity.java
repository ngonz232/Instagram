package com.example.instagram.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.bumptech.glide.Glide;
import com.example.instagram.EndlessRecyclerViewScrollListener;
import com.example.instagram.Utils;
import com.example.instagram.adapters.CommentsAdapter;
import com.example.instagram.R;
import com.example.instagram.databinding.ActivityPostDetailBinding;
import com.example.instagram.models.Comment;
import com.example.instagram.models.Like;
import com.example.instagram.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    private final String TAG = "PostDetailActivity";
    private ActivityPostDetailBinding binding;
    private Post post;
    private int position;
    private List<Comment> allComments;
    private CommentsAdapter adapter;
    private Like likeByCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        post = getIntent().getParcelableExtra("post");
        position = getIntent().getIntExtra("position", -1);
        binding.tvUsername.setText(post.getUser().getUsername());
        binding.tvDescription.setText(post.getDescription());
        binding.tvCreatedAt.setText(Utils.calculateTimeAgo(post.getCreatedAt()));

        Glide.with(this).load(post.getImage().getUrl()).into(binding.ivPostImage);
        ParseFile profileImage = post.getUser().getParseFile("photo");
        if (profileImage != null) {
            Glide.with(this).load(profileImage.getUrl()).circleCrop().into(binding.ivProfileImage);
        }

        View.OnClickListener listener = v -> {
            Intent intent = new Intent(PostDetailActivity.this, UserDetailActivity.class);
            intent.putExtra("user", post.getUser());
            startActivity(intent);
        };
        binding.ivProfileImage.setOnClickListener(listener);
        binding.tvUsername.setOnClickListener(listener);

        setupLikes();
        setupComments();
    }

    public void setupLikes() {

        binding.tvLikeInfo.setText(String.format(Locale.US, "%d likes", post.getNumLikes()));

        ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
        query.whereEqualTo(Like.KEY_POST, post);
        query.whereEqualTo(Like.KEY_USER, ParseUser.getCurrentUser());
        query.getFirstInBackground((like, e) -> {

            likeByCurrentUser = like;
            if (e != null || like == null) {
                binding.btnLike.setImageResource(R.drawable.ufi_heart);
            } else {
                binding.btnLike.setImageResource(R.drawable.ufi_heart_active);
            }
        });
    }


    public void btnLikeOnClick(View view) {

        if (likeByCurrentUser == null) {
            binding.btnLike.setImageResource(R.drawable.ufi_heart_active);
            Like like = new Like(ParseUser.getCurrentUser(), post);
            like.saveInBackground(e -> {

                if (e != null) {
                    Log.e(TAG, "Error while saving like", e);
                    return;
                }

                binding.tvLikeInfo.setText(String.format(Locale.US, "%d likes", post.getNumLikes() + 1));
                likeByCurrentUser = like;
                post.setNumLikes(post.getNumLikes() + 1);
                post.saveInBackground();
            });
        }

        // Unlike
        else {
            binding.btnLike.setImageResource(R.drawable.ufi_heart);
            try {
                likeByCurrentUser.delete();
                likeByCurrentUser = null;
                binding.tvLikeInfo.setText(String.format(Locale.US, "%d likes", post.getNumLikes() - 1));
                post.setNumLikes(post.getNumLikes() - 1);
                post.saveInBackground();
            } catch (ParseException e) {
                Log.e(TAG, "Parse exception", e);
            }
        }
    }

    public void setupComments() {

        allComments = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new CommentsAdapter(this, allComments);
        binding.rvComments.setLayoutManager(layoutManager);
        binding.rvComments.setAdapter(adapter);
        queryComments(0);

        binding.rvComments.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryComments(allComments.size());
            }
        });
    }

    public void queryComments(int skip) {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(Comment.KEY_POST);
        query.setSkip(skip);
        query.setLimit(20);
        query.whereEqualTo(Comment.KEY_POST, post);
        query.addDescendingOrder("createdAt");
        query.findInBackground((comments, e) -> {

            if (e != null) {
                Log.e(TAG, "Issue with getting comments", e);
                return;
            }

            allComments.addAll(comments);
            adapter.notifyDataSetChanged();
        });
    }

    public void btnCommentOnClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this);
        builder.setTitle("New comment");

        final EditText input = new EditText(PostDetailActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Post", (dialog, which) -> {
            String text = input.getText().toString();
            Comment comment = new Comment(ParseUser.getCurrentUser(), post, text);
            comment.saveInBackground(e -> {

                if (e != null) {
                    Log.e(TAG, "Error while saving comment", e);
                    return;
                }

                allComments.add(0, comment);
                adapter.notifyItemInserted(0);
                dialog.cancel();
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("post", post);
        intent.putExtra("position", position);
        setResult(RESULT_OK, intent);
        System.out.println("ON BACK PRESSED");
        finish();
    }
}