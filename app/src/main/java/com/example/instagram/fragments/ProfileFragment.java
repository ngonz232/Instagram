package com.example.instagram.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.instagram.EndlessRecyclerViewScrollListener;
import com.example.instagram.Utils;
import com.example.instagram.adapters.PostsAdapter;
import com.example.instagram.databinding.FragmentProfileBinding;
import com.example.instagram.models.Post;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private final String TAG = "ProfileFragment";
    private FragmentProfileBinding binding;
    private ParseUser user;
    private List<Post> allPosts;
    private PostsAdapter adapter;
    private File photoFile;
    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = ParseUser.getCurrentUser();

        allPosts = new ArrayList<>();
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        adapter = new PostsAdapter(getContext(), allPosts, 1, this);
        binding.rvPosts.setLayoutManager(layoutManager);
        binding.rvPosts.setAdapter(adapter);
        queryPosts(0);

        binding.rvPosts.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryPosts(allPosts.size());
            }
        });

        binding.ivProfilePhoto.setOnClickListener(v -> launchCamera());
        ParseFile photo = user.getParseFile("photo");
        if (photo != null) {
            Glide.with(this).load(photo.getUrl()).circleCrop().into(binding.ivProfilePhoto);
        }

        binding.swipeContainer.setOnRefreshListener(() -> {
            allPosts.clear();
            queryPosts(0);
            binding.swipeContainer.setRefreshing(false);
        });
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright);
    }

    private void launchCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri();

        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, Utils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (photoFile == null) {
                Toast.makeText(getContext(), "There is no image!", Toast.LENGTH_SHORT).show();
                return;
            }

            Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            binding.ivProfilePhoto.setImageBitmap(takenImage);
            user.put("photo", new ParseFile(photoFile));
            user.saveInBackground();
        } else {
            Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
        }
    }

    private File getPhotoFileUri() {

        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "Failed to create directory");
        }

        return new File(mediaStorageDir.getPath() + File.separator + "photo.jpg");
    }

    public void queryPosts(int skip) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setSkip(skip);
        query.setLimit(20);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
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
