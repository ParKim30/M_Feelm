package com.example.m_feelm;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.m_feelm.model.Item;
import com.example.m_feelm.model.Movie;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FragmentStatistic_movie extends Fragment {

    private TextView[] title1 = new TextView[10];
    private TextView[] title2 = new TextView[10];
    private TextView[] title3 = new TextView[10];
    ImageView[] imageView1 = new ImageView[10];
    ImageView[] imageView2 = new ImageView[10];
    ImageView[] imageView3 = new ImageView[10];
    private Calendar calendar = new GregorianCalendar();
    StatisticMovieItem[] posts = new StatisticMovieItem[10];
    WebView webView, webView2;
    private String source;
    String[] stitle1=new String[10];
    String[] stitle2=new String[10];
    String[] stitle3=new String[10];
    public static int cnt=0;
    int i=0;

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
        title2 = new TextView[]{(TextView)root.findViewById(R.id.Atitle1), (TextView)root.findViewById(R.id.Atitle2), (TextView)root.findViewById(R.id.Atitle3),
                (TextView)root.findViewById(R.id.Atitle4), (TextView) root.findViewById(R.id.Atitle5), (TextView) root.findViewById(R.id.Atitle6),
                (TextView) root.findViewById(R.id.Atitle7), (TextView) root.findViewById(R.id.Atitle8), (TextView) root.findViewById(R.id.Atitle9), (TextView) root.findViewById(R.id.Atitle10)};
        title3 = new TextView[]{(TextView)root.findViewById(R.id.Ftitle1), (TextView)root.findViewById(R.id.Ftitle2), (TextView)root.findViewById(R.id.Ftitle3),
                (TextView)root.findViewById(R.id.Ftitle4), (TextView) root.findViewById(R.id.Ftitle5), (TextView) root.findViewById(R.id.Ftitle6),
                (TextView) root.findViewById(R.id.Ftitle7), (TextView) root.findViewById(R.id.Ftitle8), (TextView) root.findViewById(R.id.Ftitle9), (TextView) root.findViewById(R.id.Ftitle10)};
        imageView1 = new ImageView[]{(ImageView)root.findViewById(R.id.poster1),(ImageView)root.findViewById(R.id.poster2),(ImageView)root.findViewById(R.id.poster3),(ImageView)root.findViewById(R.id.poster4),
                (ImageView)root.findViewById(R.id.poster5),(ImageView)root.findViewById(R.id.poster6),(ImageView)root.findViewById(R.id.poster7),(ImageView)root.findViewById(R.id.poster8),
                (ImageView)root.findViewById(R.id.poster9),(ImageView)root.findViewById(R.id.poster10)};
        imageView2 = new ImageView[]{(ImageView)root.findViewById(R.id.Aposter1),(ImageView)root.findViewById(R.id.Aposter2),(ImageView)root.findViewById(R.id.Aposter3),(ImageView)root.findViewById(R.id.Aposter4),
                (ImageView)root.findViewById(R.id.Aposter5),(ImageView)root.findViewById(R.id.Aposter6),(ImageView)root.findViewById(R.id.Aposter7),(ImageView)root.findViewById(R.id.Aposter8),
                (ImageView)root.findViewById(R.id.Aposter9),(ImageView)root.findViewById(R.id.Aposter10)};
        imageView3 = new ImageView[]{(ImageView)root.findViewById(R.id.Fposter1),(ImageView)root.findViewById(R.id.Fposter2),(ImageView)root.findViewById(R.id.Fposter3),(ImageView)root.findViewById(R.id.Fposter4),
                (ImageView)root.findViewById(R.id.Fposter5),(ImageView)root.findViewById(R.id.Fposter6),(ImageView)root.findViewById(R.id.Fposter7),(ImageView)root.findViewById(R.id.Fposter8),
                (ImageView)root.findViewById(R.id.Fposter9),(ImageView)root.findViewById(R.id.Fposter10)};
        webView = (WebView)root.findViewById(R.id.webview);
        webView2=(WebView)root.findViewById(R.id.webview2);
        YearBoxOffice();
        FormerBoxOffice();

        MyAsyncTask async = new MyAsyncTask();
        async.execute();
        MyAsyncTask2 async2 = new MyAsyncTask2();
        async2.execute();

        return root;
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void YearBoxOffice(){
        //올해 박스오피스
        try {
            webView.getSettings().setJavaScriptEnabled(true);
            // 자바스크립트인터페이스 연결
            // 이걸 통해 자바스크립트 내에서 자바함수에 접근할 수 있음.
            MyJavascriptInterface webviewInterface = new MyJavascriptInterface(0);
            webView.addJavascriptInterface(webviewInterface, "Android");

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    // 자바스크립트 인터페이스로 연결되어 있는 getHTML를 실행
                    // 자바스크립트 기본 메소드로 html 소스를 통째로 지정해서 인자로 넘김
                    view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('tbody')[0].innerHTML);");
                }
            });
            webView.loadUrl("http://www.kobis.or.kr/kobis/business/stat/boxs/findYearlyBoxOfficeList.do");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void FormerBoxOffice(){
        try {
            webView2.getSettings().setJavaScriptEnabled(true);
            MyJavascriptInterface webviewInterface2 = new MyJavascriptInterface(1);
            webView2.addJavascriptInterface(webviewInterface2, "Android");

            webView2.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    // 자바스크립트 인터페이스로 연결되어 있는 getHTML를 실행
                    // 자바스크립트 기본 메소드로 html 소스를 통째로 지정해서 인자로 넘김
                    view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('tbody')[0].innerHTML);");
                }
            });
            webView2.loadUrl("http://www.kobis.or.kr/kobis/business/stat/boxs/findFormerBoxOfficeList.do");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

     @SuppressLint("StaticFieldLeak")
     class MyAsyncTask extends AsyncTask<String,Void,StatisticMovieItem[]>{
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
            if(result!=null){
                int j=0;
                for (StatisticMovieItem post: result){
                    if(j<10){
                    Log.d("FragmentStatistic_movie", post.getMovieNm());//영화제목 출력
                        stitle1[j] = post.getMovieNm().toString();
                    title1[j].setText(post.getMovieNm());
                    j++;
                    }
                }
            }
        }
    }


    public class MyAsyncTask2 extends AsyncTask<Void,Void,Void>{
        ArrayList<Item> movies;
        Item item;
        // 영화 가져오기
        public void getMovies(final String title, int position, int flag) {
            ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
            Call<Movie> call = apiInterface.getMovies(title, 1, 1);
            call.enqueue(new Callback<Movie>() {
                @Override
                public void onResponse(@NonNull Call<Movie> call, @NonNull retrofit2.Response<Movie> response) {
                    if(response.isSuccessful()) {
                        movies = new ArrayList(response.body().getItems());
                        item = movies.get(0);
                        if(flag==1) {
                            Glide.with(getContext())
                                    .load(item.getImage())
                                    .into(imageView1[position - 1]);
                        }else if(flag==2){
                            Glide.with(getContext())
                                    .load(item.getImage())
                                    .into(imageView2[position - 1]);
                        }else if(flag==3){
                            Glide.with(getContext())
                                    .load(item.getImage())
                                    .into(imageView3[position - 1]);
                        }

                    }else{
                        Log.e(TAG, response.message());
                    }
                }
                @Override
                public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {
                    Log.e(TAG, t.getMessage());
                }
            });
        }
        @Override
        protected Void doInBackground(Void... voids) {
            for(int i=0;i<10; i++){
                getMovies(stitle1[i],i+1,1);
            }
            try {
                Thread.sleep(7000);
                for(int i=0;i<10; i++){
                    getMovies(stitle2[i],i+1,2);
                }
                Thread.sleep(15000);
                for(int i=0;i<10; i++){
                    getMovies(stitle3[i],i+1,3);
                }
            }catch (Exception e){

            }

            return null;
        }

    }

        public class MyJavascriptInterface {
            private int flag;
             @JavascriptInterface
             public void getHtml(String html) {
            //위 자바스크립트가 호출되면 여기로 html이 반환됨
            source = html;
            Document doc = Jsoup.parse(source);
                 Elements element_title = doc.select("a");
                 int i=0;
                 for (Element title: element_title){
                     if(i<10){
                         if(flag==0) {
                             System.out.println("result : " + title.text());
                             stitle2[i]=title.text();
                             title2[i].setText(title.text().toString());
                         }else if(flag==1){
                             System.out.println("result : " + title.text().toString());
                             stitle3[i]=title.text();
                             title3[i].setText(title.text().toString());
                         }
                         i++;
                     }else{
                         break;
                     }
                 }
             }

            public MyJavascriptInterface(int flag){
                this.flag = flag;
            }
    }



}
