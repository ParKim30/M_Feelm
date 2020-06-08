package com.example.m_feelm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentSearch fragmentSearch = new FragmentSearch();
    private FragmentFeelm fragmentFeelm = new FragmentFeelm();
    private FragmentStatistic fragmentStatistic = new FragmentStatistic();
    private FragmentFeed fragmentFeed = new FragmentFeed();
    private FragmentStatistic_my fragmentStatisticMy=new FragmentStatistic_my();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

        bottomNavigationView.setBackgroundColor(Color.BLACK);
        transaction.replace(R.id.frame_layout, fragmentFeelm).commitAllowingStateLoss();
    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            findViewById(R.id.bottom_navigation).setBackgroundColor(Color.WHITE);
            switch(menuItem.getItemId())
            {
                case R.id.menu_search:
                    transaction.replace(R.id.frame_layout, fragmentSearch).commitAllowingStateLoss();

                    break;
                case R.id.menu_feelm:
                    findViewById(R.id.bottom_navigation).setBackgroundColor(Color.BLACK);
                    transaction.replace(R.id.frame_layout, fragmentFeelm).commitAllowingStateLoss();
                    break;
                case R.id.menu_statistic:
                    transaction.replace(R.id.frame_layout, fragmentStatisticMy).commitAllowingStateLoss();
                    break;
                case R.id.menu_feed:
                    transaction.replace(R.id.frame_layout, fragmentFeed).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }
}

