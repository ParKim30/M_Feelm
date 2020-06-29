package com.example.m_feelm;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;


public class FragmentFeelm extends Fragment {
    ViewGroup viewGroup;
    private ArrayList<UserReview> imageList;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private FirebaseUser user;
    private ArrayList<UserReview> userReviews=new ArrayList<>();
    private static final int DP = 24;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_feelm, container,
                false);

        Context context=getContext();
        ViewPager viewPager = root.findViewById(R.id.pager);
        intializeData();
//        FeelmPagerAdapter adapter=new FeelmPagerAdapter(context, userReviews);
//        adapter.notifyDataSetChanged();
        viewPager.setAdapter(new FeelmPagerAdapter(context, userReviews));
        viewPager.setClipToPadding(false);
        viewPager.setClipChildren(false);
        viewPager.setPageMargin(150);
        viewPager.setOffscreenPageLimit(3);


        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                int pageWidth = viewPager.getMeasuredWidth() - viewPager.getPaddingLeft() - viewPager.getPaddingRight();
                int pageHeight = viewPager.getHeight();
                int paddingLeft = viewPager.getPaddingLeft();
                float transformPos = (float) (page.getLeft() - (viewPager.getScrollX() + paddingLeft)) / pageWidth;

                final float normalizedposition = Math.abs(Math.abs(transformPos) - 1);
                page.setAlpha(normalizedposition + 0.5f);

                int max = -pageHeight / 10;

                if (transformPos < -1) { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    page.setTranslationY(0);
                    page.setScaleY(0.75f);
                } else if (transformPos <= 1) { // [-1,1]
                    //page.setTranslationY(max * (1-Math.abs(transformPos)));
                    page.setScaleY(0.75f+(1-Math.abs(transformPos))*0.25f);
                } else { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    page.setTranslationY(0);
                    page.setScaleY(0.75f);
                }

            }
        });
        return root;
    }
    public void intializeData(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance();
        mReference=mDatabase.getReference("User_review");

        mReference.orderByChild("id").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userReviews.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    UserReview review=child.getValue(UserReview.class);
                    userReviews.add(review);
                    Log.d("User val", review.getMovieCode());
                    Log.d("User val", child.child("rating").getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("fail","datebase 읽어오기 Error");
            }
        });
    }
}
