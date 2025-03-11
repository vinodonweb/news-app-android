package com.example.newsapp;

import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewsSourceVolley {
    private static final String TAG = "NewsSourceVolley";

    private static final String api = "015d4a47724841bbaa58a092b6818509";
    private static final String baseURI = "https://newsapi.org/v2/sources";



    public static void getSourceDate(MainActivity mainActivity){
        RequestQueue queue = Volley.newRequestQueue(mainActivity);

        Uri.Builder buildURL = Uri.parse(baseURI).buildUpon();
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "getSources: " + urlToUse);

        Response.Listener<JSONObject> listener = response -> {
            Log.d(TAG, "getSources: " + response);
            try{
                Log.d(TAG, "getSources: " + response);
                handleResults(mainActivity, response);
            } catch (Exception e) {
                Log.d(TAG, "getSources: " + e);
            }
        };


        Response.ErrorListener error = error1 -> {
            Log.d(TAG, "getSources: ");
            if (error1.networkResponse != null) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(error1.networkResponse.data));
                    Log.d(TAG, "getArticles: " + jsonObject);
                    handleResults(mainActivity, null);
                } catch (JSONException e) {
                    Log.e(TAG, "getArticles: ", e);
                }
            } else {
                Log.e(TAG, "getSources: Network response is null.");
                MainActivity.downloadFailed();
                handleResults(mainActivity, null);
            }
        };

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlToUse, null, listener, error){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.put("User-Agent", "News-App");
                headers.put("X-Api-key", api);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }

    private static void handleResults(MainActivity mainActivity, JSONObject jObj){
        if(jObj == null){
            Log.d(TAG, "handleResults: failure in data download");
            MainActivity.downloadFailed();
            return;
        }

        try {
            JSONArray sources = jObj.getJSONArray("sources");
            ArrayList<Source> sourceList = new ArrayList<>();
            for (int i = 0; i < sources.length(); i++) {
                JSONObject source = sources.getJSONObject(i);
                String id = source.getString("id");
                String name = source.getString("name");
                String category = source.getString("category");
                String language = source.getString("language");
                String country = source.getString("country");
                sourceList.add(new Source(id, name, category, language, country));
            }
            Log.d(TAG, "handleResults: " + sourceList);

                mainActivity.updateMenuWithDynamicItems(sourceList);
            } catch (JSONException e) {
                Log.d(TAG, "handleResults: Error parsing JSON", e);
            }
        }

}