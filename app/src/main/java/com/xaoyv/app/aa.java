package com.xaoyv.app;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Tag:
 *
 * @author Xaoyv
 * date 6/8/2021 6:38 PM
 */
public class aa {
    public void main() {
        String json = "{" + "\"code\" :    \"1.1\"" + "}";
        try {
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.getInt("code");
            Log.d("TAGGGGGGGGGGGG", "main: " + code);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("TAGGGGGGGGGGGG", "main: " + e.getMessage());
        }
    }
}
