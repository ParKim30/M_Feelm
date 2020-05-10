package com.example.m_feelm;

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

import java.util.ArrayList;
import java.util.Comparator;


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_statistic_my, container, false);
        myRanking(root);


        return root;
    }
    private void myRanking(View view){
        FirebaseUser user;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        TextView myRanking1=view.findViewById(R.id.myRanking1);
        TextView myRanking2=view.findViewById(R.id.myRanking2);
        TextView myRanking3=view.findViewById(R.id.myRanking3);


        mDatabase = FirebaseDatabase.getInstance();
        mReference=mDatabase.getReference("User_review");

        ArrayList<UserReview> userReviews=new ArrayList<>();

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

