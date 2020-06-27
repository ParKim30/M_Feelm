package com.example.m_feelm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
    }
}