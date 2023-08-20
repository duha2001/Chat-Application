package com.example.d_five;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.d_five.fragments.CallFragment;
import com.example.d_five.fragments.ChatFragment;
import com.example.d_five.fragments.ContactFragment;
import com.example.d_five.fragments.ProfileFragment;

import java.util.List;

public class PagerAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 4;

    @SuppressLint("StaticFieldLeak")
    public static ChatFragment chatFragment;
    public static CallFragment callFragment;
    public static ContactFragment contactFragment;
    public static ProfileFragment profileFragment;

    public PagerAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        chatFragment = new ChatFragment();
        callFragment = new CallFragment();
        contactFragment = new ContactFragment();
        profileFragment = new ProfileFragment();
        Log.i("PagerAdapter", "cons");
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return chatFragment;
            case 1:
                return callFragment;
            case 2:
                return contactFragment;
            case 3:
                return profileFragment;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
