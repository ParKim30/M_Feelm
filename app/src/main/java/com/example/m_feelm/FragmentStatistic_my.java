package com.example.m_feelm;

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
import android.widget.TextView;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

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

    ArrayList<UserReview> userReviews=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_statistic_my, container, false);
        myRanking(root);

        totalTime=root.findViewById(R.id.TotalTime);
        //MyAsyncTask async = new MyAsyncTask();
        //async.execute();
        return root;
    }
    /*class MyAsyncTask extends AsyncTask<String,Void,StatisticMovieItem[]> {
        OkHttpClient client = new OkHttpClient();

        @Override
        protected StatisticMovieItem[] doInBackground(String... parmas) {

            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json").newBuilder();
            urlBuilder.addQueryParameter("key", "2bc022ca249311ee687b6976e45237a4");
            String url = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                Gson gson = new GsonBuilder().create();
                JsonParser parser = new JsonParser();
                //제공되는 오픈API데이터에서 어떤 항목을 가여올지 설정해야 하는데.... 음~
                JsonElement rootObject = parser.parse(response.body().charStream())
                        .getAsJsonObject().get("boxOfficeResult").getAsJsonObject().get("dailyBoxOfficeList"); //원하는 항목(?)까지 찾아 들어가야 한다.
                posts = gson.fromJson(rootObject, StatisticMovieItem[].class);
                return posts;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(StatisticMovieItem[] result) {
            super.onPostExecute(result);
            //요청결과를 여기서 처리한다. 화면에 출력하기등...
            //Log.d("Result:", result.toString());
            //if(result.length > 0){
            int j = 0;
            for (StatisticMovieItem post : result) {
                if (j < 10) {
                    Log.d("FragmentStatistic_movie", post.getMovieNm());//영화제목 출력
                    stitle1[j] = post.getMovieNm().toString();
                    title1[j].setText(post.getMovieNm());
                    j++;
                }
            }
        }

        public void MyTotalTime(){

        }
    }
     */
    private void myRanking(View view){
        FirebaseUser user;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        TextView myRanking1=view.findViewById(R.id.myRanking1);
        TextView myRanking2=view.findViewById(R.id.myRanking2);
        TextView myRanking3=view.findViewById(R.id.myRanking3);


        mDatabase = FirebaseDatabase.getInstance();
        mReference=mDatabase.getReference("User_review");

        if(user!=null) {
            mReference.orderByChild("id").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                 @RequiresApi(api = Build.VERSION_CODES.N)
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     userReviews.clear();
                     for (DataSnapshot child : dataSnapshot.getChildren()) {
                         UserReview review=child.getValue(UserReview.class);
                         userReviews.add(review);
                         Log.d("User val", child.child("title").getValue().toString());
                         Log.d("User val", child.child("rating").getValue().toString());

                     }
                     userReviews.sort(Comparator.reverseOrder());
                     for(int i=0;i<userReviews.size();i++){
                         if(i<3){
                             myRanking1.setText(userReviews.get(i).getTitle());
                         }
                         else break;
                     }
                 }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("fail","datebase 읽어오기 Error");
                }
            });
        }
    }


}

