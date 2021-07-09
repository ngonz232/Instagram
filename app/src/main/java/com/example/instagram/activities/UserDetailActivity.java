package com.example.instagram.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import com.bumptech.glide.Glide;
import com.example.instagram.EndlessRecyclerViewScrollListener;
import com.example.instagram.adapters.PostsAdapter;
import com.example.instagram.databinding.ActivityUserDetailBinding;
import com.example.instagram.models.Post;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;

public class UserDetailActivity extends AppCompatActivity {

    private final String TAG = "UserDetailActivity";
    private ActivityUserDetailBinding binding;
    private List<Post> allPosts;
    private PostsAdapter adapter;
    private ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = getIntent().getParcelableExtra("user");
        ParseFile profileImage = user.getParseFile("photo");
        if (profileImage != null) {
            Glide.with(this).load(profileImage.getUrl()).circleCrop().into(binding.ivProfilePhoto);
        }

        allPosts = new ArrayList<>();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        adapter = new PostsAdapter( this, allPosts, 1, null);
        binding.rvPosts.setLayoutManager(layoutManager);
        binding.rvPosts.setAdapter(adapter);
        queryPosts(0);

        binding.rvPosts.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryPosts(allPosts.size());
            }
        });

        binding.swipeContainer.setOnRefreshListener(() -> {
            allPosts.clear();
            queryPosts(0);
            binding.swipeContainer.setRefreshing(false);
        });
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright);
    }

    public void queryPosts(int skip) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setSkip(skip);
        query.setLimit(20);
        query.whereEqualTo(Post.KEY_USER, user);
        query.addDescendingOrder("createdAt");
        query.findInBackground((posts, e) -> {

            if (e != null) {
                Log.e(TAG, "Issue with getting posts", e);
                return;
            }

            allPosts.addAll(posts);
            adapter.notifyDataSetChanged();
        });
    }
}