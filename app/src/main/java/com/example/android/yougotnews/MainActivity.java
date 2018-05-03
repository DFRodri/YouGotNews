package com.example.android.yougotnews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.yougotnews.Adapter.ArticleAdapter;
import com.example.android.yougotnews.Class.Article;
import com.example.android.yougotnews.Loader.ArticleLoader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>>{

    //global variables
    private final String QUERY_URL = "https://content.guardianapis.com/search?";
    private static final int ARTICLE_LOADER = 0;
    private ArticleAdapter articleAdapter;
    private ListView listView;
    private TextView emptyStateTextView;
    private View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //call and set of empty text view to make load bar and error warnings display on the screen
        listView = findViewById(R.id.list);
        emptyStateTextView = findViewById(R.id.emptyView);
        listView.setEmptyView(emptyStateTextView);

        //creation of the adapter to hold the data from the list
        articleAdapter = new ArticleAdapter(this, new ArrayList<Article>());

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null & networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(ARTICLE_LOADER, null, this);
        } else {
            emptyStateTextView.setText(R.string.errorNoInternet);
        }

        emptyStateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article currentArticle = articleAdapter.getItem(position);

                Uri articleUri = Uri.parse(currentArticle.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, articleUri);
                //to make sure that the user has something to open the urls sent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.errorAPP), Toast.LENGTH_LONG).show();
                }
                startActivity(intent);
            }
        });

    }

    @Override
    public Loader<List<Article>> onCreateLoader(int id, Bundle args) {
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        Uri baseUri = Uri.parse(QUERY_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("order-by", "newest");
        uriBuilder.appendQueryParameter("use-date", "published");
        uriBuilder.appendQueryParameter("show-fields", "thumbnail%2Cbyline%2Ctrail-text");
        uriBuilder.appendQueryParameter("page-size", "5");
        uriBuilder.appendQueryParameter("section", "football");
        uriBuilder.appendQueryParameter("api-key", "876f6b80-7481-48c9-9599-9e15ddc5ded3");

        String uri = uriBuilder.toString();

        return new ArticleLoader(this, uri);
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        emptyStateTextView.setText(R.string.errorNoNews);
        articleAdapter.clear();
        if (articles != null){
            articleAdapter.clear();
            articleAdapter.addAll(articles);
            articleAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        /**if (articles != null){
            articleAdapter.clear();
            articleAdapter.addAll(articles);
            articleAdapter.notifyDataSetChanged();
        }**/
    }
}
