package com.example.touchtrack;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.touchtrack.model.DataObject;
import com.example.touchtrack.model.SwipeData;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkManager {
    private static final String TAG = "NetworkManager";

    private static NetworkManager sInstance;
    private static Context mContext;
    private static RequestQueue requestQueue;
    private static final String serverUrl = "http://shenjianan97.link:5002";
    private static final String sendDataPath = "senddata";
    private static final String trainPath = "train";
    private static final String predictPath = "predict";

    private NetworkManager(Context context) {
        mContext = context;
        requestQueue = Volley.newRequestQueue(mContext);
    }

    public static NetworkManager getInstance(Context c) {
        if (null == sInstance)
            sInstance = new NetworkManager(c);
        return sInstance;
    }

    public interface OnSendDataListener {
        void onSuccess();

        void onNetworkFail();

        void onFail();
    }

    public interface OnTrainListener {
        void onSuccess();

        void onNetworkFail();

        void onFail();
    }

    public interface OnPredictListener {
        void onSuccess(boolean result);

        void onNetworkFail();

        void onFail();
    }

    public void sendData(final String username, final List<SwipeData> sdl, final int width, final int height, final OnSendDataListener l) {
        DataObject data = new DataObject(username, sdl, width, height);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(new Gson().toJson(data));
            Log.d(TAG, jsonObject.toString());
        } catch (JSONException error) {
            Log.e(TAG, error.getMessage(), error);
            l.onFail();
            return;
        }

        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, serverUrl + "/" + sendDataPath, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "send data response" + response.toString());
                        l.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {
                    l.onNetworkFail();
                } else {
                    l.onFail();
                }
                Log.e(TAG, error.getMessage(), error);
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }
        };
        requestQueue.add(jsonRequest);
    }

    public void train(final String username, final OnTrainListener l) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userName", username);
            Log.d(TAG, jsonObject.toString());
        } catch (JSONException error) {
            Log.e(TAG, error.getMessage(), error);
            l.onFail();
            return;
        }
        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, serverUrl + "/" + trainPath, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "train response" + response.toString());
                        l.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {
                    l.onNetworkFail();
                } else {
                    l.onFail();
                }
                Log.e(TAG, error.getMessage(), error);
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }
        };
        requestQueue.add(jsonRequest);
    }

    public void predict(final String username, final List<SwipeData> sdl, final int width, final int height, final OnPredictListener l) {
        DataObject data = new DataObject(username, sdl, width, height);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(new Gson().toJson(data));
            Log.d(TAG, jsonObject.toString());
        } catch (JSONException error) {
            Log.e(TAG, error.getMessage(), error);
            l.onFail();
            return;
        }

        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, serverUrl + "/" + predictPath, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "predict response" + response.toString());
                        boolean result = response.optBoolean("result");
                        l.onSuccess(result);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {
                    l.onNetworkFail();
                } else {
                    l.onFail();
                }
                Log.e(TAG, error.getMessage(), error);
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }
        };
        requestQueue.add(jsonRequest);
    }
}