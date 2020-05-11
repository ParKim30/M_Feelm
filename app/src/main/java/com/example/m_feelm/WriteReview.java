package com.example.m_feelm;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;


import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedInputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WriteReview extends AppCompatActivity {

    DatabaseReference mDBReference = null;
    HashMap<String, Object> childUpdates = null;
   // Map<String, Object> userValue = null;
    //Review userReview = null;
    String mtitle;
    String movieCode="123";

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

        MyAsyncTask async = new MyAsyncTask();
        async.execute();

        inputDB();

    }
    private void inputDB(){
        mtitle=getIntent().getStringExtra("title");
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
        TextView m_ratingNum=findViewById(R.id.user_rating_num);

        m_title.setText(Html.fromHtml(mtitle).toString());
        m_director.setText(mdirector);
        m_actor.setText(mactor);
        m_rating.setRating(Float.parseFloat(mrating) / 2);
        m_date.setText(mdate);


        String ratingNum = String.format("%.1f", Float.parseFloat(mrating) / 2);

        m_ratingNum.setText(ratingNum);


        Glide.with(this)
                .load(mposter)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(m_poster);


        Button complete_btn = findViewById(R.id.complete);

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
                        Log.d("코드는?",movieCode);
                        newPostRef.setValue(new Review(id, mtitle, date.getText().toString(), rating.getRating(), user_review.getText().toString(), feedYN, write_date,movieCode));
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

    class MyAsyncTask extends AsyncTask<String,Void,String> {
        OkHttpClient client = new OkHttpClient();
        @Override
        protected String doInBackground(String... parmas) {

            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieList.json").newBuilder();
            urlBuilder.addQueryParameter("key","2bc022ca249311ee687b6976e45237a4");
            urlBuilder.addQueryParameter("movieNm",mtitle);
            String url = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {

                Response response = client.newCall(request).execute();
                JsonParser parser = new JsonParser();
                JsonElement rootObject = parser.parse(response.body().charStream())
                        .getAsJsonObject().get("movieListResult").getAsJsonObject().get("movieList").getAsJsonArray().get(0); //원하는 항목(?)까지 찾아 들어가야 한다.

                String code = rootObject.getAsJsonObject().get("movieCd").toString();
                return code;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //요청결과를 여기서 처리한다. 화면에 출력하기등...
            if(result!=null){
                Log.d("!!Result:", result);
                result=result.replace("\"", "");
                movieCode=result;
            }
            else{
                Log.d("null이세요?","null입니다..");
            }

        }
    }
}


