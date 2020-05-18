package com.example.m_feelm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

public class FragmentStatistic extends Fragment {
    ViewGroup viewGroup;
    TabLayout tabLayout;

    public static FragmentStatistic newInstance() {
        return new FragmentStatistic();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fv = inflater.inflate(R.layout.fragment_statistic, container, false);
        tabLayout = (TabLayout)fv.findViewById(R.id.layout_tab);
        Fragment fg;
        fg = FragmentStatistic_movie.newInstance();
        setChildFragment(fg);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fg;
                switch (tab.getPosition()) {
                    case 0:
                        fg = FragmentStatistic_movie.newInstance();
                        setChildFragment(fg);
                        break;
                    case 1:
                        fg = FragmentStatistic_my.newInstance();
                        setChildFragment(fg);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return fv;
    }

    private void setChildFragment(Fragment child) {
        FragmentTransaction childFt = getChildFragmentManager().beginTransaction();

        if (!child.isAdded()) {
            childFt.replace(R.id.frame_layout, child);
            childFt.addToBackStack(null);
            childFt.commit();
        }
    }
}
