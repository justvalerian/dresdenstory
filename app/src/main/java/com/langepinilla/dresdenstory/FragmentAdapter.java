package com.langepinilla.dresdenstory;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

//the fragment adapter handles the creation of the individual chapter fragments, on creation of the fragment adapter we set the number of fragments to be created and store it in mItemCount

public class FragmentAdapter extends FragmentStateAdapter {
    private final int mItemCount;

    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, int itemCount) {
        super(fragmentManager, lifecycle);
        mItemCount = itemCount;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return ChapterFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return mItemCount;
    }
}
