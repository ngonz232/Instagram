package com.example.instagram.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import com.example.instagram.R;
import com.example.instagram.fragments.ComposeFragment;
import com.example.instagram.fragments.FeedFragment;
import com.example.instagram.fragments.ProfileFragment;
import com.example.instagram.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final FragmentManager fragmentManager = getSupportFragmentManager();

        final Fragment fragment1 = new FeedFragment();
        final Fragment fragment2 = new ComposeFragment();
        final Fragment fragment3 = new ProfileFragment();

        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.action_feed:
                    fragment = fragment1;
                    break;
                case R.id.action_capture:
                    fragment = fragment2;
                    break;
                case R.id.action_profile:
                default:
                    fragment = fragment3;
                    break;
            }
            fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
            return true;
        });

        binding.bottomNavigation.setSelectedItemId(R.id.action_feed);
    }
}
