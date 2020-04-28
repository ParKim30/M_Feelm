package com.example.m_feelm;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.m_feelm.model.Item;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private ArrayList<Item> mMovieInfoArrayList;

    public MovieAdapter(Context context, ArrayList<Item> movieInfoArrayList) {
        mContext = context;
        mMovieInfoArrayList = movieInfoArrayList;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MovieViewHolder movieViewHolder = (MovieViewHolder) holder;
        //홀더 속 아이디에 값을 집어넣는..
        Item item = mMovieInfoArrayList.get(position);

        String reDirector = item.getDirector().replace("|"," | ");
        String reActor = item.getActor().replace("|"," | ");

        movieViewHolder.mTvTitle.setText(Html.fromHtml(item.getTitle()));
        movieViewHolder.mRbUserRating.setRating(Float.parseFloat(item.getUserRating()) / 2);
        movieViewHolder.mTvPubData.setText(item.getPubDate());
        //movieViewHolder.mTvDirector.setText(Html.fromHtml(reDirector));
        //movieViewHolder.mTvActor.setText(Html.fromHtml(reActor));

        //url을 통해 원격에 있는 이미지를 로딩
        Glide.with(mContext)
                .load(item.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(movieViewHolder.getImage());
    }

    @Override
    public int getItemCount() {
        return mMovieInfoArrayList.size();
    }

    public void clearItems() {
        mMovieInfoArrayList.clear();
        notifyDataSetChanged();
    }

    public void clearAndAddItems(ArrayList<Item> items) {
        mMovieInfoArrayList.clear();
        mMovieInfoArrayList.addAll(items);
        notifyDataSetChanged();
    }

    //리사이클러 뷰의 아이템들 아이디를 홀더에 담음
    class MovieViewHolder extends RecyclerView.ViewHolder {

        private ImageView mIvPoster;
        private TextView mTvTitle;
        private RatingBar mRbUserRating;
        private TextView mTvPubData;
        private TextView mTvDirector;
        private TextView mTvActor;
        private Intent intent;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        MovieViewHolder(View view) {
            super(view);
            mIvPoster = view.findViewById(R.id.iv_poster);
            mTvTitle = view.findViewById(R.id.tv_title);
            mRbUserRating = view.findViewById(R.id.rb_user_rating);
            mTvPubData = view.findViewById(R.id.tv_pub_data);
            //mTvDirector = view.findViewById(R.id.tv_director);
            //mTvActor = view.findViewById(R.id.tv_actor);

            GradientDrawable drawable=
                    (GradientDrawable) mContext.getDrawable(R.drawable.image_rounding);

            mIvPoster.setBackground(drawable);
            mIvPoster.setClipToOutline(true);

            view.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    Item item = mMovieInfoArrayList.get(position);

                    String reDirector = item.getDirector().replace("|"," | ");
                    String reActor = item.getActor().replace("|"," | ");

                    intent = new Intent(v.getContext(), WriteReview.class);
                    intent.putExtra("title", Html.fromHtml(item.getTitle()).toString());
                    intent.putExtra("rating", item.getUserRating());
                    intent.putExtra("director", reDirector);
                    intent.putExtra("actor", reActor);
                    intent.putExtra("date",item.getPubDate());
                    intent.putExtra("poster",item.getImage());

                    v.getContext().startActivity(intent);

                }
            });
        }

        public ImageView getImage() {
            return mIvPoster;
        }

    }
}
