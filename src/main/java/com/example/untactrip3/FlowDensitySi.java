package com.example.untactrip3;

import android.content.Intent;
import android.graphics.Color;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FlowDensitySi extends AppCompatActivity {
    String key = "l7xx5165a84073b34123b052bcec46843934";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RetrofitClient retrofitClient;
    private SafeCasterApi safeCasterApi;
    private RecyclerAdapter_dong2 adapter;
    ArrayList<Data> itemList;
    private TextView textView2;
    ArrayList<Map<String, Object>> SeoulAreaList = null;
    public String si;
    public static List<Datum_dong> items;
    public static List<Datum_code> itemss;
    public ArrayList<String> lDongCdList;
    public static double sum;
    public static double avg;
    public String ldongCd;
    public ArrayList<String> code = null;
    public int count;
    public static double[] hourlyAvg= new double[24];// 시간별로 동들의 밀집도 저장
    public static double[] dayAvg =new double[7]; //동들의 24시간 평균 7일치 저장
    int dongcnt=0; // 모든 시간 평균 구할 시 동 순회 마지막 루프 확인
    int dayavgcnt=0;// 모든 시간 평균 구할 시 7일 순회 루프 확인


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_density_si);
        retrofitClient = RetrofitClient.getInstance();
        safeCasterApi = RetrofitClient.getSafeCasterApi();

        SimpleDateFormat format3 = new SimpleDateFormat("HH");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
        String format_time3 = format3.format(System.currentTimeMillis());
        String format_time2 = format2.format(System.currentTimeMillis());

        int h = Integer.parseInt(format_time3);

        itemList = new ArrayList<>();
        Intent intent = getIntent();
        si = intent.getExtras().getString("si");

        count = 0;
        lDongCdList = intent.getStringArrayListExtra("lDongCdList");
        for (int i = 0; i < lDongCdList.size(); i++){
            System.out.println(lDongCdList.get(i));
        }



            for (int i = 0; i < lDongCdList.size(); i++) {
            safeCasterApi.getFlowDensity_dong(key,
                    format_time2, lDongCdList.get(i)).enqueue(new Callback<FlowDensity_dong>() {
                @Override
                public void onResponse(Call<FlowDensity_dong> call, Response<FlowDensity_dong> response) {
                    FlowDensity_dong siMenu = response.body();
                    Log.d("retrofit", "Data fetch success");
                    items = siMenu.getData();


                    sum += items.get(h).getFlowDensityPercentile();
                    //그래프 위해 시간별 밀집도 저장
                    for (int j = 0; j < 24; j++) {
                        hourlyAvg[j] += items.get(j).getFlowDensityPercentile();
                    }
                    count++;

                    if(count == lDongCdList.size()){
                        avg = sum/count;
                        textView2 = findViewById(R.id.textView2);
                        sum = 0.0;
                        textView2.setText(Double.toString(avg));

                        //시간별 동평균
                        for (int j = 0; j < 24; j++) {
                            hourlyAvg[j] /= lDongCdList.size();
                        }
                        drawLineChart();
                    }
                }
                @Override
                public void onFailure(Call<FlowDensity_dong> call, Throwable t) {
                    Log.d("TEST", "절대유동인구 조회실패 ");
                }
            });
        }

        
        //동들의 7일치 평균
        for (int i = 0; i < lDongCdList.size(); i++) {
            for(int j = 0; j < 7 ; j ++){
                safeCasterApi.getFlowDensity_dong(key, daybefore(j), lDongCdList.get(i)).enqueue(new Callback<FlowDensity_dong>() {
                    @Override
                    public void onResponse(Call<FlowDensity_dong> call, Response<FlowDensity_dong> response) {
                        FlowDensity_dong daydata = response.body();
                        items = daydata.getData();


                        //해당 동의 24시간 밀집도 합산
                        for(int k=0;k<24;k++)
                            dayAvg[dayavgcnt]+= items.get(k).getFlowDensityPercentile();

                        ++dayavgcnt;

                        //한 동 7일치 채우면 다음 동위해 초기화하고 채운 동 수 +1
                        if(dayavgcnt==7) {
                            dayavgcnt = 0;
                            ++dongcnt;
                        }

                        //모든 동 순회 했으면 barchart그리기
                        if(dongcnt==lDongCdList.size()) {

                            for (int k = 0; k < 7; k++) {
                                dayAvg[k] /=lDongCdList.size() * 24;
                            }
                            drawBarChart();
                            dongcnt=0;
                        }
                    }
                    @Override
                    public void onFailure(Call<FlowDensity_dong> call, Throwable t) {
                        Log.d("TEST", "절대유동인구 조회실패 ");
                    }
                });
            }
        }


