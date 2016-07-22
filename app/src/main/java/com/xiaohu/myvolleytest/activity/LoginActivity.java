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
import com.android.volley.toolbox.Volley;
import com.xiaohu.myvolleytest.R;
import com.xiaohu.myvolleytest.Service.HeartBeatService;
import com.xiaohu.myvolleytest.http.GsonUtils;
import com.xiaohu.myvolleytest.http.HttpModel;
import com.xiaohu.myvolleytest.http.HttpProxy;
import com.xiaohu.myvolleytest.http.JsonModel;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/21.
 */
public class LoginActivity extends Activity {
    Button btnLogin, btnYan, btnEsc, btnSui;
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
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnEsc = (Button) findViewById(R.id.btn_esc);
        btnYan = (Button) findViewById(R.id.btn_yanzheng);
        btnSui = (Button) findViewById(R.id.btn_suiji);

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

    public static String getSHA(String val) {
        MessageDigest md5;
        byte[] m = new byte[20];
        //md5.update(val.getBytes("UTF-8"));
        try {
            md5 = MessageDigest.getInstance("SHA-1");
            md5.update(val.getBytes("UTF-8"), 0, val.length());
            m = md5.digest();//加密
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return getString(m);
    }

    private static String getString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length);
        String sTemp;
        for (int i = 0; i < b.length; i++) {
            sTemp = Integer.toHexString(0xFF & b[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        System.out.println("sha1::" + sb.toString().length() + "**" + sb.toString());
        return sb.toString();
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
//map.put("Password","123");
        System.out.println("---" + sharedPreferences.getString("RandomNumber", ""));
        String psw = getSHA(getSHA("feifeidemao"));
        String request = getSHA(psw + sharedPreferences.getString("RandomNumber", ""));
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
