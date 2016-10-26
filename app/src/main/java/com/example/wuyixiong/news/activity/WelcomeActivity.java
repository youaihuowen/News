package com.example.wuyixiong.news.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wuyixiong.news.R;
import com.example.wuyixiong.news.adapter.ViewPagerAdapter;
import com.example.wuyixiong.news.base.BaseActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class WelcomeActivity extends BaseActivity {

    private ViewPager vp;
    private ArrayList<View> views = new ArrayList<View>();
    private ViewPagerAdapter vpAdapter;

    private ImageView iv_select;
    private ImageView iv_select2;
    private ImageView iv_select3;
    private ImageView iv_select4;

    private Button btn_enter;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        sp = getSharedPreferences("data",MODE_PRIVATE);
        Boolean b = sp.getBoolean("firstOpen",false);
        if (b){
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
        }else {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("firstOpen",true);
            editor.commit();
            initView();

            //导入数据库
            new Thread(new Runnable() {
                @Override
                public void run() {
                    importDatabase();
                }
            }).start();

            setLisetner();
        }
    }

    @Override
    protected void initView() {
        vp = (ViewPager) findViewById(R.id.vp_welcome);

        views.add(getLayoutInflater().inflate(R.layout.viewpage, null));
        views.add(getLayoutInflater().inflate(R.layout.viewpage2, null));
        views.add(getLayoutInflater().inflate(R.layout.viewpage3, null));
        views.add(getLayoutInflater().inflate(R.layout.viewpage4, null));

        vpAdapter = new ViewPagerAdapter(views);
        vp.setAdapter(vpAdapter);

        iv_select = (ImageView) findViewById(R.id.iv_viewpager);
        iv_select2 = (ImageView) findViewById(R.id.iv_viewpager2);
        iv_select3 = (ImageView) findViewById(R.id.iv_viewpager3);
        iv_select4 = (ImageView) findViewById(R.id.iv_viewpager4);
        btn_enter = (Button) views.get(3).findViewById(R.id.btn_enter);


    }

    @Override
    protected void setLisetner() {
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        iv_select.setImageResource(R.drawable.viewpager_selected);
                        iv_select2.setImageResource(R.drawable.viewpager_notselected);
                        iv_select3.setImageResource(R.drawable.viewpager_notselected);
                        iv_select4.setImageResource(R.drawable.viewpager_notselected);
                        break;
                    case 1:
                        iv_select.setImageResource(R.drawable.viewpager_notselected);
                        iv_select2.setImageResource(R.drawable.viewpager_selected);
                        iv_select3.setImageResource(R.drawable.viewpager_notselected);
                        iv_select4.setImageResource(R.drawable.viewpager_notselected);
                        break;
                    case 2:
                        iv_select.setImageResource(R.drawable.viewpager_notselected);
                        iv_select2.setImageResource(R.drawable.viewpager_notselected);
                        iv_select3.setImageResource(R.drawable.viewpager_selected);
                        iv_select4.setImageResource(R.drawable.viewpager_notselected);
                        break;
                    case 3:
                        iv_select.setImageResource(R.drawable.viewpager_notselected);
                        iv_select2.setImageResource(R.drawable.viewpager_notselected);
                        iv_select3.setImageResource(R.drawable.viewpager_notselected);
                        iv_select4.setImageResource(R.drawable.viewpager_selected);
                        break;
                }


                btn_enter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                        finish();
                    }
                });
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void importDatabase(){
        //实例化database的file
        File file=new File("data/data/com.example.wuyixiong.news/database");

        try {
            //如果database不存在，建立目录
            if(!file.exists()){
                file.mkdir();
            }
            //实例化路径为phone.db的file
            file=new File("data/data/com.example.wuyixiong.news/database/news.db");


            //如果news.db不存在，新建文件
            if (!file.exists()){
                file.createNewFile();
            }else{
                return;
            }

            //建立从raw读取数据的输入流
            InputStream input=getResources().openRawResource(R.raw.news);
            //建立写到数据库中的输出流
            FileOutputStream output=new FileOutputStream(file);

            byte[] buffer=new byte[1024];//缓冲区
            int count=0;
            while((count=input.read(buffer))!=-1){
                output.write(buffer,0,count);
            }
            input.close();
            output.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
