package com.example.m_feelm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.m_feelm.model.Item;
import com.example.m_feelm.model.Movie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.apmem.tools.layouts.FlowLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatBotActivity extends AppCompatActivity{
    private RecyclerView recyclerView;
    private FlowLayout btn_wrapper;
    private ChatAdapter mAdapter;
    private ArrayList messageArrayList;
    private boolean initialRequest;
    private ImageButton back_btn;
    ProgressDialog progressDialog;
    private static String TAG = "ChatBotActivity";
    private Context mContext;
    String genre_txt[] = {"코메디","액션","로맨스","스릴러","공포","판타지","SF","아동"};
    String nation_txt[]={"뉴질랜드","대만","대한민국","미국","멕시코","베트남","싱가포르","스페인","일본","영국","중국","태국","필리핀","없음"};
    String yesno_txt[]={"있음","없음"};
    String yesno_date_txt[]={"개봉시기 선택","잘 모르겠어요"};

    private ArrayList<String> myMovieTitle=new ArrayList<>();
    ArrayList<Button> myMovieTitle_btn=new ArrayList<>();

    Button genre_list_btn[]=new Button[9];
    Button nation_list_btn[]=new Button[14];
    Button yesno_btn[]=new Button[2];
    Button yesno_date_btn[]=new Button[2];

    String text;
    String nation_text;
    String like_actor_text;
    String date_text;
    String select_like_movie;
    int tot_number;
    int rand_number;
    StatisticMovieItem[] posts = new StatisticMovieItem[10];
    KeywordsItem[] keywordlist = new KeywordsItem[10];
    String[] result_text= new String[5];
    ArrayList<String> arrayList = new ArrayList<>();
    String[] keywords_text;
    MyAsyncTask2 task2 = new MyAsyncTask2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        mContext = getApplicationContext();

        //inputMessage = findViewById(R.id.message);
        //btnSend = findViewById(R.id.btn_send);
//        String customFont = "Montserrat-Regular.ttf";
//        Typeface typeface = Typeface.createFromAsset(getAssets(), customFont);
//        inputMessage.setTypeface(typeface);
        recyclerView = findViewById(R.id.recycler_view);
        btn_wrapper = findViewById(R.id.choice_btn_wrapper);
        back_btn = findViewById(R.id.back_btn);
        progressDialog = new ProgressDialog(this);

        messageArrayList = new ArrayList<>();
        mAdapter = new ChatAdapter(messageArrayList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.scrollToPosition(mAdapter.getItemCount());
        this.initialRequest = true;

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //db에 있는 영화제목들 가져오는 함수
        setMyMovieTitleText();

        sendMessage();

        backgroundThread();

    }

    private void setMyMovieTitleText()
    {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mReference=mDatabase.getReference("User_review");

        mReference.orderByChild("id").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myMovieTitle.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    UserReview review=child.getValue(UserReview.class);
                    myMovieTitle.add(review.getTitle());
                    for(String title:myMovieTitle)
                    {
                        Log.d("User val", title);
                    }
                    //Log.d("User val", review.getTitle());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("fail","datebase 읽어오기 Error");
            }
        });
    }

    private void GetKeywords() {
        MyAsyncTask3 async3 = new MyAsyncTask3();
        async3.execute();
    }

    private void sendMessage(){
        if (this.initialRequest) {
            Message inputMessage = new Message();
            inputMessage.setMessage("무엇을 도와드릴까요?");
            inputMessage.setId("100");
            messageArrayList.add(inputMessage);
        }else{
            Message inputMessage = new Message();
            inputMessage.setMessage("무엇을 도와드릴까요?");
            inputMessage.setId("1");
            messageArrayList.add(inputMessage);
            this.initialRequest=false;
        }
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setStroke(5, Color.rgb(250,100,0));
        drawable.setColor(Color.WHITE);
        drawable.setCornerRadius(50.0f);
        //drawable.setSize(50,50);
        // 버튼 생성
        Button fit_btn = new Button(this);
        fit_btn.setText("맞춤 영화 추천");
        fit_btn.setBackgroundDrawable(drawable);
        Button genre_btn = new Button(this);
        genre_btn.setText("장르별 영화 추천");
        genre_btn.setBackgroundDrawable(drawable);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0,0,0,20);

        fit_btn.setLayoutParams(lp);
        genre_btn.setLayoutParams(lp);

        genre_list_btn = new Button[]{new Button(getApplicationContext()), new Button(getApplicationContext()), new Button(getApplicationContext()), new Button(getApplicationContext()), new Button(getApplicationContext()),
                new Button(getApplicationContext()), new Button(getApplicationContext()), new Button(getApplicationContext())};

        nation_list_btn=new Button[]{new Button(getApplicationContext()), new Button(getApplicationContext()), new Button(getApplicationContext()), new Button(getApplicationContext()), new Button(getApplicationContext()),
                new Button(getApplicationContext()), new Button(getApplicationContext()), new Button(getApplicationContext()), new Button(getApplicationContext()), new Button(getApplicationContext()),
                new Button(getApplicationContext()), new Button(getApplicationContext()), new Button(getApplicationContext()),new Button(getApplicationContext())};

        yesno_btn=new Button[]{new Button(getApplicationContext()), new Button(getApplicationContext())};

        yesno_date_btn=new Button[]{new Button(getApplicationContext()), new Button(getApplicationContext())};

        for(int i=0; i<8; i++) {
            genre_list_btn[i].setText(genre_txt[i]);
            //genre_list_btn[i].setBackgroundDrawable(drawable);
            //genre_list_btn[i].setLayoutParams(lp);
        }

        for(int i=0; i<14;i++)
        {
            nation_list_btn[i].setText(nation_txt[i]);
            //nation_list_btn[i].setBackgroundDrawable(drawable);
            //nation_list_btn[i].setLayoutParams(lp);
        }

        for(int i=0; i<2;i++)
        {
            yesno_btn[i].setText(yesno_txt[i]);
            //nation_list_btn[i].setBackgroundDrawable(drawable);
            //nation_list_btn[i].setLayoutParams(lp);
        }

        for(int i=0; i<2;i++)
        {
            yesno_date_btn[i].setText(yesno_date_txt[i]);
            //nation_list_btn[i].setBackgroundDrawable(drawable);
            //nation_list_btn[i].setLayoutParams(lp);
        }

        //
        btn_wrapper.addView(fit_btn);
        btn_wrapper.addView(genre_btn);

        fit_btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Message outMessage = new Message();
            outMessage.setMessage(fit_btn.getText().toString());
            outMessage.setId("1");
            messageArrayList.add(outMessage);
            btn_wrapper.removeAllViews();

            //내 db에 있는 영화제목들 가져오기
            Message inputMessage = new Message();
            inputMessage.setMessage("관람하신 영화 중 하나를 골라주세요!");
            inputMessage.setId("100");
            messageArrayList.add(inputMessage);

            for(String title : myMovieTitle)
            {
                Button btn = new Button(ChatBotActivity.this);
                btn.setText(title);
                myMovieTitle_btn.add(btn);
            }

            for(Button btn:myMovieTitle_btn)
            {
                btn_wrapper.addView(btn);
            }
            System.out.println(myMovieTitle_btn.size());

            for(int i=0; i<myMovieTitle_btn.size();i++)
            {
                final int current=i;
                final Button tempButton = myMovieTitle_btn.get(current);
                tempButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println(tempButton.getText().toString());
                        select_like_movie=tempButton.getText().toString();
                        btn_wrapper.removeAllViews();
                        //키워드 가져오기 함수
                        GetKeywords();
                    }
                });
            }
           backgroundThread();
        }
        });


        genre_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message outMessage = new Message();
                outMessage.setMessage(genre_btn.getText().toString());
                outMessage.setId("1");
                messageArrayList.add(outMessage);
                btn_wrapper.removeAllViews();

                Message inputMessage = new Message();
                inputMessage.setMessage("어떤 장르를 좋아하세요?");
                inputMessage.setId("100");
                messageArrayList.add(inputMessage);

                for(int i=0;i<genre_list_btn.length;i++){
                    btn_wrapper.addView(genre_list_btn[i]);
                }
                backgroundThread();
            }
        });

        for(int i=0;i<genre_list_btn.length; i++){
            final Button button = genre_list_btn[i];
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message outMessage = new Message();
                    outMessage.setMessage(button.getText().toString());
                    System.out.println(outMessage.getMessage());
                    outMessage.setId("1");
                    messageArrayList.add(outMessage);
                    btn_wrapper.removeAllViews();

                    //영화추천
                    //getMovies(button.getText().toString());
                    text = button.getText().toString();

                    /*Message inputMessage = new Message();
                    inputMessage.setMessage(button.getText().toString()+" 영화 추천을 시작합니다.");
                    inputMessage.setId("100");
                    messageArrayList.add(inputMessage);*/

                    Message inputMessage = new Message();
                    inputMessage.setMessage("어느 나라의 영화를 원하시나요?");
                    inputMessage.setId("100");
                    messageArrayList.add(inputMessage);

                    for(int i=0;i<14;i++){
                        btn_wrapper.addView(nation_list_btn[i]);
                    }

                    /*MyAsyncTask async = new MyAsyncTask();
                    async.execute();*/

                    backgroundThread();
                }
            });
        }

        for(int i=0;i<nation_list_btn.length; i++){
            final Button button = nation_list_btn[i];
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message outMessage = new Message();
                    outMessage.setMessage(button.getText().toString());
                    System.out.println(outMessage.getMessage());
                    outMessage.setId("1");
                    messageArrayList.add(outMessage);
                    btn_wrapper.removeAllViews();

                    //선택한 나라 text
                    //getMovies(button.getText().toString());
                    nation_text = button.getText().toString();

                    Message inputMessage = new Message();
                    inputMessage.setMessage("선호하는 배우가 있으신가요?");
                    inputMessage.setId("100");
                    messageArrayList.add(inputMessage);

                    for(int i=0;i<2;i++){
                        btn_wrapper.addView(yesno_btn[i]);
                    }

                    /*MyAsyncTask async = new MyAsyncTask();
                    async.execute();*/

                    backgroundThread();
                }
            });
        }

        for(int i=0;i<yesno_btn.length; i++){
            final Button button = yesno_btn[i];
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(button.getText().toString()=="있음")
                    {
                        AlertDialog.Builder ad = new AlertDialog.Builder(ChatBotActivity.this);

                        ad.setTitle("선호하는 배우를 입력해주세요");       // 제목 설정

                        final EditText et = new EditText(ChatBotActivity.this);
                        ad.setView(et);

                        // 확인 버튼 설정
                        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.v(TAG, "Yes Btn Click");

                                // Text 값 받아서 로그 남기기
                                like_actor_text = et.getText().toString();
                                Log.v(TAG, like_actor_text);
                                System.out.println(like_actor_text);

                                dialog.dismiss();     //닫기
                                // Event

                                Message outMessage = new Message();
                                outMessage.setMessage(like_actor_text);
                                System.out.println(outMessage.getMessage());
                                outMessage.setId("1");
                                messageArrayList.add(outMessage);
                                btn_wrapper.removeAllViews();

                                Message inputMessage = new Message();
                                inputMessage.setMessage("언제 개봉한 영화를 원하시나요?");
                                inputMessage.setId("100");
                                messageArrayList.add(inputMessage);

                                for(int i=0;i<2;i++){
                                    btn_wrapper.addView(yesno_date_btn[i]);
                                }
                            }
                        });

                        // 취소 버튼 설정
                        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.v(TAG,"No Btn Click");
                                dialog.dismiss();     //닫기
                                // Event
                            }
                        });

                        ad.show();
                    }else if(button.getText().toString()=="없음")
                    {
                        like_actor_text="없음";
                        Message outMessage = new Message();
                        outMessage.setMessage(like_actor_text);
                        System.out.println(outMessage.getMessage());
                        outMessage.setId("1");
                        messageArrayList.add(outMessage);
                        btn_wrapper.removeAllViews();

                        Message inputMessage = new Message();
                        inputMessage.setMessage("언제 개봉한 영화를 원하시나요?");
                        inputMessage.setId("100");
                        messageArrayList.add(inputMessage);

                        for(int i=0;i<2;i++){
                            btn_wrapper.addView(yesno_date_btn[i]);
                        }
                    }



                    backgroundThread();
                }
            });
        }

        for(int i=0;i<yesno_date_btn.length; i++){
            final Button button = yesno_date_btn[i];
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(button.getText().toString()=="개봉시기 선택") {
                        /*Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);

                        AlertDialog.Builder ad = new AlertDialog.Builder(ChatBotActivity.this);

                        final NumberPicker numberPicker = new NumberPicker(getApplicationContext());
                        numberPicker.setMinValue(year-100);
                        numberPicker.setMaxValue(year);
                        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                        numberPicker.setWrapSelectorWheel(false);
                        numberPicker.setValue(year);

                        ad.setView(numberPicker);*/
                        AlertDialog.Builder ad = new AlertDialog.Builder(ChatBotActivity.this);
                        ad.setTitle("개봉한 시기(년도)를 입력해주세요");       // 제목 설정

                        final EditText et = new EditText(ChatBotActivity.this);
                        ad.setView(et);
                        // 확인 버튼 설정
                        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.v(TAG, "Yes Btn Click");

                                // Text 값 받아서 로그 남기기
                                //date_text = Integer.toString(numberPicker.getValue());
                                date_text = et.getText().toString();

                                Log.v(TAG, date_text);
                                System.out.println("연도 : "+date_text);

                                dialog.dismiss();     //닫기
                                // Event

                                Message outMessage = new Message();
                                outMessage.setMessage(date_text);
                                System.out.println(outMessage.getMessage());
                                outMessage.setId("1");
                                messageArrayList.add(outMessage);
                                btn_wrapper.removeAllViews();

                                Message inputMessage = new Message();
                                inputMessage.setMessage("영화 추천을 시작합니다");
                                inputMessage.setId("100");
                                messageArrayList.add(inputMessage);

                                progressDialog.setMessage("추천중입니다. 기다려 주세요...");
                                progressDialog.show();

                                MyAsyncTask async = new MyAsyncTask();
                                async.execute();
                            }
                        });

                        // 취소 버튼 설정
                        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.v(TAG,"No Btn Click");
                                dialog.dismiss();     //닫기
                                // Event
                            }
                        });

                        ad.show();


                    } else if(button.getText().toString()=="잘 모르겠어요")
                    {
                        date_text="없음";

                        Message outMessage = new Message();
                        outMessage.setMessage(date_text);
                        System.out.println(outMessage.getMessage());
                        outMessage.setId("1");
                        messageArrayList.add(outMessage);
                        btn_wrapper.removeAllViews();

                        Message inputMessage = new Message();
                        inputMessage.setMessage("영화 추천을 시작합니다");
                        inputMessage.setId("100");
                        messageArrayList.add(inputMessage);

                        progressDialog.setMessage("추천중입니다. 기다려 주세요...");
                        progressDialog.show();

                        MyAsyncTask async = new MyAsyncTask();
                        async.execute();


                    }

                    backgroundThread();
                }
            });
        }

        backgroundThread();
    }



    private void backgroundThread(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("ttest");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            if (mAdapter.getItemCount() > 1) {
                                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);

                            }

                        }
                    });
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    // 영화 가져오기
    class MyAsyncTask extends AsyncTask<String,Void, Integer> {
        OkHttpClient client = new OkHttpClient();
        @Override
        protected Integer doInBackground(String... parmas) {
        //http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp?ServiceKey=UI6ZF443843L2KV91ZT5&genre
            StringBuilder urlBuilder = new StringBuilder("http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json.jsp?collection=kmdb_new");
            //HttpUrl.Builder urlBuilder = HttpUrl.parse("http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp").newBuilder();
            try {
                urlBuilder.append("&"+ URLEncoder.encode("ServiceKey","UTF-8")+"=UI6ZF443843L2KV91ZT5");
                urlBuilder.append("&"+ URLEncoder.encode("genre","UTF-8")+"="+URLEncoder.encode(text,"UTF-8")+"&"+URLEncoder.encode("listCount","UTF-8")+"=1000"+"&"+URLEncoder.encode("ratedYn","UTF-8")+"=Y");
                if(nation_text!="없음")
                {
                    urlBuilder.append("&"+ URLEncoder.encode("nation","UTF-8")+"="+URLEncoder.encode(nation_text,"UTF-8"));
                }
                if(like_actor_text!="없음")
                {
                    urlBuilder.append("&"+ URLEncoder.encode("actor","UTF-8")+"="+URLEncoder.encode(like_actor_text,"UTF-8"));
                }
                if(date_text!="없음")
                {
                    urlBuilder.append("&"+ URLEncoder.encode("createDte","UTF-8")+"="+URLEncoder.encode(date_text,"UTF-8"));
                }

                URL url = new URL(urlBuilder.toString());
                System.out.println(url);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-type", "application/json");
                System.out.println("Response code: " + conn.getResponseCode());

                BufferedReader rd;
                if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                StringBuilder sb = new StringBuilder();
                String line = null;
                while (true) {
                    try {
                        if (!((line = rd.readLine()) != null)) break;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    sb.append(line);
                }

                rd.close();
                conn.disconnect();
                String str = sb.toString();
                System.out.println(str);
                JsonParser parser = new JsonParser();
                System.out.println(parser.parse(str).getClass().getName());
                JsonElement element = (parser.parse(str)).getAsJsonObject().get("Data").getAsJsonArray().get(0).getAsJsonObject().get("Result");
                Gson gson = new Gson();
                posts = gson.fromJson(element, StatisticMovieItem[].class);
                if(posts==null){
                    return 0;
                }
                return posts.length;

            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            } catch (ProtocolException ex) {
                ex.printStackTrace();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return 0;
        }


        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            //요청결과를 여기서 처리한다. 화면에 출력하기등...
            if(result!=0){
                tot_number=result;
                System.out.println("tot_number : "+tot_number);
                rand_number = (int)(Math.random()*(tot_number-5));
                System.out.println("random : "+rand_number);

                if(tot_number<=5){
                    //총 개수가 5개가 넘지 않을 때 처리
                    rand_number=0;
                    for (int i=rand_number;i<tot_number;i++){
                        //ArrayList에 result 값을 넣어주기
                        if(posts[i].getMovieNm()!=null) {
                            result_text[i]=(posts[i].getMovieNm());

                            //System.out.println(posts[i].getMovieNm());
                            //System.out.println(posts[i].getImage());
                            //Message inputMessage = new Message();
                            //inputMessage.setId("100");
                            //inputMessage.setMessage(posts[i].getMovieNm());
                            //messageArrayList.add(inputMessage);
                            //System.out.println("fail" + posts[i].getImage());
                            /*if (posts[i].getImage() != null) {
                                Message inputMessage2 = new Message(posts[i].getImage());
                                messageArrayList.add(inputMessage2);
                            }*/
                        }
                    }
                }else {
                    int cnt=0;
                    for (int i = rand_number; i < rand_number + 5; i++) {
                        //ArrayList에 result 값을 넣어주기
                        if (posts[i].getMovieNm() != null) {
                            result_text[cnt]=(posts[i].getMovieNm());
                            cnt++;
                        }
                        /*if (posts[i].getImage() != null) {
                            Message inputMessage2 = new Message(posts[i].getImage());
                            messageArrayList.add(inputMessage2);
                        }*/
                    }

                }

                System.out.println(result_text.length);

                for(int i=0; i<result_text.length; ++i)
                {
                    System.out.println(result_text[i]);
                }

                //naver poster 가져오는거 다시해보기
                task2.execute();


            }
            if(result==0)
            {
                Message inputMessage = new Message();
                inputMessage.setMessage("추천 결과가 없습니다.");
                inputMessage.setId("100");
                messageArrayList.add(inputMessage);
            }
            backgroundThread();
        }
    }

    public String removeTag(String html) throws Exception {
        return html.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
    }

    public String removewords(String title) {
        title = title.replaceAll("!HS", "");
        title = title.replaceAll("!HE", "");
        title = title.replaceAll("!HS2", "");
        return title;
    }

    public class MyAsyncTask2 extends AsyncTask<Void,Void,Void>{
        ArrayList<Item> movies;
        Item item;
        // 영화 가져오기
        public void getMovies(final String title, int position) {
            ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
            Call<Movie> call = apiInterface.getMovies(title, 50, 1);
            call.enqueue(new Callback<Movie>() {
                @Override
                public void onResponse(@NonNull Call<Movie> call, @NonNull retrofit2.Response<Movie> response) {
                    if(response.isSuccessful()) {
                        movies = new ArrayList(response.body().getItems());
                        //!!!!!!!제목에 맞게 포스터 나오게 해야함!
                        //item=movies.get(0);
                        /*for(int i=0; i<movies.size(); i++)
                        {
                            System.out.println(i+movies.get(i).getTitle());
                        }*/
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
                                    Message inputMessage2 = new Message(item.getImage(), title);
                                    inputMessage2.setId("300");
                                    messageArrayList.add(inputMessage2);
                                }
                                break;
                            }else{
                                i++;
                            }
                        }

                        backgroundThread();
                        arrayList.add(item.getImage());
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
            progressDialog.dismiss();

            for(int i=0;i<result_text.length; i++){
                getMovies(result_text[i],i+1);
            }


            backgroundThread();
            return null;
        }

    }

    //키워드 가져오기
    class MyAsyncTask3 extends AsyncTask<String,Void, Integer> {
        OkHttpClient client = new OkHttpClient();
        @Override
        protected Integer doInBackground(String... parmas) {
            //http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp?ServiceKey=UI6ZF443843L2KV91ZT5&genre
            StringBuilder urlBuilder = new StringBuilder("http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json.jsp?collection=kmdb_new");
            //HttpUrl.Builder urlBuilder = HttpUrl.parse("http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp").newBuilder();
            try {
                urlBuilder.append("&"+ URLEncoder.encode("ServiceKey","UTF-8")+"=UI6ZF443843L2KV91ZT5");
                urlBuilder.append("&"+ URLEncoder.encode("query","UTF-8")+"="+URLEncoder.encode(select_like_movie,"UTF-8")+"&"+URLEncoder.encode("listCount","UTF-8")+"=1000"+"&"+URLEncoder.encode("ratedYn","UTF-8")+"=Y");


                URL url = new URL(urlBuilder.toString());
                System.out.println(url);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-type", "application/json");
                System.out.println("Response code: " + conn.getResponseCode());

                BufferedReader rd;
                if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                StringBuilder sb = new StringBuilder();
                String line = null;
                while (true) {
                    try {
                        if (!((line = rd.readLine()) != null)) break;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    sb.append(line);
                }

                rd.close();
                conn.disconnect();
                String str = sb.toString();
                System.out.println(str);
                JsonParser parser = new JsonParser();
                System.out.println(parser.parse(str).getClass().getName());
                JsonElement element = (parser.parse(str)).getAsJsonObject().get("Data").getAsJsonArray().get(0).getAsJsonObject().get("Result");
                Gson gson = new Gson();
                keywordlist = gson.fromJson(element, KeywordsItem[].class);
                if(keywordlist==null){
                    return 0;
                }
                return keywordlist.length;

            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            } catch (ProtocolException ex) {
                ex.printStackTrace();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return 0;
        }


        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            String text="";
            for(int i=0; i<result; i++){
                //System.out.println(select_like_movie.replaceAll(" ","").equals(removewords(keywordlist[i].getMovieNm().replaceAll(" ",""))));
                if(select_like_movie.replaceAll(" ","").equals(removewords(keywordlist[i].getMovieNm().replaceAll(" ","")))) {
                    System.out.println(keywordlist[i].getKeywords());
                    text = keywordlist[i].getKeywords();
                    keywords_text=text.split(",");
                    /*keywords_text.add(keywordlist[i].getKeywords());*/
                }
            }

            for(String word:keywords_text)
            {
                System.out.println(word);
            }


            backgroundThread();
        }
    }

//    // Sending a message to Watson Assistant Service
//    private void sendMessage() {
//
//       inputmessage = this.inputMessage.getText().toString().trim();
//        if (!this.initialRequest) {
//            Message inputMessage = new Message();
//            inputMessage.setMessage(inputmessage);
//            inputMessage.setId("1");
//            messageArrayList.add(inputMessage);
//        } else {
//            Message inputMessage = new Message();
//            inputMessage.setMessage(inputmessage);
//            inputMessage.setId("100");
//            this.initialRequest = false;
//            //Toast.makeText(getApplicationContext(), "Tap on the message for Voice", Toast.LENGTH_LONG).show();
//
//        }
//
//        this.inputMessage.setText("");
//        mAdapter.notifyDataSetChanged();
//
//        if (watsonAssistantSession == null) {
//            ServiceCall<SessionResponse> call = watsonAssistant.createSession(new CreateSessionOptions.Builder().assistantId(mContext.getString(R.string.assistant_id)).build());
//            //watsonAssistantSession = call.execute();
//            System.out.print("test");
//        }
////        BackgroundThread thread = new BackgroundThread();
////
////        thread.start();
//        Thread thread = new Thread(new Runnable() {
//            public void run() {
//                try {
//                    if (watsonAssistantSession == null) {
//                        ServiceCall<SessionResponse> call = watsonAssistant.createSession(new CreateSessionOptions.Builder().assistantId(mContext.getString(R.string.assistant_id)).build());
//                        watsonAssistantSession = call.execute();
//                    }
//
//                    MessageInput input = new MessageInput.Builder()
//                            .text(inputmessage)
//                            .build();
//                    MessageOptions options = new MessageOptions.Builder()
//                            .assistantId(mContext.getString(R.string.assistant_id))
//                            .input(input)
//                            .sessionId(watsonAssistantSession.getResult().getSessionId())
//                            .build();
//                    Response<MessageResponse> response = watsonAssistant.message(options).execute();
//                    Log.i(TAG, "run: " + response.getResult());
//                    if (response != null &&
//                            response.getResult().getOutput() != null &&
//                            !response.getResult().getOutput().getGeneric().isEmpty()) {
//
//                        List<RuntimeResponseGeneric> responses = response.getResult().getOutput().getGeneric();
//
//                        for (RuntimeResponseGeneric r : responses) {
//                            Message outMessage;
//                            switch (r.responseType()) {
//                                case "text":
//                                    outMessage = new Message();
//                                    outMessage.setMessage(r.text());
//                                    outMessage.setId("2");
//
//                                    messageArrayList.add(outMessage);
//
//                                    break;
//
//                                case "option":
//                                    outMessage =new Message();
//                                    String title = r.title();
//                                    String OptionsOutput = "";
//                                    for (int i = 0; i < r.options().size(); i++) {
//                                        DialogNodeOutputOptionsElement option = r.options().get(i);
//                                        OptionsOutput = OptionsOutput + option.getLabel() +"\n";
//
//                                    }
//                                    outMessage.setMessage(title + "\n" + OptionsOutput);
//                                    outMessage.setId("2");
//
//                                    messageArrayList.add(outMessage);
//
//                                    break;
//
//                                case "image":
//                                    outMessage = new Message(r);
//                                    messageArrayList.add(outMessage);
//
//                                    break;
//                                default:
//                                    Log.e("Error", "Unhandled message type");
//                            }
//                        }
//
//                                        runOnUiThread(new Runnable() {
//                                            public void run() {
//                                                mAdapter.notifyDataSetChanged();
//                                                if (mAdapter.getItemCount() > 1) {
//                                                    recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
//
//                                                }
//
//                                            }
//                                        });
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        thread.start();
//    }




}
