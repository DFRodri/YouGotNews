package com.example.android.yougotnews.Loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.android.yougotnews.Class.Article;
import com.example.android.yougotnews.QueryUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ArticleLoader extends AsyncTaskLoader<List<Article>> {

    private String queryUrl;

    private static final String TAG = "ERROR: ";

    public ArticleLoader(Context context, String queryUrl) {
        super(context);
        this.queryUrl = queryUrl;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Article> loadInBackground() {
        if (queryUrl == null) {
            return null;
        }
        List<Article> articles = QueryUtils.fetchArticleData(queryUrl);

        return articles;
    }
}
