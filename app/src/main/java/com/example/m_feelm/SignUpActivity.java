package com.example.m_feelm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import retrofit2.http.Tag;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    // 비밀번호 정규식
//    private static final Pattern PASSWORD_PATTERN= Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");
    //private static final String TAG = "EmailPassword";

    // 파이어베이스 인증 객체 생성
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    // 이메일과 비밀번호
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText checkPassword;
    private EditText nickName;
    private Button check_nickName_btn;
    private Button signUpBtn;
    ProgressDialog progressDialog;
    boolean flag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // 파이어베이스 인증 객체 선언
        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText)findViewById(R.id.et_email);
        editTextPassword = (EditText)findViewById(R.id.et_password);
        checkPassword = (EditText)findViewById(R.id.check_password);
        nickName = (EditText)findViewById(R.id.nickname);
        signUpBtn = (Button)findViewById(R.id.sign_up_btn);
        check_nickName_btn=(Button)findViewById(R.id.check_nickname_btn);
        progressDialog = new ProgressDialog(this);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference().child("User_nickname").getRef();

        signUpBtn.setOnClickListener(this);
        check_nickName_btn.setOnClickListener(this);

        DatabaseReference postsRef = database.getReference().child("User_nickname");

        DatabaseReference newPostRef = postsRef.push();

    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();
        String checkpassword = checkPassword.getText().toString();
        String nickname = nickName.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Email을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(nickname)){
            Toast.makeText(this, "nickname을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!(password.equals(checkpassword))){
            Toast.makeText(this, "Password를 다시 한 번 확인해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        //email과 password가 제대로 입력되어 있다면 계속 진행된다.
        progressDialog.setMessage("등록중입니다. 기다려 주세요...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Toast.makeText(SignUpActivity.this, "1!", Toast.LENGTH_SHORT).show();
                        if(task.isSuccessful()){
                            //Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(SignUpActivity.this, "등록 완료!", Toast.LENGTH_SHORT).show();
                            DatabaseReference postsRef = database.getReference().child("User_nickname");
                            DatabaseReference newPostRef = postsRef.push();
                            newPostRef.setValue(new Nickname(nickname,firebaseAuth.getUid()));
//                            newPostRef.setValue(nickName.getText().toString());
//                            newPostRef.child(nickName.getText().toString()).setValue(firebaseAuth.getInstance().getUid());
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                        } else {
                            //에러발생시
                            //textviewMessage.setText("에러유형\n - 이미 등록된 이메일  \n -암호 최소 6자리 이상 \n - 서버에러");
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "등록 에러!", Toast.LENGTH_SHORT).show();
                        }
                        //Toast.makeText(SignUpActivity.this, "2!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });




    }

    private boolean checkNickname(){
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    DatabaseReference ref2 = snapshot.getRef();
                    System.out.println(ref2.getKey());
                    System.out.println(nickName.getText().toString());
                    System.out.println((nickName.getText().toString()).equals(snapshot.getKey()));
                    if((nickName.getText().toString()).equals(snapshot.getKey())){
                        Toast.makeText(SignUpActivity.this, "중복!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(SignUpActivity.this, "사용가능!", Toast.LENGTH_SHORT).show();
                        flag=true;
//                        DatabaseReference postsRef = database.getReference().child("User_nickname");
//                        postsRef.child(nickName.getText().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    databaseError.getMessage();
            }
        });
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view == signUpBtn) {
            //TODO
            registerUser();
        }
        if(view==check_nickName_btn){
            flag = checkNickname();
        }

//        if(view == textviewSingin) {
//            //TODO
//            startActivity(new Intent(this, LoginActivity.class)); //추가해 줄 로그인 액티비티
//        }
    }

    public class Nickname{
        public String nickname;
        public String Uid;

        Nickname(String nickname, String uid){
            this.nickname=nickname;
            this.Uid=uid;
        }
    }

}






