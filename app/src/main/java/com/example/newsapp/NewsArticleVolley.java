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

public class NewsArticleVolley {
    private static final String TAG = "NewsArticleVolley";
    private static final String URL = "https://newsapi.org/v2/top-headlines";
    private static final String api = "015d4a47724841bbaa58a092b6818509";

    public static void getArticleData(MainActivity mainActivity, String sourceID) {
        RequestQueue queue = Volley.newRequestQueue(mainActivity);

        Uri.Builder buildURL = Uri.parse(URL).buildUpon();
        buildURL.appendQueryParameter("sources", sourceID);
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "getArticles: " + urlToUse);

        Response.Listener<JSONObject> listener = response -> {
            Log.d(TAG, "getArticles: " + response);
            try {
                handleResults(mainActivity, response);
            } catch (Exception e) {
                Log.d(TAG, "getArticles: " + e);
            }
        };

        Response.ErrorListener error = error1 -> {
            Log.e(TAG, "getArticles: Error occurred while fetching articles.");
            if (error1.networkResponse != null) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(error1.networkResponse.data));
                    Log.d(TAG, "getArticles: " + jsonObject);
                    handleResults(mainActivity, null);
                } catch (JSONException e) {
                    Log.e(TAG, "getArticles: ", e);
                }
            } else {
                handleResults(mainActivity, null);
            }
        };

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlToUse, null, listener, error) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "News-App");
                headers.put("X-Api-key", api);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }

    private static void handleResults(MainActivity mainActivity, JSONObject response) {

        if (response == null) {
            Log.d(TAG, "handleResults: Failed to get data");
            return;
        }

        try {
            JSONArray articles = response.getJSONArray("articles");
            ArrayList<Article> articleList = new ArrayList<>();
            for (int i = 0; i < articles.length(); i++) {
                JSONObject article = articles.getJSONObject(i);
                String author = article.getString("author");
                String title = article.getString("title");
                String description = article.getString("description");
                String url = article.getString("url");
                String urlToImage = article.getString("urlToImage");
                String publishedAt = article.getString("publishedAt");
                articleList.add(new Article(author, title, description, url, urlToImage, publishedAt));
            }
            Log.d(TAG, "handleResults: " + articleList);
            mainActivity.updateArticleData(articleList);
        } catch (JSONException e) {
            Log.e(TAG, "handleResults: ", e);
        }
    }
}