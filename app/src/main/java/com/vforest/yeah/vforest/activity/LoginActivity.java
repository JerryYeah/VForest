package com.vforest.yeah.vforest.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.vforest.yeah.vforest.R;
import com.vforest.yeah.vforest.util.CommonUrl;
import com.vforest.yeah.vforest.util.HttpUtil;
import com.vforest.yeah.vforest.util.JsonResponse;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private TextView username;
    private TextView password;
    private TextView sign_up;
    private ProgressDialog loginPrgDlg;
    private Button btn_login;
    private View view1, view2;
    private int eventFlag=1;  //1为登录，2为注册

    private static String name = "yeah";
    private Button btn_skip;

    public static String getName(){
        return name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //初始化控件
        username=(TextView)findViewById(R.id.login_username);
        password=(TextView)findViewById(R.id.login_password);
        btn_login=(Button)findViewById(R.id.btn_login);
        view1 = findViewById(R.id.view111);
        view2 = findViewById(R.id.view112);
        sign_up=(TextView)findViewById(R.id.btn_newuser);

        btn_skip = (Button)findViewById(R.id.skip);

        //为Button注册点击事件
        btn_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String usrnm = username.getText().toString();
                final String pwd = password.getText().toString();
                name = usrnm;
                final Map<String,String> map = new HashMap();
                map.put("username", usrnm);
                map.put("password", pwd);
                if(eventFlag == 1){          //用户登录
                if (!usrnm.equals("") && !pwd.equals("")){
                    showProgressDialog();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpUtil.sendOkHttpRequest(CommonUrl.loginAccount, map, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    //Log.d("!!!", "onFailure: 失败啦！！！！！");
                                    indicate("登录失败");
                                }
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    Log.d("!!!!!!!!!", "onResponse: 成功啦！！！！！");
                                    String responseText = response.body().string();
                                    Log.d("!!!!!!!!!", "回复:"+responseText);
                                    String code = JsonResponse.getRspCode(responseText);
                                    Log.d("!!!!!!!!!", "回复:"+code);
                                    if(code.equals("10004")){     //登录通过
                                        closeProgressDialog();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }else if(code.equals("10005"))
                                       indicate("账号或密码错误，请重试");
                                    else
                                        indicate("用户不存在");
                                }
                            });
                        }
                    }).start();

                }else
                    indicate("用户名和密码不能为空！");

                }else{   //注册
                    if (!usrnm.equals("") && !pwd.equals("")){
                        showProgressDialog();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HttpUtil.sendOkHttpRequest(CommonUrl.registerAccount, map, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        indicate("注册失败");
                                    }
                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        String responseText = response.body().string();
                                        String code = JsonResponse.getRspCode(responseText);
                                        if(code.equals("10003")){     //注册成功
                                           indicate("注册成功，去登录吧");
                                        }else if(code.equals("10001"))
                                            indicate("该用户名已被注册，换一个试试");
                                        else
                                        indicate("注册失败");
                                    }
                                });
                            }
                        }).start();
                    }else
                        indicate("用户名和密码不能为空！");
                }
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(eventFlag == 1){
                    eventFlag = 2;
                    sign_up.setText("老用户登录");
                    btn_login.setText("注册");
                }else{   //点击前处于注册状态
                    eventFlag = 1;
                    sign_up.setText("新用户注册");
                    btn_login.setText("登录");
                }

            }
        });

        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void showProgressDialog(){
        if(loginPrgDlg == null) {
            loginPrgDlg = new ProgressDialog(LoginActivity.this);
            loginPrgDlg.setTitle("VF");
            loginPrgDlg.setMessage("处理中");
            loginPrgDlg.setCancelable(false);
        }
            loginPrgDlg.show();
    }
    private void closeProgressDialog(){
        if(loginPrgDlg != null)
            loginPrgDlg.dismiss();
    }

    private void indicate(final String hint){
        LoginActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                closeProgressDialog();
                Toast.makeText(LoginActivity.this, hint,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
