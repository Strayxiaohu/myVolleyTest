package com.xiaohu.myvolleytest.http;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/21.
 */
public class MyStringRequest extends StringRequest {
Context myContext;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
Map<String,String> myHeaders=null;

    public MyStringRequest(Context context,int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        myContext=context;
        sharedPreferences = context.getSharedPreferences("APPROVAL", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public MyStringRequest(Context context,String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
        myContext=context;
        sharedPreferences = context.getSharedPreferences("APPROVAL", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    public MyStringRequest(Context context,int method,String url,Map<String,String> map, Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(method,url,listener,errorListener);
        myHeaders=map;
        sharedPreferences = context.getSharedPreferences("APPROVAL", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            Map<String, String> responseHeaders = response.headers;
            String rawCookies = responseHeaders.get("Set-Cookie");//cookie值
            String dataString = new String(response.data, "UTF-8");//返回值
            System.out.println("cookie:" + rawCookies + "---");
            if (rawCookies != null) {
                int num = rawCookies.indexOf(";");
                if (num > 0) {
                    editor.putString("SessionId", rawCookies.substring(0, num));
                    editor.commit();
                }
            }
            //cookie:SessionId=15C9425D170100610D93455BCC; path=/; HttpOnly---{"RandomNumber":"15C9425D170300610D93455BCC",
            // "Success":true,"Message":null,"Time":"\/Date(1469067754393)\/"}
            return Response.success(dataString, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if (headers == null
                || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }
        String sid = sharedPreferences.getString("SessionId", "");
//                if (sid.length() > 0) {
//                    StringBuilder builder = new StringBuilder();
//                   builder.append(sid);
//                    if (headers.containsKey("Cookie")) {
//                        builder.append("; ");
//                        builder.append(headers.get("Cookie"));
//                    }
//                    headers.put("Cookie", builder.toString());
//                }
        System.out.println(";;;;;;;;" + sid);
        if(myHeaders!=null){
            headers=myHeaders;
        }
        headers.put("Charset","UTF-8");
        headers.put("Cookie", sid);

        return headers;
        //eturn super.getHeaders();
    }


//    jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
//                                                         DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                                                         DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
}
