package com.xiaohu.myvolleytest.http;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/7/21.
 */
public class GsonUtils {
    public static JsonModel analysisJson(String json, String method) {
        JsonModel model = new JsonModel();
        //json=json.replace("/","");
        try {
            JSONObject jsonObject = new JSONObject(json);
            model.setData(jsonObject.getString("Time"));
            model.setMessage(jsonObject.getString("Message"));
            model.setSuccess(jsonObject.getString("Success"));

            if (method.equals("RandomNumber")) {
                model.setRandomNumber(jsonObject.getString("RandomNumber"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return model;

    }
}
