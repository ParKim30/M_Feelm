package com.example.m_feelm;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;


import org.apmem.tools.layouts.FlowLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WriteReview extends Activity {

    DatabaseReference mDBReference = null;
    HashMap<String, Object> childUpdates = null;
   // Map<String, Object> userValue = null;
    //Review userReview = null;
    String mtitle;
    String mposter;
    String mdirector;
    String movieCode="123";
    String runTime;
    FlowLayout fl;
    MyAsyncTask2 async2;
    MyAsyncTask3 async3;

    PopupWindow mPopupWindow;
    ProgressDialog progressDialog;

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
        async2=new MyAsyncTask2();
        async.execute();


        inputDB();

        async3 = new MyAsyncTask3();
        async3.execute();

    }
    private void inputImg(){
        ImageView m_poster=findViewById(R.id.moviePoster);
        if(mposter!=null) {
            Picasso.get().load(mposter).fit().into(m_poster);
        }
    }
    private void inputDB(){
        mtitle=getIntent().getStringExtra("title");
        String mrating=getIntent().getStringExtra("rating");
        mdirector=getIntent().getStringExtra("director");
        String mactor=getIntent().getStringExtra("actor");
        String mdate=getIntent().getStringExtra("date");
        mposter=getIntent().getStringExtra("poster");

        mactor = mactor.replace("|","/");
        String[] actors = mactor.split("/");

        for(int i=0; i<actors.length; i++)
            System.out.println(actors[i]);

        TextView m_title = findViewById(R.id.title);
        TextView m_director = findViewById(R.id.director);
        //TextView m_actor = findViewById(R.id.actor);
        FlowLayout fl = findViewById(R.id.flowlayout);
        RatingBar m_rating= findViewById(R.id.user_rating);
        TextView m_date = findViewById(R.id.pubDate);
        TextView m_ratingNum=findViewById(R.id.user_rating_num);
        TextView m_withPeople=findViewById(R.id.withPeople);

        TextView[] at = new TextView[actors.length];
        Button[] bt = new Button[actors.length];

        for(int i=0; i<actors.length-1; i++)
        {
            //at[i]= new TextView(this);
            String name;
            bt[i]=new Button(this);
            //bt[i].setBackground(ContextCompat.getDrawable(this, R.drawable.actor_btn_shape));
            bt[i].setClickable(true);
            bt[i].setTextSize(13);
            bt[i].setBackgroundColor(000);
            bt[i].setText("#"+actors[i]);
            fl.addView(bt[i]);
            name = bt[i].getText().toString();
            bt[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WriteReview.this,ActorInfoPopup.class);
                    intent.putExtra("actorNm",name);
                    startActivityForResult(intent,1);
//                    View popupView = getLayoutInflater().inflate(R.layout.actor_popup_info, null);
//                    mPopupWindow = new PopupWindow(popupView,1300, 2100);
//                    mPopupWindow.setFocusable(true);
//                    mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("actorNm",name);
                    System.out.println("success");
                }
            });
        }

        m_title.setText(Html.fromHtml(mtitle).toString());
        m_director.setText(mdirector);
        //m_actor.setText(mactor);
        m_rating.setRating(Float.parseFloat(mrating) / 2);
        m_date.setText(mdate);


        String ratingNum = String.format("%.1f", Float.parseFloat(mrating) / 2);

        m_ratingNum.setText(ratingNum);

