package com.search.fang_search_layout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.search.searchview.BCallBack;
import com.search.searchview.SearchView;

public class MainActivity extends AppCompatActivity {

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView=findViewById(R.id.searchView);
        searchView.setOnClickBack(new BCallBack() {
            @Override
            public void backAction() {
                finish();
            }
        });
    }
}
