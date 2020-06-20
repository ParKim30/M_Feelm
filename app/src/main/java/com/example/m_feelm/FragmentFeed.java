package com.example.m_feelm;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ibm.cloud.sdk.core.http.Response;
import com.ibm.cloud.sdk.core.http.ServiceCall;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.assistant.v2.Assistant;
import com.ibm.watson.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.assistant.v2.model.DialogNodeOutputOptionsElement;
import com.ibm.watson.assistant.v2.model.MessageInput;
import com.ibm.watson.assistant.v2.model.MessageOptions;
import com.ibm.watson.assistant.v2.model.MessageResponse;
import com.ibm.watson.assistant.v2.model.RuntimeResponseGeneric;
import com.ibm.watson.assistant.v2.model.SessionResponse;

import java.util.ArrayList;
import java.util.List;

public class FragmentFeed  extends Fragment {
    ViewGroup viewGroup;
    private Context mContext;
    private RecyclerView recyclerView;
    private EditText inputMessage;
    private Button btnSend;
    private ChatAdapter mAdapter;
    private ArrayList messageArrayList;
    private boolean initialRequest;
    private static String TAG = "FragmentFeed";

    private Assistant watsonAssistant;
    private Response<SessionResponse> watsonAssistantSession;

    private void createServices() {
        watsonAssistant = new Assistant("2020-06-02", new IamAuthenticator(getContext().getString(R.string.assistant_apikey)));
        watsonAssistant.setServiceUrl(getContext().getString(R.string.assistant_url));

    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_feed, container, false);

        mContext = getContext();

        inputMessage = root.findViewById(R.id.message);
        btnSend = root.findViewById(R.id.btn_send);
        recyclerView = root.findViewById(R.id.recycler_view);

        messageArrayList = new ArrayList<>();
        mAdapter = new ChatAdapter(messageArrayList);
        //microphoneHelper = new MicrophoneHelper();

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        this.inputMessage.setText("");
        this.initialRequest = true;

//        int permission = ContextCompat.checkSelfPermission(mContext,
//                Manifest.permission.RECORD_AUDIO);
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            Log.i(TAG, "Permission to record denied");
//            makeRequest();
//        } else {
//            Log.i(TAG, "Permission to record was already granted");
//        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (checkInternetConnection()) {
                    sendMessage();
                //}
            }
        });
        createServices();
//        sendMessage();
        return root;
    }

    // Sending a message to Watson Assistant Service
    private void sendMessage() {

        final String inputmessage = this.inputMessage.getText().toString().trim();
        if (!this.initialRequest) {
            Message inputMessage = new Message();
            inputMessage.setMessage(inputmessage);
            inputMessage.setId("1");
            messageArrayList.add(inputMessage);
        } else {
            Message inputMessage = new Message();
            inputMessage.setMessage(inputmessage);
            inputMessage.setId("100");
            this.initialRequest = false;
            //Toast.makeText(getContext(), "Tap on the message for Voice", Toast.LENGTH_LONG).show();

        }

        this.inputMessage.setText("");
        mAdapter.notifyDataSetChanged();

        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    if (watsonAssistantSession == null) {
                        ServiceCall<SessionResponse> call = watsonAssistant.createSession(new CreateSessionOptions.Builder().assistantId(mContext.getString(R.string.assistant_id)).build());
                        watsonAssistantSession = call.execute();
                    }

                    MessageInput input = new MessageInput.Builder()
                            .text(inputmessage)
                            .build();
                    MessageOptions options = new MessageOptions.Builder()
                            .assistantId(mContext.getString(R.string.assistant_id))
                            .input(input)
                            .sessionId(watsonAssistantSession.getResult().getSessionId())
                            .build();
                    Response<MessageResponse> response = watsonAssistant.message(options).execute();
                    Log.i(TAG, "run: " + response.getResult());
                    if (response != null &&
                            response.getResult().getOutput() != null &&
                            !response.getResult().getOutput().getGeneric().isEmpty()) {

                        List<RuntimeResponseGeneric> responses = response.getResult().getOutput().getGeneric();

                        for (RuntimeResponseGeneric r : responses) {
                            Message outMessage;
                            switch (r.responseType()) {
                                case "text":
                                    outMessage = new Message();
                                    outMessage.setMessage(r.text());
                                    outMessage.setId("2");

                                    messageArrayList.add(outMessage);

                                    break;

                                case "option":
                                    outMessage =new Message();
                                    String title = r.title();
                                    String OptionsOutput = "";
                                    for (int i = 0; i < r.options().size(); i++) {
                                        DialogNodeOutputOptionsElement option = r.options().get(i);
                                        OptionsOutput = OptionsOutput + option.getLabel() +"\n";

                                    }
                                    outMessage.setMessage(title + "\n" + OptionsOutput);
                                    outMessage.setId("2");

                                    messageArrayList.add(outMessage);

                                    break;

                                case "image":
                                    outMessage = new Message(r);
                                    messageArrayList.add(outMessage);

                                    break;
                                default:
                                    Log.e("Error", "Unhandled message type");
                            }
                        }


//                                mAdapter.notifyDataSetChanged();
//                                if (mAdapter.getItemCount() > 1) {
//                                    recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
//
//                                }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        thread.start();
    }


    /**
     * Check Internet Connection
     *
     * @return
     */
//    private boolean checkInternetConnection() {
//        // get Connectivity Manager object to check connection
//        ConnectivityManager cm =
//                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        boolean isConnected = activeNetwork != null &&
//                activeNetwork.isConnectedOrConnecting();
//
//        // Check for network connections
//        if (isConnected) {
//            return true;
//        } else {
//            Toast.makeText(getContext(), " No Internet Connection available ", Toast.LENGTH_LONG).show();
//            return false;
//        }
//
//    }

}