/*
        RecyclerView recyclerView = findViewById(R.id.si_recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        safeCasterApi.getLocation_code(key,
                "kr", si).enqueue(new Callback<Location_code>() {
            @Override
            public void onResponse(Call<Location_code> call, Response<Location_code> response) {
                Location_code locationCode = response.body();
                Log.d("retrofit", "Data fetch success");
                itemss = locationCode.getData();
                adapter = new RecyclerAdapter_dong2(FlowDensitySi.this, si, itemss);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<Location_code> call, Throwable t) {
                Log.d("TEST", "조회실패 ");
            }
        });
 */

    }

    public String daybefore(int day){ //day만큼 전날
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String time = format.format (System.currentTimeMillis() - (((long) 1000 * 60 * 60 * 24) * day));
        return time;
    }

    private void drawLineChart() {

        LineChart lineChart = (LineChart) findViewById(R.id.chart);

        ArrayList<Entry> lineEntries = new ArrayList<>();

        List<String> hhList=  Arrays.asList("00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23");

        //entry할당
        for(int i=0;i<24;i++) {
            lineEntries.add(new Entry(Float.parseFloat(hhList.get(i)), (float) hourlyAvg[i]));
        }


        LineDataSet lineDataSet = new LineDataSet(lineEntries,"시간별 시 전체 유동인구 평균");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setHighlightEnabled(true);
        lineDataSet.setLineWidth(2);
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setCircleColor(Color.WHITE);
        lineDataSet.setCircleHoleColor(Color.BLUE);
        lineDataSet.setDrawHighlightIndicators(true);
        lineDataSet.setValueTextSize(9);


        LineData data = new LineData(lineDataSet);


        lineChart.getDescription().setText("");
        //lineChart.setDrawMarkers(true);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.enableGridDashedLine(16, 12, 0);

        YAxis yAxis = lineChart.getAxisRight();
        yAxis.setDrawLabels(false);
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(false);
        yAxis.setSpaceTop(25);
        yAxis.setSpaceBottom(25);

        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setDrawLabels(false);
        yAxisLeft.setDrawAxisLine(false);
        yAxisLeft.setDrawGridLines(false);
        yAxis.setSpaceTop(25);
        yAxis.setSpaceBottom(25);
        lineChart.setData(data);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
        hourlyAvg = new double[24];

    }

    private void drawBarChart(){
        BarChart barChart;

        // variable for our bar data.
        BarData barData;

        // variable for our bar data set.
        BarDataSet barDataSet;

        // array list for storing entries.
        barChart = findViewById(R.id.BarChart);
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        //bardata만들기
        for(int i=0;i<7;i++)
            barEntries.add(new BarEntry((float) i,new Double(dayAvg[6-i]).floatValue()));

        barDataSet = new BarDataSet(barEntries, "시 전체 유동인구 평균");

        barData = new BarData(barDataSet);

        barChart.setData(barData);
        barChart.getDescription().setText("");

        //항목아래에 라벨
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("D-6");
        xAxisLabel.add("D-5");
        xAxisLabel.add("D-4");
        xAxisLabel.add("D-3");
        xAxisLabel.add("D-2");
        xAxisLabel.add("D-1");
        xAxisLabel.add("Today");

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setTextSize(12);

        barChart.getAxisRight().setDrawAxisLine(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawAxisLine(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);




        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        barChart.notifyDataSetChanged();
        barChart.invalidate();
        dayAvg=new double[7];

    }

}