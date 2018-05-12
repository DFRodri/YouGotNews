package com.example.android.yougotnews.Utils;

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

    //global variable
    private static final String TAG = QueryUtils.class.getSimpleName();

    //private empty constructor since the info we want is all inside this method
    private QueryUtils(){}

    //method to fetch the articles we want from the internet
    //it parses the info so it can be displayed as we want
    public static List<Article> fetchArticleData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        //because exceptions (errors) are a thing,
        //we have to be sure that our https request works or we get a warning logged
        try {
            jsonResponse = makeHttpsRequest(url);
        }catch (IOException e){
            Log.e(TAG, "Can't load loader.", e);
        }
        //our parsed articles are added to a list that we can display later
        List<Article> articles = parseJSON(jsonResponse);

        //returns the list of articles to the caller
        return articles;
    }

    //method to create the url to make the internet request and return it to the caller
    private static URL createUrl(String stringUrl) {
        URL url = null;
        //because exceptions (errors) are a thing,
        //we have to be sure that our URL actually is a workable URL or we get a warning logged
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Problems with the URL creation", e);
        }
        //returns the url created to the caller
        return url;
    }

    //method to make the https request to the server and return the info we need
    private static String makeHttpsRequest(URL url) throws IOException {
        //local variable that will be used to store the data obtained from the server
        String jsonResponse = "";

        //if there is no url, we can simply stop here and get nothing
        if (url == null) {
            return jsonResponse;
        }

        //start with empty variables to prevent issues when recycling data
        HttpsURLConnection urlConnection = null;
        InputStream inputStream = null;
        //because exceptions (errors) are a thing,
        //we have to be sure that our call to the server works or we get a warning logged
        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //if we get a yes from our GET method (that we can get info from the server)
            //we get a positive answer (response code 200) or a warning logged
            //that something else happened
            if (urlConnection.getResponseCode() == 200) {
                //then we proceed with getting the data, analyze and convert it to a string
                //and add it to our jsonResponse string to be returned back, at the end, to the caller
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(TAG, "Error Code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, "Problem making the HTTP request", e);
        } finally {
            //because we don't need a permanent connection ongoing, we can dc here after
            //everything goes as we expect (connect and get data or get a warning logged)
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        //returns the string with the info collected from the server to the caller
        return jsonResponse;
    }

    //method to convert the data obtained (read from one input stream with a buffered reader)
    //from the internet into one more accessible to work with
    private static String readFromStream(InputStream inputStream) throws IOException {
        //when our inputStream has something (!= null), we read those bytes and convert them to
        //UTF-8 chars and those chars to a string to make it easier to work with later (parse)
        //We use StringBuilder to collect all this info because we can recycle data with it
        //(it's sort of a string 2.0 version) and this makes our work easier
        StringBuilder result = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            //while we still have info to collect from the input stream, we should keep adding info
            //to our string builder until we reach its end
            while (line != null) {
                result.append(line);
                line = bufferedReader.readLine();
            }
        }
        //return our string builder converted to a string to the caller
        return result.toString();
    }

    //method to convert the string obtained previously into the list of articles we want
    //(parse the JSON data collected from the internet)
    private static List<Article> parseJSON(String articleJSON) {
        //if our JSON is empty, we can just stop here and return nothing to the caller
        if (TextUtils.isEmpty(articleJSON)) {
            return null;
        }
        //but if we got something, we can work on it if it's formatted as we expect it to be
        //or we get a warning logged

        //creation of the list of articles that we want to populate with data
        List<Article> articles = new ArrayList<>();
        //attempt to format the string into article objects
        try {
            //creation of the JSON object
            JSONObject response = new JSONObject(articleJSON);
            //obtain the object response from it
            JSONObject articleObject = response.getJSONObject("response");
            //obtain the array in the object response
            JSONArray articleArray = articleObject.getJSONArray("results");
            //check each position of the array for specific info
            /**
             * @Param fields - object that has the:
             *      @Param trailText - summary of the article
             *      @Param byline - who wrote the article
             *      @Param thumbnail - url of the header image used in the article
             * @Param webTitle - tile of the article
             * @Param sectionName - section of the article from where we want news
             * @Param webPublicationDate - when the article was published
             * @Param webUrl - the url of the article
             **/
            for (int i = 0; i < articleArray.length(); i++) {
                JSONObject currentArticle = articleArray.getJSONObject(i);

                JSONObject articleFields = currentArticle.getJSONObject("fields");

                String summary = articleFields.optString("trailText");
                String author = articleFields.optString("byline");
                String thumbnail = articleFields.optString("thumbnail");

                String title = currentArticle.optString("webTitle");
                String section = currentArticle.optString("sectionName");
                String date = currentArticle.optString("webPublicationDate");
                String url = currentArticle.optString("webUrl");

                //if there is no author, there is nothing to add
                if (author.length() == 0) {
                    author = "The Guardian";
                }

                //creation of the article object with the data formatted
                Article article = new Article(title, summary, section, date, author, url, thumbnail);
                //add the article to our list of articles
                articles.add(article);
            }
        //or get a warning logged with the issue
        } catch (JSONException e) {
            Log.e(TAG, "Problem parsing the JSON results", e);
        }
        //returns the list of articles to the caller
        return articles;
    }

}
