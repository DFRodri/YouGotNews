package com.example.android.yougotnews;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.yougotnews.Class.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class QueryUtils {

    private static final String TAG = QueryUtils.class.getSimpleName();

    private QueryUtils(){}

    public static List<Article> fetchArticleData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        }catch (IOException e){
            Log.e(TAG, "Can't load loader.", e);
        }

        List<Article> articles = parseJSON(jsonResponse);

        return articles;
    }

    public static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Problems with the URL creation", e);
        }
        return url;
    }

    public static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpsURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(TAG, "Error Code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, "Problem making the HTTPS request", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                result.append(line);
                line = bufferedReader.readLine();
            }
        }
        return result.toString();
    }

    public static List<Article> parseJSON(String articleJSON) {
        if (TextUtils.isEmpty(articleJSON)) {
            return null;
        }

        List<Article> articles = new ArrayList<>();
        try {
            JSONObject response = new JSONObject(articleJSON);
            JSONObject articleObject = response.getJSONObject("response");
            JSONArray articleArray = articleObject.getJSONArray("results");
            for (int i = 0; i < articleArray.length(); i++) {
                JSONObject currentArticle = articleArray.getJSONObject(i);
                JSONObject articleFields = currentArticle.getJSONObject("fields");

                String title = currentArticle.optString("webTitle");
                String summary = articleFields.optString("trail-text");
                String section = currentArticle.optString("sectionName");
                String date = currentArticle.optString("webPublicationDate");
                String author = articleFields.optString("byline");
                String url = currentArticle.optString("webUrl");
                String thumbnail = articleFields.optString("thumbnail");

                if (author.length() == 0) {
                    author = null;
                }

                Article article = new Article(title, summary, section, date, author, url, thumbnail);

                articles.add(article);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the JSON results", e);
        }
        return articles;
    }

}
