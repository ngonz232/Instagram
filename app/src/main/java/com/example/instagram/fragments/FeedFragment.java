package com.example.instagram.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.instagram.EndlessRecyclerViewScrollListener;
import com.example.instagram.Utils;
import com.example.instagram.databinding.FragmentFeedBinding;
import com.example.instagram.models.Post;
import com.example.instagram.adapters.PostsAdapter;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    private final String TAG = "FeedFragment";
    private FragmentFeedBinding binding;
    private List<Post> allPosts;
    private PostsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentFeedBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allPosts = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        adapter = new PostsAdapter(getContext(), allPosts, 0, this);
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
        query.addDescendingOrder("createdAt");
        query.findInBackground((posts, e) -> {

            if (e != null) {
                Log.e(TAG, "Issue with getting posts", e);
                return;
            }

            // Save received posts to list and notify adapter of new data
            allPosts.addAll(posts);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Utils.POST_DETAIL_ACTIVITY_CODE && resultCode == Activity.RESULT_OK) {
            int position = data.getIntExtra("position", -1);
            allPosts.set(position, data.getParcelableExtra("post"));
            adapter.notifyItemChanged(position);
        }
    }
}