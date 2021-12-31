package com.example.untactrip3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int Untact_red = Color.parseColor("#E05D5D");

        TextView tv_main = (TextView) findViewById(R.id.tv_main);

        Spannable span = (Spannable) tv_main.getText();
        span.setSpan(new ForegroundColorSpan(Color.WHITE),8,17, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        span.setSpan(new BackgroundColorSpan(Untact_red),8,17, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        span.setSpan(new RelativeSizeSpan(1.3f), 8, 17, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        ImageButton btn_seoul = (ImageButton)findViewById(R.id.btn_seoul);
        ImageButton btn_korea = (ImageButton)findViewById(R.id.btn_korea);
        Button dev_info = (Button)findViewById(R.id.dev_info);

        btn_seoul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SeoulMenu.class);
                startActivity(intent);
            }
        });
        btn_korea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,KoreaMenu.class);
                startActivity(intent);
            }
        });

        dev_info = (Button)findViewById(R.id.dev_info);

        //리스너설정
        dev_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //버튼 클릭시 다이얼로그 생성을 할것이다. (버튼 클릭시 알림창이라는 객체가 뜨도록할것이다)
                dialog = new Dialog(MainActivity.this, R.style.Dialog);

                dialog.setTitle("타이틀 설정");
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.setContentView(R.layout.activity_dev_info_popup);
                dialog.show();
            }
        });
    }

    public void rankclick(View view){
        Toast.makeText(getApplicationContext(), "준비중입니다.", Toast.LENGTH_LONG).show();
    }
}