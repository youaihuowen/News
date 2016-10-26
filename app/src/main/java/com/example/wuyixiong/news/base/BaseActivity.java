package com.example.wuyixiong.news.base;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wuyixiong.news.R;

public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private Dialog dialog;
    /**
     * 绑定控件
     */
    protected abstract void initView();

    /**
     * 设置监听
     */
    protected abstract void setLisetner();

    public void showLoadingDialog(String msg,boolean cancelable) {
        View v = getLayoutInflater().inflate(R.layout.loading, null);
        RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.rl_loading);
        ImageView im = (ImageView) v.findViewById(R.id.iv_loading);
        TextView tv = (TextView) v.findViewById(R.id.tv_loading);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.loading);
        im.setAnimation(animation);
        if(msg!=null){
            tv.setText(msg);
        }
        dialog = new Dialog(this,R.style.loading_dialog);
        dialog.setCancelable(cancelable);
        dialog.setContentView(layout);
        dialog.show();
    }

    public void cancelDialog(){
        if(dialog != null){
            dialog.dismiss();
        }
    }
}
