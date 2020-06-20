package com.example.m_feelm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText email_login;
    private EditText pw_login;
    private Button login_btn;
    private Button signup_btn;
    FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_btn = (Button) findViewById(R.id.login_btn);
        email_login = (EditText) findViewById(R.id.et_email);
        pw_login = (EditText) findViewById(R.id.et_password);
        signup_btn = (Button)findViewById((R.id.signup_btn));

        firebaseAuth = firebaseAuth.getInstance();

        login_btn.setOnClickListener(this);
        signup_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==login_btn){
            String email = email_login.getText().toString().trim();
            String pwd = pw_login.getText().toString().trim();

            firebaseAuth.signInWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //Toast.makeText(LoginActivity.this,"mAuth. onComplete 함수" ,Toast.LENGTH_SHORT).show();
                            if (!task.isSuccessful()) {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidUserException e) {
                                    Toast.makeText(LoginActivity.this,"존재하지 않는 id 입니다." ,Toast.LENGTH_SHORT).show();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    Toast.makeText(LoginActivity.this,"이메일 형식이 맞지 않습니다." ,Toast.LENGTH_SHORT).show();
                                } catch (FirebaseNetworkException e) {
                                    Toast.makeText(LoginActivity.this,"Firebase NetworkException" ,Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(LoginActivity.this,"Exception" ,Toast.LENGTH_SHORT).show();
                                }

                            }else{

                                currentUser = firebaseAuth.getCurrentUser();

                                Toast.makeText(LoginActivity.this, "로그인 성공" + "/" + currentUser.getEmail() + "/" + currentUser.getUid() ,Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
        }else if(v==signup_btn){
            finish();
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));

        }




    }
    //로그아웃 안했으면, 즉 로그인 되어있으면 자동으로 메인페이지로 이동시키기
//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        currentUser = firebaseAuth.getCurrentUser();
//        if(currentUser != null){
//            startActivity(new Intent(LoginActivity.this, MainActivity.class));
//            finish();
//        }
//    }


}
