package com.example.untactrip3;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.untactrip3.api.SafeCasterApi;
import com.example.untactrip3.dto.Datum_dong;
import com.example.untactrip3.dto.FlowDensity_dong;
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
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FlowDensityDong extends AppCompatActivity {
    String key="l7xx5165a84073b34123b052bcec46843934";
    private RecyclerView recyclerView;
    private RecyclerView recyclerView2;
    private RecyclerView.Adapter mAdapter;
    private RetrofitClient retrofitClient;
    private SafeCasterApi safeCasterApi;
    private String ldongCd;
    private String ldongName;
    private String guName;
    private TextView date_info;
    private TextView text_state;
    private ImageView iv_state;
    public static double sum2;
    public static double daysum;
    public static double avg;
    public static int state;
    public static List<Datum_dong> items;
    public static List<Datum_dong> emp_items;
    public static List<Double> weekavg=new ArrayList<>();


    ArrayList<Map<String, Object>> SeoulAreaList=null;
    ArrayList<String> code = null;
    int avgcount=0;
    int graphcount=0;
    int weekcursor=0;


    public String FindCdByDong(String Dong){
        String result=null;
        int i=0;

        for(i=0;i<SeoulAreaList.size();i++) {
            if (SeoulAreaList.get(i).get("dong")==null)
                continue;;
            if (SeoulAreaList.get(i).get("dong").toString().equals(Dong))
                    result=SeoulAreaList.get(i).get("lDongCd").toString();
        }

        if(i>SeoulAreaList.size())
            result="없는 동입니다";

        return result;
    }

    public String daybefore(int day){ //day만큼 전날
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String time = format.format (System.currentTimeMillis() - (((long) 1000 * 60 * 60 * 24) * day));
        return time;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_density_dong);

        retrofitClient=RetrofitClient.getInstance();
        safeCasterApi=RetrofitClient.getSafeCasterApi();

        Gson gson = new Gson();

        try{

            InputStream is = getAssets().open("SeoulArea.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            JSONObject jsonObject = new JSONObject(json);


            Map<String,Object> SeoulAreaData= gson.fromJson( jsonObject.toString(),new TypeToken<Map<String, Object>>(){}.getType());

            SeoulAreaList = (ArrayList) SeoulAreaData.get("data");


        }catch (Exception e){e.printStackTrace();}

        date_info = findViewById(R.id.date_info);
        iv_state = findViewById(R.id.iv_state);


        SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd HH시 기준");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat format3 = new SimpleDateFormat("hh");
        SimpleDateFormat format4 = new SimpleDateFormat("HH");
        String format_time1 = format1.format (System.currentTimeMillis());
        String format_time2 = format2.format (System.currentTimeMillis());
        String format_time3 = format3.format (System.currentTimeMillis());
        String format_time4 = format4.format (System.currentTimeMillis());


        int h = Integer.parseInt(format_time3);
        int flowTime=Integer.parseInt(format_time4);



        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        Intent intent = getIntent();
        ldongName = intent.getExtras().getString("dong");
        guName = intent.getExtras().getString("gu");

        //평균값 avgadapter에 보내기
        recyclerView2 = findViewById(R.id.text_state);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView2.setLayoutManager(layoutManager2);



//        code = intent.getStringArrayListExtra("code");


        ldongCd = FindCdByDong(ldongName);



        sum2 = 0.0;
        date_info.setText(format_time1 + " " + guName + " " + ldongName + " 유동인구" );
        safeCasterApi.getFlowDensity_dong(key,
                format_time2, ldongCd).enqueue(new Callback<FlowDensity_dong>() {
            @Override
            public void onResponse(Call<FlowDensity_dong> call, Response<FlowDensity_dong> response) {
                FlowDensity_dong flowDensity_dong = response.body();
                Log.d("retrofit", "Data fetch success");
                items = flowDensity_dong.getData();

                //현재시간만을 recycler에서 보여주기위해
                List<Datum_dong> currentTimeItem= new ArrayList<Datum_dong>();
                currentTimeItem.add(items.get(flowTime));

                mAdapter = new RecyclerAdapter_Flow(currentTimeItem, FlowDensityDong.this, ldongName);
                recyclerView.setAdapter(mAdapter);

                SharedPreferences sf=getSharedPreferences("graphEntry", MODE_PRIVATE);
                SharedPreferences.Editor editor= sf.edit(); //sharedPreferences를 제어할 editor를 선언
                editor.clear();
                for(int i=0;i<items.size();i++) {
                    editor.putString(items.get(i).getHh().toString(),Double.toString(items.get(i).getFlowDensityPercentile()));
                    String density = sf.getString(items.get(i).getHh().toString(),"없음");
                    editor.commit();
                    ++graphcount;

                    if(graphcount==items.size()) {
                        drawLineChart();
                        graphcount=0;
                    }

                }// key,value 형식으로 저장


                //7일간 현 시간 평균
                for(int i = 1; i <= 7 ; i ++){
                    safeCasterApi.getFlowDensity_dong(key, daybefore(i), ldongCd).enqueue(new Callback<FlowDensity_dong>() {
                        @Override
                        public void onResponse(Call<FlowDensity_dong> call, Response<FlowDensity_dong> response) {
                            FlowDensity_dong emp_dong = response.body();
                            Log.d("retrofit", "Data fetch success");
                            emp_items = emp_dong.getData();
                            sum2 += emp_items.get(flowTime).getFlowDensityPercentile();
                            ++avgcount;

                            if(avgcount==7) {
                                avg = sum2/7;
                                System.out.println(avg+"현 시각 평균평균평균");

                                //평균값 recycleradapter_avg에 보내기
                                mAdapter = new RecyclerAdapter_avg(avg, FlowDensityDong.this, flowTime, items);
                                recyclerView2.setAdapter(mAdapter);
                            }
                            if (avg > items.get(flowTime).getFlowDensityPercentile()) {
                                state = 1;

                            } else {
                                state = 2;
                            }
                            if(state == 1){
                                iv_state.setImageResource(R.drawable.good);
                            }
                            else if(state == 2){
                                iv_state.setImageResource(R.drawable.bad);

                            }

                        }
                        @Override
                        public void onFailure(Call<FlowDensity_dong> call, Throwable t) {
                            Log.d("TEST", "절대유동인구 조회실패 ");
                        }
                    });

                    }

                //요일 평균 요일평균 요일 평균
                for(int i = 0; i < 7 ; i ++){
                    safeCasterApi.getFlowDensity_dong(key, daybefore(i), ldongCd).enqueue(new Callback<FlowDensity_dong>() {
                        @Override
                        public void onResponse(Call<FlowDensity_dong> call, Response<FlowDensity_dong> response) {
                            FlowDensity_dong daydata = response.body();
                            Log.d("retrofit", "Data fetch success");
                            items = daydata.getData();
                            //하루 밀집도 시간별로 합산
                            for(int j=0;j<items.size();j++)
                                daysum+= items.get(j).getFlowDensityPercentile();
                            System.out.println(daysum);
                            Double avg=new Double(daysum/24);
                            daysum=0;
                            System.out.println(avg+"요일별 평균 요일별요일볍ㄹ");
                            weekavg.add(avg);
                            ++weekcursor;
                            //barchart 그리기
                            if(weekcursor==7){
                                drawBarChart();
                                weekcursor=0;
                            }

                        }
                        @Override
                        public void onFailure(Call<FlowDensity_dong> call, Throwable t) {
                            Log.d("TEST", "절대유동인구 조회실패 ");
                        }
                    });

                }


            }
            @Override
            public void onFailure(Call<FlowDensity_dong> call, Throwable t) {
                Log.d("TEST", "절대유동인구 조회실패 ");
            }
        });

    }

    //barchart 함수
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
            barEntries.add(new BarEntry((float) i,weekavg.get(6-i).floatValue()));

        barDataSet = new BarDataSet(barEntries, "유동인구 평균");

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
        weekavg.clear();



    }



    private void drawLineChart() {

        LineChart lineChart = (LineChart) findViewById(R.id.chart);

        ArrayList<Entry> lineEntries = new ArrayList<>();

        SharedPreferences sharedPreferences= getSharedPreferences("graphEntry", MODE_PRIVATE);    // test 이름의 기본모드 설정, 만약 test key값이 있다면 해당 값을 불러옴.
        List<String> hhList=  Arrays.asList("00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23");

        //entry할당
        for(int i=0;i<24;i++) {
            String density = sharedPreferences.getString(hhList.get(i),"없음");
            System.out.println(density+"drawFunc");
            lineEntries.add(new Entry(Float.parseFloat(hhList.get(i)), Float.parseFloat(density)));
        }

//linedataset만들기
        LineDataSet lineDataSet = new LineDataSet(lineEntries,"시간별 유동인구");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setHighlightEnabled(true);
        lineDataSet.setLineWidth(2);
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setCircleColor(Color.WHITE);
        lineDataSet.setCircleHoleColor(Color.BLUE);
        //lineDataSet.setCircleRadius(6);
        //lineDataSet.setCircleHoleRadius(3);
        lineDataSet.setDrawHighlightIndicators(true);
        lineDataSet.setValueTextSize(9);
        //lineDataSet.setValueTextColor(Color.DKGRAY);

        LineData data = new LineData(lineDataSet);

        /*dataset.setDrawCubic(true); //선 둥글게 만들기
        dataset.setDrawFilled(true); //그래프 밑부분 색칠*/


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



    }
}