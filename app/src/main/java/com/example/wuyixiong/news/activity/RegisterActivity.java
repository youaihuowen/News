package com.example.wuyixiong.news.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wuyixiong.news.R;
import com.example.wuyixiong.news.base.BaseActivity;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_account;
    private EditText et_password;
    private EditText et_check;
    private EditText et_email;

    private Button btn_register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        setLisetner();
    }

    @Override
    protected void initView() {
        et_account = (EditText) findViewById(R.id.et_register_account);
        et_password = (EditText) findViewById(R.id.et_register_password);
        et_check = (EditText) findViewById(R.id.et_register_check);
        et_email = (EditText) findViewById(R.id.et_register_email);

        btn_register = (Button) findViewById(R.id.btn_regester_regester);
    }

    @Override
    protected void setLisetner() {
        btn_register.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_regester_regester:
                register(et_account.getText().toString(),
                        et_email.getText().toString(),
                        et_password.getText().toString(),
                        et_check.getText().toString());
                break;
        }

    }

    private void register(String account, String email, String pwd, String check){
        if (account==null || account.equals("")){
            Toast.makeText(this, "账号不能为空", Toast.LENGTH_SHORT).show();
        }else if(!email.matches("^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$")){
            Toast.makeText(this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
        }else if(!pwd.equals(check)){
            Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
        }else if(pwd.length()<6){
            Toast.makeText(this, "密码不能小于六位", Toast.LENGTH_SHORT).show();
        }else {
            Task task = new Task();
            String urlPath = "http://118.244.212.82:9092/newsClient/user_register?";
            task.execute(urlPath, account, email, pwd);
        }
    }

    /**
     * 注册的异步任务
     */
    private class Task extends AsyncTask<String ,Void, String> {

        @Override
        protected String doInBackground(String... params) {
            StringBuilder message = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                String content = "ver=1&uid=" +params[1]+"&email="+params[2]+"&pwd="+params[3];
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
                if (code == 200){
                    InputStream in= connection.getInputStream();
                    byte[] buffer = new byte[1024];
                    int count = 0;
                    while ((count = in.read(buffer)) != -1){
                        message.append(new String(buffer,0,count));
                    }
                    in.close();
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
                if (result==0){
                    setResult(1);
                    finish();

                }else {
                    Toast.makeText(this, explain, Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
