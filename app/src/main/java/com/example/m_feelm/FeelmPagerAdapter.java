package com.example.m_feelm;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class FeelmPagerAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<UserReview> userReviews;

    public FeelmPagerAdapter(Context context, ArrayList<UserReview> userReviews)
    {
        this.mContext = context;
        this.userReviews = userReviews;
        notifyDataSetChanged();
        Log.d("출력","adapter 연결 중");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.feelm_item, null);

        ImageView imageView = view.findViewById(R.id.feelmImg);
        Glide.with(mContext)
                .load(userReviews.get(position).getPosterUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

        GradientDrawable drawable=
                (GradientDrawable) mContext.getDrawable(R.drawable.image_rounding);

        imageView.setBackground(drawable);
        imageView.setClipToOutline(true);

        TextView title=view.findViewById(R.id.title);
        TextView pubDate=view.findViewById(R.id.pubDate);
        TextView genre=view.findViewById(R.id.genre);
        RatingBar ratingBar=view.findViewById(R.id.rb_user_rating);
        TextView ratingNum=view.findViewById(R.id.user_rating_num);

        title.setText(userReviews.get(position).getTitle());
        //pubDate.setText(userReviews.get(position).get);
        //genre.setText();
        ratingNum.setText(String.format("%.1f", userReviews.get(position).getRating()));
        ratingBar.setRating(userReviews.get(position).getRating());

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return userReviews.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (View)object);
    }
}
