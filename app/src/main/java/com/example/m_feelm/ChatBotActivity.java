package com.example.m_feelm;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
import java.util.Calendar;

import okhttp3.OkHttpClient;

public class ChatBotActivity extends AppCompatActivity{
    private RecyclerView recyclerView;
    private FlowLayout btn_wrapper;
    private ChatAdapter mAdapter;
    private ArrayList messageArrayList;
    private boolean initialRequest;
    private ImageButton back_btn;
    private static String TAG = "ChatBotActivity";
    private Context mContext;
    String genre_txt[] = {"드라마","코메디","액션","로맨스","스릴러","공포","범죄","판타지","SF","애니메이션"};
    String nation_txt[]={"뉴질랜드","대만","대한민국","미국","멕시코","베트남","싱가포르","스페인","일본","영국","중국","태국","필리핀","없음"};
    String yesno_txt[]={"있음","없음"};
    String yesno_date_txt[]={"개봉시기 선택","잘 모르겠어요"};
    Button genre_list_btn[]=new Button[10];
    Button nation_list_btn[]=new Button[14];
    Button yesno_btn[]=new Button[2];
    Button yesno_date_btn[]=new Button[2];

    String text;
    String nation_text;
    String like_actor_text;
    String date_text;
    StatisticMovieItem[] posts = new StatisticMovieItem[10];

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

        sendMessage();

        backgroundThread();

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
                new Button(getApplicationContext()), new Button(getApplicationContext()), new Button(getApplicationContext()), new Button(getApplicationContext()), new Button(getApplicationContext())};

        nation_list_btn=new Button[]{new Button(getApplicationContext()), new Button(getApplicationContext()), new Button(getApplicationContext()), new Button(getApplicationContext()), new Button(getApplicationContext()),
                new Button(getApplicationContext()), new Button(getApplicationContext()), new Button(getApplicationContext()), new Button(getApplicationContext()), new Button(getApplicationContext()),
                new Button(getApplicationContext()), new Button(getApplicationContext()), new Button(getApplicationContext()),new Button(getApplicationContext())};

        yesno_btn=new Button[]{new Button(getApplicationContext()), new Button(getApplicationContext())};

        yesno_date_btn=new Button[]{new Button(getApplicationContext()), new Button(getApplicationContext())};

        for(int i=0; i<10; i++) {
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

                for(int i=0;i<10;i++){
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
                                inputMessage.setMessage("영화추천을 시작합니다");
                                inputMessage.setId("100");
                                messageArrayList.add(inputMessage);
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
                        inputMessage.setMessage("영화추천을 시작합니다");
                        inputMessage.setId("100");
                        messageArrayList.add(inputMessage);
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
    class MyAsyncTask extends AsyncTask<String,Void,StatisticMovieItem[]> {
        OkHttpClient client = new OkHttpClient();
        @Override
        protected StatisticMovieItem[] doInBackground(String... parmas) {
//http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp?ServiceKey=UI6ZF443843L2KV91ZT5&genre
            StringBuilder urlBuilder = new StringBuilder("http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json.jsp?collection=kmdb_new");
            //HttpUrl.Builder urlBuilder = HttpUrl.parse("http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp").newBuilder();
            try {
                urlBuilder.append("&"+ URLEncoder.encode("ServiceKey","UTF-8")+"=UI6ZF443843L2KV91ZT5");
                urlBuilder.append("&"+ URLEncoder.encode("genre","UTF-8")+"="+URLEncoder.encode(text,"UTF-8")+"&"+URLEncoder.encode("listCount","UTF-8")+"=1000");
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
                return posts;
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            } catch (ProtocolException ex) {
                ex.printStackTrace();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }


//            Request request = new Request.Builder()
//                    .url(url)
//                    .build();
//            try {
//                Response response = client.newCall(request).execute();
//                Gson gson = new GsonBuilder().create();
//                JsonParser parser = new JsonParser();
//                //제공되는 오픈API데이터에서 어떤 항목을 가여올지 설정해야 하는데.... 음~
//                JsonElement rootObject = parser.parse(response.body().charStream())
//                        .getAsJsonObject().get("Data").getAsJsonObject().get("Result"); //원하는 항목(?)까지 찾아 들어가야 한다.
//                Log.d("result:",rootObject.getAsString());
//                StatisticMovieItem[] posts;
//                posts = gson.fromJson(rootObject, StatisticMovieItem[].class);
//                return posts;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            return null;
        }

        @Override
        protected void onPostExecute(StatisticMovieItem[] result) {
            super.onPostExecute(result);
            //요청결과를 여기서 처리한다. 화면에 출력하기등...
            if(result!=null){
                int j=0;
                for (StatisticMovieItem post: result){
                    if(j<2){
                        System.out.println(post.getMovieNm());
                        System.out.println(post.getImage());
                        j++;
                        Message inputMessage = new Message();
                        inputMessage.setId("100");
                        inputMessage.setMessage(post.getMovieNm());
                        messageArrayList.add(inputMessage);
                        System.out.println(post.getImage());
                        if(post.getImage()!=null) {
                            Message inputMessage2 = new Message(post.getImage());
                            messageArrayList.add(inputMessage2);
                        }

                    }
                }

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




    /**
     * Check Internet Connection
     *
     * @return
     */
    private boolean checkInternetConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        // Check for network connections
        if (isConnected) {
            return true;
        } else {
            Toast.makeText(this, " No Internet Connection available ", Toast.LENGTH_LONG).show();
            return false;
        }
    }

}
