package com.example.untactrip3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import androidx.recyclerview.widget.RecyclerView;

import com.example.untactrip3.api.SafeCasterApi;
import com.example.untactrip3.dto.Datum_code;
import com.example.untactrip3.dto.Datum_dong;
import com.example.untactrip3.dto.FlowDensity_dong;
import com.example.untactrip3.dto.Location_code;
import com.example.untactrip3.service.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SiMenu extends AppCompatActivity {
    String key="l7xx5165a84073b34123b052bcec46843934";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RetrofitClient retrofitClient;
    private SafeCasterApi safeCasterApi;
    private RecyclerAdapter_si adapter;
    ArrayList<Data> itemList;
    ArrayList<Map<String, Object>> SeoulAreaList=null;
    public String si;
    public static List<Datum_code> itemss;
    private TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_si_menu);
        retrofitClient=RetrofitClient.getInstance();
        safeCasterApi=RetrofitClient.getSafeCasterApi();
        textView2 = findViewById(R.id.textView2);
        RecyclerView recyclerView = findViewById(R.id.si_recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        Intent intent = getIntent();
        si = intent.getExtras().getString("si");
        textView2.setText(si);
        safeCasterApi.getLocation_code(key,
                "kr", si).enqueue(new Callback<Location_code>() {
            @Override
            public void onResponse(Call<Location_code> call, Response<Location_code> response) {
                Location_code locationCode = response.body();
                Log.d("retrofit", "Data fetch success");
                itemss = locationCode.getData();
                adapter = new RecyclerAdapter_si(SiMenu.this, si, itemss);
                recyclerView.setAdapter(adapter);
            }


            @Override
            public void onFailure(Call<Location_code> call, Throwable t) {
                Log.d("TEST", "조회실패 ");
            }
        });
    }
}
