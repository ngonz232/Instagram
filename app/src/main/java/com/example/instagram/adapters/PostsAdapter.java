package com.example.instagram.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.instagram.Utils;
import com.example.instagram.activities.UserDetailActivity;
import com.example.instagram.databinding.ItemPostImageBinding;
import com.example.instagram.models.Comment;
import com.example.instagram.models.Post;
import com.example.instagram.activities.PostDetailActivity;
import com.example.instagram.databinding.ItemPostBinding;
import com.parse.ParseFile;
import com.parse.ParseUser;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private final String TAG = "PostsAdapter";
    private final Context context;
    private final List<Post> posts;
    private final int mode;
    private final Fragment fragment;

    public PostsAdapter(Context context, List<Post> posts, int mode, Fragment fragment) {
        this.context = context;
        this.posts = posts;
        this.mode = mode;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mode == 0) {
            return new ViewHolder(ItemPostBinding.inflate(LayoutInflater.from(context)));
        } else {
            return new ViewHolder(ItemPostImageBinding.inflate(LayoutInflater.from(context)));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemPostBinding itemPostBinding;
        private ItemPostImageBinding itemPostImageBinding;

        public ViewHolder(ItemPostBinding binding) {
            super(binding.getRoot());
            this.itemPostBinding = binding;
            binding.getRoot().setOnClickListener(this);

            View.OnClickListener listener = v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(context, UserDetailActivity.class);
                    intent.putExtra("user", posts.get(position).getUser());
                    context.startActivity(intent);
                }
            };
            binding.ivProfileImage.setOnClickListener(listener);
            binding.tvUsername.setOnClickListener(listener);

            binding.btnComment.setOnClickListener(this::btnCommentClicked);
        }

        public ViewHolder(ItemPostImageBinding binding) {
            super(binding.getRoot());
            this.itemPostImageBinding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(Post post) {
            ImageView ivImage;
            if (mode == 0) {
                ivImage = itemPostBinding.ivImage;
                itemPostBinding.tvDescription.setText(post.getDescription());
                itemPostBinding.tvUsername.setText(post.getUser().getUsername());
                itemPostBinding.tvLikeInfo.setText(String.format(Locale.US, "%d likes", post.getNumLikes()));

                ParseFile profileImage = post.getUser().getParseFile("photo");
                if (profileImage != null) {
                    Glide.with(context).load(profileImage.getUrl()).circleCrop().into(itemPostBinding.ivProfileImage);
                }
            } else {
                ivImage = itemPostImageBinding.ivImage;
            }

            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImage);
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("post", posts.get(position));
                intent.putExtra("position", position);
                if (fragment != null) {
                    fragment.startActivityForResult(intent, Utils.POST_DETAIL_ACTIVITY_CODE);
                } else {
                    ((Activity) context).startActivityForResult(intent, Utils.POST_DETAIL_ACTIVITY_CODE);
                }
            }
        }

        public void btnCommentClicked(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("New comment");

            final EditText input = new EditText(context);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("Post", (dialog, which) -> {
                String text = input.getText().toString();
                Comment comment = new Comment(ParseUser.getCurrentUser(), posts.get(getAdapterPosition()), text);
                comment.saveInBackground(e -> {

                    if (e != null) {
                        Log.e(TAG, "Error while saving comment", e);
                        return;
                    }

                    Toast.makeText(input.getContext(), "Comment added", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                    onClick(v);
                });
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        }
    }
}