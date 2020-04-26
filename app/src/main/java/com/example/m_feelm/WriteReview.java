package com.example.m_feelm;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WriteReview extends AppCompatActivity {

    int y=0, m=0, d=0;

    private Button complete_btn;
    private ImageButton calender;

    DatabaseReference mDBReference = null;
    HashMap<String, Object> childUpdates = null;
    Map<String, Object> userValue = null;
    Review userReview = null;

    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener myDatePicker = (view, year, month, dayOfMonth) -> {
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, month);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateLabel();
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_review);

        String mtitle=getIntent().getStringExtra("title");
        String mrating=getIntent().getStringExtra("rating");
        String mdirector=getIntent().getStringExtra("director");
        String mactor=getIntent().getStringExtra("actor");
        String mdate=getIntent().getStringExtra("date");
        String mposter=getIntent().getStringExtra("poster");

        TextView m_title = findViewById(R.id.title);
        TextView m_director = findViewById(R.id.director);
        TextView m_actor = findViewById(R.id.actor);
        RatingBar m_rating= findViewById(R.id.user_rating);
        TextView m_date = findViewById(R.id.pubDate);
        ImageView m_poster=findViewById(R.id.moviePoster);

        m_title.setText(Html.fromHtml(mtitle).toString());
        m_director.setText(mdirector);
        m_actor.setText(mactor);
        m_rating.setRating(Float.parseFloat(mrating) / 2);
        m_date.setText(mdate);


        Glide.with(this)
                .load(mposter)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(m_poster);


        complete_btn=findViewById(R.id.complete);

        mDBReference = FirebaseDatabase.getInstance().getReference();
        childUpdates = new HashMap<>();

        EditText date=findViewById(R.id.watchDate);
        date.setOnClickListener(v -> new DatePickerDialog(WriteReview.this, myDatePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        Switch share=findViewById(R.id.reviewShare);

        complete_btn.setOnClickListener(v -> {
            FirebaseUser user;
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            user = firebaseAuth.getCurrentUser();


            if(user!=null) {
                String id = user.getUid();
                RatingBar rating = findViewById(R.id.my_rating);
                EditText user_review = findViewById(R.id.Myreview);
                boolean feedYN = share.isChecked();
                //현재 날짜 구하기
                Date currentTime = Calendar.getInstance().getTime();
                String write_date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(currentTime);

            /*userReview = new Review(id,title,watch_date,rating.getNumStars(),user_review.getText().toString(),feedYN,write_date);
            userValue = userReview.toMap();
            review_cnt++;
            childUpdates.put("/User_review/" + id, userValue);
            mDBReference.updateChildren(childUpdates);*/

                DatabaseReference postsRef = mDBReference.child("User_review");

                DatabaseReference newPostRef = postsRef.push();
                //고유한 키 생성
                newPostRef.setValue(new Review(id, mtitle, date.getText().toString(), rating.getRating(), user_review.getText().toString(), feedYN, write_date));
            }
            }
        );
    }
    private void updateLabel() {
        String myFormat = "yyyy/MM/dd";    // 출력형식   2018/11/28
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

        EditText et_date = findViewById(R.id.watchDate);
        et_date.setText(sdf.format(myCalendar.getTime()));
    }

}
