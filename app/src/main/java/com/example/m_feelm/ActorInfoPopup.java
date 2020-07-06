package com.example.m_feelm;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Constraints;

import com.example.m_feelm.model.Item;
import com.example.m_feelm.model.Movie;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.lang.reflect.Array;
import java.util.ArrayList;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

import static com.gun0912.tedpermission.TedPermission.TAG;

public class ActorInfoPopup extends Activity {
    String actorNm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.actor_popup_info);

        TextView name = findViewById(R.id.actorNm);

        Intent intent = getIntent();
        actorNm = intent.getStringExtra("actorNm");
        actorNm = actorNm.replace("#","");
        name.setText(actorNm);


        MyAsyncTask async = new MyAsyncTask();
        async.execute();




    }
    ArrayList<Item> movies;
    ArrayList<ActorInfoItem> actorInfo;
    String naverPosterUrl;
    Item item;
    // 영화 가져오기
    public void getMovies(final String title,int position) {
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<Movie> call = apiInterface.getMovies(title, 50, 1);
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull retrofit2.Response<Movie> response) {
                if(response.isSuccessful()) {
                    movies = new ArrayList(response.body().getItems());
                    int i=0;
                    while(i<movies.size()){
                        item = movies.get(i);
                        try {
                            String getTitle = removeTag(item.getTitle());
                            getTitle = getTitle.replaceAll(" ","");
                            item.setTitle(getTitle);
                            System.out.println("getTitle:"+getTitle);
                            System.out.println("kmdbTitle:"+title.replaceAll(" ",""));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println(item.getTitle().equals(title.replaceAll(" ","")));
                        if(item.getTitle().equals(title.replaceAll(" ",""))){
                            System.out.println("ㅁㅁㅁ"+title);
                            if(!(item.getImage().equals(""))) {
                                System.out.println("getmovie : "+item.getImage());
                                naverPosterUrl=item.getImage();
                            }
                            naverPosterUrl=item.getImage();
                            actorInfo.get(position).setPoster(naverPosterUrl);
                            break;
                        }else{
                            i++;
                        }
                    }

                   // arrayList.add(item.getImage());
                }else{
                    Log.e(TAG, response.message());
                }
            }
            @Override
            public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {
                Log.e(TAG, t.getMessage());
            }
            public String removeTag(String html) throws Exception {
                return html.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
            }
        });
    }
    class MyAsyncTask extends AsyncTask<String,Void, ArrayList<ActorInfoItem>> {
        OkHttpClient client = new OkHttpClient();
        @Override
        protected  ArrayList<ActorInfoItem> doInBackground(String... parmas) {

            String url;
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp").newBuilder();
            urlBuilder.addQueryParameter("ServiceKey", "UI6ZF443843L2KV91ZT5");
            urlBuilder.addQueryParameter("collection", "kmdb_new2");
            urlBuilder.addQueryParameter("detail", "Y");
            urlBuilder.addQueryParameter("actor", actorNm);
            url = urlBuilder.build().toString();


            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {

                Response response = client.newCall(request).execute();
                JsonParser parser = new JsonParser();
                JsonElement rootObject = parser.parse(response.body().charStream())
                        .getAsJsonObject().get("Data").getAsJsonArray().get(0).getAsJsonObject().get("Result");
                //원하는 항목(?)까지 찾아 들어가야 한다.


                ArrayList<ActorInfoItem> items=new ArrayList<ActorInfoItem>();
                for(int i=0;i<rootObject.getAsJsonArray().size();++i)
                {
                    String poster=rootObject.getAsJsonArray().get(i).getAsJsonObject().get("posters").toString();
                    String title=rootObject.getAsJsonArray().get(i).getAsJsonObject().get("title").toString().replaceAll("\"","");

                    ActorInfoItem item=new ActorInfoItem(title,poster);
                    items.add(item);
                }

                return items;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(ArrayList<ActorInfoItem> result) {
            super.onPostExecute(result);

            if(result!=null){

                    actorInfo=result;
                    for(int i=0;i<actorInfo.size();++i)
                    {
                        String[] arr = actorInfo.get(i).getPoster().split("\\|");

                        arr[0] = arr[0].substring(1);
                        if (arr[0].contains("\"")) {
                            int len = arr[0].length();
                            actorInfo.get(i).setPoster(arr[0].substring(0, len - 1));
                        } else actorInfo.get(i).setPoster(arr[0]);

                        if(actorInfo.get(i).getPoster().equals("")||actorInfo.get(i).getPoster()==null)
                        {
                            getMovies(actorInfo.get(i).getTitle(),i);
                            //if(naverPosterUrl==null) System.out.println("null이다!");
                            //actorInfo.get(i).setPoster(naverPosterUrl);
                        }
                        System.out.println("포스터"+actorInfo.get(i).getPoster());
                    }

                    // 커스텀 아답타 생성
                    ActorInfoItemAdapter adapter = new ActorInfoItemAdapter (
                            getApplicationContext(),
                            R.layout.actor_info_item,       // GridView 항목의 레이아웃 row.xml
                            actorInfo);    // 데이터

                    GridView gv = (GridView)findViewById(R.id.gridview);
                    gv.setAdapter(adapter);  // 커스텀 아답타를 GridView 에 적용
            }
            else{
                Log.d("null이세요?","null입니다..");
            }

        }
    }
}