//
//        Glide.with(this)
//                .load(mposter)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(m_poster);
//        Glide.with(this).load(mposter)
//                .dontTransform()
//                .into(m_poster);



        Button complete_btn = findViewById(R.id.complete);

        mDBReference = FirebaseDatabase.getInstance().getReference();
        childUpdates = new HashMap<>();

        EditText date=findViewById(R.id.watchDate);
        date.setOnClickListener(v -> new DatePickerDialog(WriteReview.this, myDatePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        //Switch share=findViewById(R.id.reviewShare);

        complete_btn.setOnClickListener(v -> {
                    FirebaseUser user;
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    user = firebaseAuth.getCurrentUser();


                    if(user!=null) {
                        //progressDialog.setMessage("ProgressDialog running...");
                        //progressDialog.show();

                        String id = user.getUid();
                        RatingBar rating = findViewById(R.id.my_rating);
                        EditText user_review = findViewById(R.id.Myreview);
                        //boolean feedYN = share.isChecked();
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
                        newPostRef.setValue(new Review(id, mtitle, date.getText().toString(), rating.getRating(), user_review.getText().toString(), write_date,movieCode,mposter,m_withPeople.getText().toString(),runTime));
                        //progressDialog.dismiss();
                    }
            finish();
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
                        .getAsJsonObject().get("movieListResult").getAsJsonObject().get("movieList").getAsJsonArray().get(0);

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
                Log.d("!!Result1:", result);
                result=result.replace("\"", "");
                movieCode=result;
                async2.execute();
            }
            else{
                Log.d("null이세요?","null입니다..");
            }

        }
    }

    class MyAsyncTask2 extends AsyncTask<String,Void, String> {
        OkHttpClient client = new OkHttpClient();
        @Override
        protected  String doInBackground(String... parmas) {

            ArrayList<ArrayList<String>> movieInfo=new ArrayList<>();
            ArrayList<String> showTime=new ArrayList<>();
            ArrayList<String> genre=new ArrayList<>();

            String url;
                HttpUrl.Builder urlBuilder = HttpUrl.parse("http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json").newBuilder();
                urlBuilder.addQueryParameter("key", "2bc022ca249311ee687b6976e45237a4");
                urlBuilder.addQueryParameter("movieCd", movieCode);

                url = urlBuilder.build().toString();


                Request request = new Request.Builder()
                        .url(url)
                        .build();

                try {

                    Response response = client.newCall(request).execute();
                    JsonParser parser = new JsonParser();
                    JsonElement rootObject = parser.parse(response.body().charStream())
                            .getAsJsonObject().get("movieInfoResult").getAsJsonObject().get("movieInfo"); //원하는 항목(?)까지 찾아 들어가야 한다.


                    String time=rootObject.getAsJsonObject().get("showTm").toString();
                    return time;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";
            }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //요청결과를 여기서 처리한다. 화면에 출력하기등...
            if(result!=null){
                Log.d("!!Result2:", String.valueOf(result));
                result.replace("\"", "");
                runTime=result;
            }
            else{
                Log.d("null이세요?","null입니다..");
            }

        }
    }
    class MyAsyncTask3 extends AsyncTask<String,Void, String> {
        OkHttpClient client = new OkHttpClient();
        @Override
        protected  String doInBackground(String... parmas) {

            String url;
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp").newBuilder();
            urlBuilder.addQueryParameter("ServiceKey", "UI6ZF443843L2KV91ZT5");
            urlBuilder.addQueryParameter("collection", "kmdb_new2");
            urlBuilder.addQueryParameter("detail", "Y");
            urlBuilder.addQueryParameter("title", mtitle);
            urlBuilder.addQueryParameter("director", mdirector);
            url = urlBuilder.build().toString();


            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {

                Response response = client.newCall(request).execute();
                JsonParser parser = new JsonParser();
                JsonElement rootObject = parser.parse(response.body().charStream())
                        .getAsJsonObject().get("Data").getAsJsonArray().get(0).getAsJsonObject().get("Result")
                        .getAsJsonArray().get(0);//원하는 항목(?)까지 찾아 들어가야 한다.


                String poster=rootObject.getAsJsonObject().get("posters").toString();
                return poster;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //요청결과를 여기서 처리한다. 화면에 출력하기등...
            Log.d("!!Result3:", result);
            if(result!=null){
                if(result.length()<5){

                }
                else {
                    Log.d("!!Result3:", result);
                    String[] arr = result.split("\\|");

                    arr[0] = arr[0].substring(1);
                    if (arr[0].contains("\"")) {
                        int len = arr[0].length();
                        mposter = arr[0].substring(0, len - 1);
                    } else mposter = arr[0];

                }
                inputImg();
            }
            else{
                Log.d("null이세요?","null입니다..");
            }

        }
    }
}


