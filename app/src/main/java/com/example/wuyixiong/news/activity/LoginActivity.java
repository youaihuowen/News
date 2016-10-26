package com.example.wuyixiong.news.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wuyixiong.news.R;
import com.example.wuyixiong.news.base.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_name;
    private EditText et_pwd;

    private Button btn_login;

    private TextView tv_registe;
    private TextView tv_findPwd;

    String username = null;
    String password = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        setLisetner();
    }

    @Override
    protected void initView() {
        et_name = (EditText) findViewById(R.id.et_login_name);
        et_pwd = (EditText) findViewById(R.id.et_login_password);
        btn_login = (Button) findViewById(R.id.btn_login_login);
        tv_findPwd = (TextView) findViewById(R.id.tv_login_findPassword);
        tv_registe = (TextView) findViewById(R.id.tv_login_registe);
    }

    @Override
    protected void setLisetner() {
        btn_login.setOnClickListener(this);
        tv_registe.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login_login:
                login(et_name.getText().toString(), et_pwd.getText().toString());
                Log.i("tag", et_name.getText().toString()+et_pwd.getText().toString());
                break;
            case R.id.tv_login_registe:
                Intent mIntent = new Intent(this,RegisterActivity.class);
                startActivityForResult(mIntent, 1);
        }

    }

    /**
     * 登录的方法
     * @param user 用户名
     * @param pwd 密码
     */
    private void login(String user, String pwd){
        if (user == null || user.length()<1){
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            et_name.setText("");
            et_pwd.setText("");
        }else if (pwd ==null || pwd.length()<1){
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            et_name.setText("");
            et_pwd.setText("");
        }else {
            String urlPath = "http://118.244.212.82:9092/newsClient/";
            Task task = new Task();
            task.execute(urlPath,user, pwd);
        }
    }

    /**
     * 异步任务从网路上获取登录的相关数据
     */
    private class Task extends AsyncTask<String ,Void, String>{

        @Override
        protected String doInBackground(String... params) {
            StringBuilder message = new StringBuilder();
            try {
                //建立连接
                URL url = new URL(params[0]+"user_login?");
                String content = "ver=1&uid="+params[1]+"&pwd="+params[2]+"&device=0";
                //获取HttpURlConnection实例
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //设置请求方式
                connection.setRequestMethod("POST");
                //设置请求头格式
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", content.length()+"");
                //允许HttpURlConnection对象使用输出流
                connection.setDoOutput(true);
                //获取输出流，输出
                OutputStream output = connection.getOutputStream();
                output.write(content.getBytes());
                //关闭输出流
                output.close();
                //获取响应码
                int code = connection.getResponseCode();
                if (code==200){
                    InputStream in= connection.getInputStream();
                    byte[] buffer = new byte[1024];
                    int count = 0;
                    while ((count = in.read(buffer)) != -1){
                        message.append(new String(buffer,0,count));
                    }
                    in.close();
                }else {
                    Toast.makeText(LoginActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return message.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("tag", s);
            analysisData(s);

        }
    }

    /**
     * 解析从网络上返回的数据
     * @param s 返回的数据
     */
    private void analysisData(String s){
        try {
            JSONObject object = new JSONObject(s);
            int status = object.getInt("status");
            String message = object.getString("message");
            if (status == 0 && message.equals("OK")){
                JSONObject object1 = object.getJSONObject("data");
                int result = object1.getInt("result");
                String explain = object1.getString("explain");
                String token = object1.getString("token");
                if (result==0){

                    //将用户信息保存到SharedPreferences
                    SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("isLogin",true);
                    editor.putString("account",et_name.getText().toString());
                    editor.putString("token",token);
                    editor.commit();

                    //跳转页面
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);


                }else {
                    Toast.makeText(this, explain, Toast.LENGTH_SHORT).show();
                    et_name.setText("");
                    et_pwd.setText("");
                }
            }else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                et_name.setText("");
                et_pwd.setText("");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1){
            Toast.makeText(this, "注册成功请登录", Toast.LENGTH_LONG).show();
        }
    }
}
