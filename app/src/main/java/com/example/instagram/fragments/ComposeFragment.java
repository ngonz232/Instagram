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

import com.example.instagram.Utils;
import com.example.instagram.activities.MainActivity;
import com.example.instagram.databinding.FragmentComposeBinding;
import com.example.instagram.models.Post;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;

public class ComposeFragment extends Fragment {

    private final String TAG = "CaptureFragment";
    private FragmentComposeBinding binding;
    private File photoFile;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentComposeBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnCaptureImage.setOnClickListener(v -> launchCamera());
        binding.btnSubmit.setOnClickListener(v -> {

            String description = binding.etDescription.getText().toString();
            if (description.isEmpty()) {
                Toast.makeText(getContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (photoFile == null || binding.ivPostImage.getDrawable() == null) {
                Toast.makeText(getContext(), "There is no image!", Toast.LENGTH_SHORT).show();
                return;
            }

            savePost(description, ParseUser.getCurrentUser(), photoFile);
        });
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
            Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            binding.ivPostImage.setImageBitmap(takenImage);
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

    private void savePost(String description, ParseUser currentUser, File photoFile) {
        binding.pbLoading.setVisibility(View.VISIBLE);
        Post post = new Post(currentUser, description, new ParseFile(photoFile));
        post.saveInBackground(e -> {

            if (e != null) {
                Log.e(TAG, "Error while saving post", e);
                return;
            }

            Toast.makeText(getContext(), "Post saved", Toast.LENGTH_SHORT).show();
            binding.etDescription.setText("");
            binding.ivPostImage.setImageResource(0);
            binding.pbLoading.setVisibility(View.INVISIBLE);
            Intent i = new Intent(getContext(), MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
    }

}