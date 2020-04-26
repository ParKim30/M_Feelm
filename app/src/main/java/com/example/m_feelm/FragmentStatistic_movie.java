package com.example.m_feelm;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FragmentStatistic_movie extends Fragment {

    TextView[] title1 = new TextView[10];
    Calendar calendar = new GregorianCalendar();

    public static FragmentStatistic_movie newInstance(){
        return new FragmentStatistic_movie();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_statistic_movie, container, false);
        title1 = new TextView[]{(TextView) root.findViewById(R.id.title1), (TextView) root.findViewById(R.id.title2), (TextView) root.findViewById(R.id.title3),
                (TextView) root.findViewById(R.id.title4), (TextView) root.findViewById(R.id.title5), (TextView) root.findViewById(R.id.title6),
                (TextView) root.findViewById(R.id.title7), (TextView) root.findViewById(R.id.title8), (TextView) root.findViewById(R.id.title9), (TextView) root.findViewById(R.id.title10)};

        MyAsyncTask async = new MyAsyncTask();
        async.execute();

        return root;
    }

    public class MyAsyncTask extends AsyncTask<String,Void,StatisticMovieItem[]>{
        OkHttpClient client = new OkHttpClient();
        @Override
        protected StatisticMovieItem[] doInBackground(String... parmas) {
            calendar.add(Calendar.DATE, -1); // 오늘날짜로부터 -1
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); // 날짜 포맷
            String today = sdf.format(calendar.getTime()); // String으로 저장

            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://www.kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json").newBuilder();
            urlBuilder.addQueryParameter("key","548c8a7d33aeab8ac6e933eaaaa89b7a");
            urlBuilder.addQueryParameter("targetDt",today);
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
                StatisticMovieItem[] posts = gson.fromJson(rootObject, StatisticMovieItem[].class);

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
            Log.d("Result:", result.toString());
            //title1.setText(result.toString());
            if(result.length > 0){
                int i=0;
                for (StatisticMovieItem post: result){
                    //Log.d("FragmentStatistic_movie", String.valueOf(post.getRank()));//랭킹
                    Log.d("FragmentStatistic_movie", post.getMovieNm());//영화제목 출력
//                    Log.d(TAG, post.getOpenDt());//개봉일 출력
                    title1[i].setText(post.getMovieNm());
                    i++;
                }
            }
        }
    }

}
