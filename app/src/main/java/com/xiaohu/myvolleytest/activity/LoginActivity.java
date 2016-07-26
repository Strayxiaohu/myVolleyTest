package com.xiaohu.myvolleytest.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiaohu.myvolleytest.R;
import com.xiaohu.myvolleytest.Service.HeartBeatService;
import com.xiaohu.myvolleytest.http.GsonUtils;
import com.xiaohu.myvolleytest.http.HttpModel;
import com.xiaohu.myvolleytest.http.HttpProxy;
import com.xiaohu.myvolleytest.http.JsonModel;
import com.xiaohu.myvolleytest.model.CodeModel;
import com.xiaohu.myvolleytest.sha1password.EncryptionPassword;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/21.
 */
public class LoginActivity extends Activity {
    Button btnLogin, btnYan, btnEsc, btnSui, btnWeb, btnGetCode;
    RequestQueue queue;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        queue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences("APPROVAL", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        initView();
        initEvent();
    }

    private void initEvent() {
        btnGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getCode();
                getCodeMethod();
            }
        });
        btnWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, WebViewActivity.class);
                startActivity(intent);
            }
        });
        btnEsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutMethod();
            }
        });
        btnSui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SuiJiMethod(false);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YanZhengMethod(true);
            }
        });
        btnYan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YanZhengMethod(false);
            }
        });
    }

    private void initView() {
        btnWeb = (Button) findViewById(R.id.btn_webservice);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnEsc = (Button) findViewById(R.id.btn_esc);
        btnYan = (Button) findViewById(R.id.btn_yanzheng);
        btnSui = (Button) findViewById(R.id.btn_suiji);
        btnGetCode = (Button) findViewById(R.id.btn_getCode);

    }

    private void getCode() {
        JsonObjectRequest request = new JsonObjectRequest("http://192.168.1.113:62020/Service/Code.aspx?Action=GetCodes&CodeName=Sex&endWith=女", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("result:"+response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
            }
        });
        queue.add(request);
    }

    private void getCodeMethod() {
        String url = "http://192.168.1.113:62020/Service/Code.aspx?Action=GetCodes&CodeName=Sex";
        String url1 = "http://192.168.1.113:62020/Service/Code.aspx?Action=GetCodesStartWith&CodeName=Sex&startWith=1";
        String url2="http://192.168.1.113:62020/Service/Code.aspx?Action=GetCodesEndWith&CodeName=Sex&startWith=1";
        HttpModel model = new HttpModel();
        HttpProxy proxy = new HttpProxy(queue, url, LoginActivity.this, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("getCode::" + response);
                CodeModel codeModel= new Gson().fromJson(response,CodeModel.class);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
                Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LogoutMethod() {
        String url = "http://192.168.1.113:62020/Service/User.aspx?Action=Logout";
        HttpModel model = new HttpModel();
        HttpProxy proxy = new HttpProxy(queue, url, model, LoginActivity.this, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JsonModel model = GsonUtils.analysisJson(response, "");
                if (model.getSuccess().equals("true")) {
                    Toast.makeText(LoginActivity.this, "注销成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, HeartBeatService.class);
                    stopService(intent);
                    editor.putString("SessionId", "");
                    editor.putString("RandomNumber", "");
                    editor.commit();
                } else {
                    Toast.makeText(LoginActivity.this, "注销失败", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "注销失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void YanZhengMethod(final boolean isTrue) {
        String url = "http://192.168.1.113:62020/Service/User.aspx?Action=Online";
        HttpModel model = new HttpModel();
        HttpProxy proxy = new HttpProxy(queue, url, model, LoginActivity.this, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("验证：" + response);
                JsonModel model1 = GsonUtils.analysisJson(response, "");

                if (model1.getSuccess().equals("true")) {
                    //在线不需要重新登录
                    Toast.makeText(LoginActivity.this, "在线", Toast.LENGTH_SHORT).show();
                } else {
                    if (isTrue) {
                        SuiJiMethod(true);
                    }
                    System.out.println("验证失败");
                    Toast.makeText(LoginActivity.this, "不在线", Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
            }
        });
    }

    private void SuiJiMethod(final boolean isTrue) {
        editor.putString("SessionId", "");
        editor.commit();
        String url = "http://192.168.1.113:62020/Service/User.aspx?Action=GetRandomNumber";
        final HttpModel model = new HttpModel();
        HttpProxy proxy = new HttpProxy(queue, url, model, LoginActivity.this, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JsonModel model1 = GsonUtils.analysisJson(response, "RandomNumber");
                editor.putString("RandomNumber", model1.getRandomNumber());
                editor.commit();
                System.out.println("RandomNumber::" + response);
                Toast.makeText(LoginActivity.this, "RandomNumber::" + model1.getRandomNumber(), Toast.LENGTH_SHORT).show();
                //重新登录
                if (isTrue) {
                    loginMethod();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
            }
        });
    }


    private void loginMethod() {
        String url = "http://192.168.1.113:62020/Service/User.aspx?Action=Login";
        Map<String, String> map = new HashMap<String, String>();
        map.put("UserType", "SealUser");
        map.put("UserName", "admin");
        System.out.println("---" + sharedPreferences.getString("RandomNumber", ""));
        String psw = EncryptionPassword.getSHA(EncryptionPassword.getSHA("feifeidemao"));
        String request = EncryptionPassword.getSHA(psw + sharedPreferences.getString("RandomNumber", ""));
        map.put("Password", request);
        HttpProxy postProxy = new HttpProxy(queue, url, map, LoginActivity.this, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                JsonModel model = GsonUtils.analysisJson(response, "");
                if (model.getSuccess().equals("true")) {
                    Intent intent = new Intent(LoginActivity.this, HeartBeatService.class);
                    startService(intent);
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
