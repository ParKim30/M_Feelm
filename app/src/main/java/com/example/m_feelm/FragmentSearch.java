package com.example.m_feelm;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_feelm.model.Item;
import com.example.m_feelm.model.Movie;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FragmentSearch extends Fragment implements View.OnClickListener{
    ViewGroup viewGroup;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_search, container,
                false);
        setupRecyclerView(root);
        setupSearchView(root);
        return root;

        //return inflater.inflate(R.layout.fragment_search, container, false);
    }

    private RecyclerView mRvMovies;
    private RecyclerView.LayoutManager mLayoutManager;

    private MovieAdapter mMovieAdapter;

    private EditText mEtKeyword;
    private Button mBtnSearch;

    private InputMethodManager mInputMethodManager;

    public FragmentSearch() {
        // Required empty public constructor
    }


    private void setupRecyclerView(View view) {
        mRvMovies = view.findViewById(R.id.rv_movies);
        mRvMovies.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRvMovies.setLayoutManager(mLayoutManager);

        // 어댑터 설정
        ArrayList<Item> movies = new ArrayList<>();
        mMovieAdapter = new MovieAdapter(getContext(), movies);
        mRvMovies.setAdapter(mMovieAdapter);

        // 구분선 추가
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                new LinearLayoutManager(getContext()).getOrientation());
        mRvMovies.addItemDecoration(dividerItemDecoration);
    }

    private void setupSearchView(View view) {
        mEtKeyword = view.findViewById(R.id.searchText);
        mBtnSearch = view.findViewById(R.id.searchBtn);
        mBtnSearch.setOnClickListener(this);
        mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.searchBtn) {
            hideKeyboard();
            startSearch(mEtKeyword.getText().toString());
        }
    }

    public void hideKeyboard() {
        mInputMethodManager.hideSoftInputFromWindow(mRvMovies.getWindowToken(), 0);
    }

    public void showEmptyFieldMessage() {
        Toast.makeText(getContext(), "검색어를 입력해주세요", Toast.LENGTH_SHORT).show();
    }

    public void showNotFoundMessage(String keyword) {
        Toast.makeText(getContext(), "\'" + keyword + "\' 를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
    }

    // 검색어가 입력되었는지 확인 후 영화 가져오기
    public void startSearch(String title) {
        if (title.isEmpty()) {
            showEmptyFieldMessage();
        } else {
            mLayoutManager.scrollToPosition(0);
            getMovies(title);
        }
    }

    // 영화 가져오기
    public void getMovies(final String title) {
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<Movie> call = apiInterface.getMovies(title, 100, 1);
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                if(response.isSuccessful()) {
                    ArrayList<Item> movies = new ArrayList(response.body().getItems());
                    if (movies.size() == 0) {
                        mMovieAdapter.clearItems();
                        showNotFoundMessage(title);
                    } else {
                        mMovieAdapter.clearAndAddItems(movies);
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
}

