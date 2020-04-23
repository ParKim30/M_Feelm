package com.example.m_feelm;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentStatistic_movie extends Fragment {

    public static FragmentStatistic_movie newInstance(){
        return new FragmentStatistic_movie();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistic_movie, container, false);
    }
}
