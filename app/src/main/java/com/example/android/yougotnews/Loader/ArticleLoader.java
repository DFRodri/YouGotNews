package com.example.android.yougotnews.Loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.yougotnews.Class.Article;
import com.example.android.yougotnews.Utils.QueryUtils;

import java.util.List;

public class ArticleLoader extends AsyncTaskLoader<List<Article>> {

    //global variable
    private final String queryUrl;

    //constructor of our article Loader class
    public ArticleLoader(Context context, String queryUrl) {
        super(context);
        this.queryUrl = queryUrl;
    }

    //override of the onStartLoading method to make the loader work
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    //override of the loadInBackground method to start fetching the info we want from the server
    @Override
    public List<Article> loadInBackground() {
        //if our query (call to the server) returned nothing, we can stop here since there is nothing
        //else to do
        if (queryUrl == null) {
            return null;
        }
        //initialization of our list of articles and call to the QueryUtils class' method
        //fetchArticleData to populate it
        List<Article> articles = QueryUtils.fetchArticleData(queryUrl);

        //return the list of articles to the caller
        return articles;
    }
}
