package com.example.wuyixiong.news.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wuyixiong.news.adapter.HLVAdapter;
import com.example.wuyixiong.news.base.BaseActivity;
import com.example.wuyixiong.news.fragment.CommentFragment;
import com.example.wuyixiong.news.fragment.FavoriteFragment;
import com.example.wuyixiong.news.fragment.LocalFragment;
import com.example.wuyixiong.news.fragment.NewsFragment;
import com.example.wuyixiong.news.fragment.PhotoFragment;
import com.example.wuyixiong.news.sliding.SlidingMenu;
import com.example.wuyixiong.news.view.HorizontalListView;
import com.example.wuyixiong.news.R;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_home;//标题的左边的图片
    private ImageView iv_menu;//标题右边的图片

    private SlidingMenu slidingMenu;//滑动菜单

    private ImageView iv_login;//右滑后登录的图片
    private TextView tv_login;//右滑后登录图片下的文字
    private LinearLayout ll_other;//右滑下边其他方式登录
    private LinearLayout ll_menu;
    private TextView tv_setting;
    private ImageView iv_setting;
    private TextView tv_night;
    private ImageView iv_night;

    private FrameLayout fl_container;//放置fragment的容器

    NewsFragment news = new NewsFragment();//显示新闻的fragment
    private Fragment nowFragment;

    private LinearLayout ll_news;
    private LinearLayout ll_favorte;
    private LinearLayout ll_local;
    private LinearLayout ll_tei;
    private LinearLayout ll_photo;

    private FragmentManager fm;

    private boolean isLogined = false;
    private String userName = null;
    private String token = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSlidingMenu();
        initView();//初始化控件
        checkLogin();
        setLisetner();//设置监听
    }

    @Override
    protected void initView() {
        fl_container = (FrameLayout) findViewById(R.id.fl_main_container);

        iv_login = (ImageView) slidingMenu.findViewById(R.id.iv_login);
        tv_login = (TextView) slidingMenu.findViewById(R.id.tv_login);
        ll_other = (LinearLayout) slidingMenu.findViewById(R.id.ll_slide_left_other);
        iv_setting = (ImageView) slidingMenu.findViewById(R.id.iv_slide_setting);
        iv_night = (ImageView) slidingMenu.findViewById(R.id.iv_slide_night);
        ll_menu = (LinearLayout) slidingMenu.findViewById(R.id.ll_slide_left_menu);
        tv_setting = (TextView) slidingMenu.findViewById(R.id.tv_silde_setting);
        tv_night = (TextView) slidingMenu.findViewById(R.id.tv_slide_night);

        iv_home = (ImageView) findViewById(R.id.iv_home);
        iv_menu = (ImageView) findViewById(R.id.iv_menu);

        ll_news = (LinearLayout) slidingMenu.findViewById(R.id.ll_slide_right_news);
        ll_favorte = (LinearLayout) slidingMenu.findViewById(R.id.ll_slide_right_favorte);
        ll_local = (LinearLayout) slidingMenu.findViewById(R.id.ll_slide_right_local);
        ll_tei = (LinearLayout) slidingMenu.findViewById(R.id.ll_slide_right_tei);
        ll_photo = (LinearLayout) slidingMenu.findViewById(R.id.ll_slide_right_photo);
        fm = getSupportFragmentManager();
        addNewsFrag(news);
    }

    @Override
    protected void setLisetner() {
        iv_login.setOnClickListener(this);
        tv_login.setOnClickListener(this);
        iv_home.setOnClickListener(this);
        iv_menu.setOnClickListener(this);

        ll_news.setOnClickListener(this);
        ll_favorte.setOnClickListener(this);
        ll_local.setOnClickListener(this);
        ll_tei.setOnClickListener(this);
        ll_photo.setOnClickListener(this);
    }


    /**
     * 设置左滑右滑
     */
    private void initSlidingMenu() {
        //获取滑动实例
        slidingMenu = new SlidingMenu(this);
        //设置是否可以滑动
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //设置滑动方向
        slidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
        //设置滑动时拖拽效果
        slidingMenu.setBehindScrollScale(0);
        //滑出单位
        slidingMenu.setBehindOffsetRes(R.dimen.sliding_menu);
        //绑定显示的activity
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        //左滑布局
        slidingMenu.setMenu(R.layout.slide_left);
        //右滑布局
        slidingMenu.setSecondaryMenu(R.layout.slide_right);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login:
            case R.id.iv_login:
                if (!isNetworkAvailable(this)){
                    Toast.makeText(this,"没有网络",Toast.LENGTH_SHORT).show();
                }else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
                slidingMenu.showContent();
                break;
            case R.id.iv_home:
                if (slidingMenu != null) {
                    if (slidingMenu.isMenuShowing()) {
                        slidingMenu.showContent();
                    } else {
                        slidingMenu.showMenu();
                    }
                }
                break;
            case R.id.iv_menu:
                if (slidingMenu != null) {
                    if (slidingMenu.isSecondaryMenuShowing()) {
                        slidingMenu.showContent();
                    } else {
                        slidingMenu.showSecondaryMenu();
                    }
                }
                break;
            case R.id.ll_slide_right_news:
                ll_news.setBackgroundResource(R.color.colorSlideRightSelected);
                ll_favorte.setBackgroundResource(R.color.colorSlideRightNoSelect);
                ll_local.setBackgroundResource(R.color.colorSlideRightNoSelect);
                ll_tei.setBackgroundResource(R.color.colorSlideRightNoSelect);
                ll_photo.setBackgroundResource(R.color.colorSlideRightNoSelect);
                replaceFrag(news);
                slidingMenu.showContent();
                break;
            case R.id.ll_slide_right_favorte:
                ll_news.setBackgroundResource(R.color.colorSlideRightNoSelect);
                ll_favorte.setBackgroundResource(R.color.colorSlideRightSelected);
                ll_local.setBackgroundResource(R.color.colorSlideRightNoSelect);
                ll_tei.setBackgroundResource(R.color.colorSlideRightNoSelect);
                ll_photo.setBackgroundResource(R.color.colorSlideRightNoSelect);
                replaceFrag(new FavoriteFragment());
                slidingMenu.showContent();
                break;
            case R.id.ll_slide_right_local:
                ll_news.setBackgroundResource(R.color.colorSlideRightNoSelect);
                ll_favorte.setBackgroundResource(R.color.colorSlideRightNoSelect);
                ll_local.setBackgroundResource(R.color.colorSlideRightSelected);
                ll_tei.setBackgroundResource(R.color.colorSlideRightNoSelect);
                ll_photo.setBackgroundResource(R.color.colorSlideRightNoSelect);
                replaceFrag(new LocalFragment());
                slidingMenu.showContent();
                break;
            case R.id.ll_slide_right_tei:
                ll_news.setBackgroundResource(R.color.colorSlideRightNoSelect);
                ll_favorte.setBackgroundResource(R.color.colorSlideRightNoSelect);
                ll_local.setBackgroundResource(R.color.colorSlideRightNoSelect);
                ll_tei.setBackgroundResource(R.color.colorSlideRightSelected);
                ll_photo.setBackgroundResource(R.color.colorSlideRightNoSelect);
                replaceFrag(new CommentFragment());
                slidingMenu.showContent();
                break;
            case R.id.ll_slide_right_photo:
                ll_news.setBackgroundResource(R.color.colorSlideRightNoSelect);
                ll_favorte.setBackgroundResource(R.color.colorSlideRightNoSelect);
                ll_local.setBackgroundResource(R.color.colorSlideRightNoSelect);
                ll_tei.setBackgroundResource(R.color.colorSlideRightNoSelect);
                ll_photo.setBackgroundResource(R.color.colorSlideRightSelected);
                replaceFrag(new PhotoFragment());
                slidingMenu.showContent();
                break;

        }
    }

    /**
     * 第一次进入时加载新闻的fragment
     *
     * @param fragment 新闻的fragment
     */
    private void addNewsFrag(Fragment fragment) {
        nowFragment = fragment;
        fm.beginTransaction().add(R.id.fl_main_container, fragment).commit();
    }

    /**
     * 替换fragment
     *
     * @param fragment 要显示的fragment
     */
    private void replaceFrag(Fragment fragment) {
        nowFragment = fragment;
        fm.beginTransaction().replace(R.id.fl_main_container, fragment).commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (slidingMenu.isMenuShowing() || slidingMenu.isSecondaryMenuShowing()) {
                slidingMenu.showContent();
                return true;
            }
            if (!(nowFragment instanceof NewsFragment)) {
                onClick(ll_news);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 检查是否已经登录
     */
    private void checkLogin() {
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        isLogined = sp.getBoolean("isLogin", false);
        Log.i("tag", isLogined + "");
        userName = sp.getString("account", null);
        Log.i("tag", "username " + userName);
        token = sp.getString("token", null);
        Log.i("tag", "token " + token);

        if (isLogined) {
            tv_login.setText(userName);
            iv_home.setImageResource(R.drawable.login);
            iv_login.setImageResource(R.drawable.login);
            ll_other.setVisibility(View.GONE);
            ll_menu.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 判断是否有网
     * @param context 上下文
     * @return 有网返回true 没网返回false
     */
    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()){
            return false;
        }else{
            return true;
        }
    }
}
