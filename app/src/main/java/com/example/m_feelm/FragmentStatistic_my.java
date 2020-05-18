package com.example.m_feelm;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentStatistic_my extends Fragment {


    public static FragmentStatistic_my newInstance(){
        return new FragmentStatistic_my();
    }

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mChild;
    private TextView totalTime;
    private int showTime=0;
    private TextView myRanking[]=new TextView[3];
    private ImageView rankingImg[]=new ImageView[3];

    private FirebaseUser user;

    private ArrayList<UserReview> userReviews=new ArrayList<>();
    private ArrayList<String> mCd=new ArrayList<>();
    private Map<String, Long> counts;
    private View root;
    private PieChart pieChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_statistic_my, container, false);

        totalTime=root.findViewById(R.id.TotalTime);
        myRanking(root);


        return root;
    }

    private void createChart(){
        Log.d("!!?Result:", String.valueOf(counts));
        pieChart = root.findViewById(R.id.piechart);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<>();
        for( String key : counts.keySet() ){
            String repKey=key.replace("\"","");
            yValues.add(new PieEntry(counts.get(key),repKey));
        }

//        yValues.add(new PieEntry(34f,"Japen"));
//        yValues.add(new PieEntry(23f,"USA"));
//        yValues.add(new PieEntry(14f,"UK"));
//        yValues.add(new PieEntry(35f,"India"));
//        yValues.add(new PieEntry(40f,"Russia"));
//        yValues.add(new PieEntry(40f,"Korea"));

        Description description = new Description();
        description.setText("영화 장르"); //라벨
        description.setTextSize(15);
        pieChart.setDescription(description);

        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic); //애니메이션

        PieDataSet dataSet = new PieDataSet(yValues,"genre");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);
    }
    class MyAsyncTask extends AsyncTask<String,Void, ArrayList<ArrayList<String>>> {
        OkHttpClient client = new OkHttpClient();
        @Override
        protected  ArrayList<ArrayList<String>> doInBackground(String... parmas) {

            ArrayList<ArrayList<String>> movieInfo=new ArrayList<>();
            ArrayList<String> showTime=new ArrayList<>();
            ArrayList<String> genre=new ArrayList<>();

            String[] url = new String[mCd.size()];
            Log.d("출력4", String.valueOf(mCd));
            for(int i=0;i<mCd.size();i++) {
                HttpUrl.Builder urlBuilder = HttpUrl.parse("http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json").newBuilder();
                urlBuilder.addQueryParameter("key", "2bc022ca249311ee687b6976e45237a4");
                urlBuilder.addQueryParameter("movieCd", mCd.get(i));

                url[i] = urlBuilder.build().toString();


                Request request = new Request.Builder()
                        .url(url[i])
                        .build();

                try {

                    Response response = client.newCall(request).execute();
                    JsonParser parser = new JsonParser();
                    JsonElement rootObject = parser.parse(response.body().charStream())
                            .getAsJsonObject().get("movieInfoResult").getAsJsonObject().get("movieInfo"); //원하는 항목(?)까지 찾아 들어가야 한다.


                    showTime.add(rootObject.getAsJsonObject().get("showTm").toString());
                    genre.add(rootObject.getAsJsonObject().get("genres").getAsJsonArray().get(0).getAsJsonObject().get("genreNm").toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            movieInfo.add(showTime);
            movieInfo.add(genre);
            return movieInfo;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute( ArrayList<ArrayList<String>> result) {
            super.onPostExecute(result);
            //요청결과를 여기서 처리한다. 화면에 출력하기등...
            if(result!=null){
                Log.d("!!Result:", String.valueOf(result.get(0)));
                for(int i=0;i<result.get(0).size();i++){
                    result.get(0).set(i,result.get(0).get(i).replace("\"", ""));
                    showTime+= Integer.parseInt(result.get(0).get(i));
                }
                //result=result.replace("\"", "");
                Log.d("!!Result:", String.valueOf(showTime));
                totalTime.setText(showTime+"분동안 영화 관람");

                counts = result.get(1).stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));

                Log.d("!!Result:", String.valueOf(counts));

                createChart();
            }
            else{
                Log.d("null이세요?","null입니다..");
            }

        }
    }

    private void myRanking(View view){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        myRanking[0]=view.findViewById(R.id.myRanking1);
        myRanking[1]=view.findViewById(R.id.myRanking2);
        myRanking[2]=view.findViewById(R.id.myRanking3);

        rankingImg[0]=view.findViewById(R.id.rankingImg1);
        rankingImg[1]=view.findViewById(R.id.rankingImg2);
        rankingImg[2]=view.findViewById(R.id.rankingImg3);

        mDatabase = FirebaseDatabase.getInstance();
        mReference=mDatabase.getReference("User_review");

        if(user!=null) {
            readData(new MyCallback(){
                @Override
                public void onCallback(ArrayList<String> value) {
                    Log.d("출력2", String.valueOf(value));
                    mCd.addAll(value);
                    Log.d("출력3", String.valueOf(mCd));


                    MyAsyncTask async = new MyAsyncTask();
                    async.execute();
                }
            });
        }
    }
    private void readData(MyCallback myCallback){
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
                userReviews.sort(Comparator.reverseOrder());

                ArrayList movieCd=new ArrayList();
                for(int i=0;i<userReviews.size();i++){
                    if(i<3){
                        myRanking[i].setText(userReviews.get(i).getTitle());
                        //Log.d("출력랭킹", userReviews.get(i).getRating());
                        Log.d("출력랭킹", userReviews.get(i).getTitle());
                        Glide.with(getContext())
                                .load(userReviews.get(i).getPosterUrl())
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(rankingImg[i]);
                    }
                    movieCd.add(userReviews.get(i).getMovieCode());
                }
                Log.d("출력", String.valueOf(movieCd));
                myCallback.onCallback(movieCd);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("fail","datebase 읽어오기 Error");
            }
        });
    }

    public interface MyCallback{
        void onCallback(ArrayList<String> value);
    }
}

