package com.nebula.NebulaApp;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.nebula.NebulaApp.databinding.FragmentLeaveBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LeaveFragment extends Fragment {
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private ReviewedFragment reviewedFragment;
    private AppliedFragment appliedFragment;

    private FragmentLeaveBinding binding;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        binding = FragmentLeaveBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
//        View v = inflater.inflate(R.layout.fragment_leave, container, false);

        Toolbar toolbar = (Toolbar) requireActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Leave");

        viewPager = v.findViewById(R.id.viewPager);
        tabLayout = v.findViewById(R.id.tabLayout);
        reviewedFragment = new ReviewedFragment();
        appliedFragment = new AppliedFragment();

        tabLayout.setupWithViewPager(viewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(),0);
        viewPagerAdapter.addFragment(reviewedFragment,"Reviewed");
        viewPagerAdapter.addFragment(appliedFragment,"Applied");
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.getTabAt(0).setText("Reviewed");
        tabLayout.getTabAt(1).setText("Applied");
        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.reviewed_icon);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.applied_icon);

        // FAB
        FloatingActionButton fab = v.findViewById(R.id.fab);
        final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.circle_explosion_anim);
        animation.setDuration(700);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setVisibility(View.GONE);
                binding.circle.setVisibility(View.VISIBLE);
                ViewExt.startAnimation(binding.circle, animation, new Runnable() {
                    @Override
                    public void run() {
                        // display your fragment
                        v.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.attendanceChangeRate));
                        binding.circle.setVisibility(View.GONE);
                    }
                });
            }
        });

        return v;
    }
    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentTitles = new ArrayList<>();
        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }
        //add fragment to the viewpager
        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            fragmentTitles.add(title);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
        @Override
        public int getCount() {
            return fragments.size();
        }
        //to setup title of the tab layout
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }
    }
}