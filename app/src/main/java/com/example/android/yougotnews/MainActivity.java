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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>> {

    //global variables
    private final String QUERY_URL = "https://content.guardianapis.com/search";
    private static final int ARTICLE_LOADER_ID = 1;
    private ArticleAdapter articleAdapter;
    private ListView listView;
    private TextView emptyStateTextView;
    private View progressBar;
    private LoaderManager loaderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //call and set of empty text view to make load bar and error warnings display on the screen
        listView = findViewById(R.id.articleList);
        emptyStateTextView = findViewById(R.id.emptyView);
        listView.setEmptyView(emptyStateTextView);

        //creation of the adapter to hold the data from the list
        articleAdapter = new ArticleAdapter(this, new ArrayList<Article>());
        listView.setAdapter(articleAdapter);

        //call to method that check if internet is or not available
        internetState();

        //ClickListener to refresh the list view when empty view becomes visible
        emptyStateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                internetState();
            }
        });

        //ClickListener to make our actions on each item open the correspondent url of the article
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

    //method that checks if internet is available or not
    public void internetState() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;
        //checks if there is a connectivity manager for the internet
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
            //and if it's active and ready to work
            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                loaderManager = getLoaderManager();
                loaderManager.initLoader(ARTICLE_LOADER_ID, null, MainActivity.this);
            } else {
                //if not, it displays an error message
                emptyStateTextView.setText(R.string.errorNoInternet);
            }
        }
    }

    //Override to make our loader fetch the wanted info from the guardian api
    @Override
    public Loader<List<Article>> onCreateLoader(int id, Bundle args) {
        //hides the empty state view and shows the progress bar
        emptyStateTextView.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        //creates the query
        Uri baseUri = Uri.parse(QUERY_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("order-by", "newest");
        uriBuilder.appendQueryParameter("use-date", "published");
        uriBuilder.appendQueryParameter("show-fields", "thumbnail,byline,trail-text");
        uriBuilder.appendQueryParameter("page-size", "5");
        uriBuilder.appendQueryParameter("section", "football");
        uriBuilder.appendQueryParameter("api-key", "876f6b80-7481-48c9-9599-9e15ddc5ded3");

        String uri = uriBuilder.toString();
        //return the articleLoader to the caller
        return new ArticleLoader(this, uri);
    }

    //Override to make our loader add all the info fetched from the internet to our adapter
    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        //hides the progress bar since we already finished loading the data from the internet
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        //if there is info in the list of articles, add it to the adapter and display it
        if (articles != null) {
            articleAdapter.addAll(articles);
            //notifies the adapter of the addition of articles
            articleAdapter.notifyDataSetChanged();
        } else {
            //else displays and error message to the user
            emptyStateTextView.setText(R.string.errorNoNews);
        }
    }

    //Override to make our loader clear the info displayed in the adapter when a new call is made
    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        articleAdapter.clear();
    }

    //Override to inflate our icons in the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    //Override to make actions on the icons inflated to work
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //refresh button
            case R.id.refresh:
                articleAdapter.clear();
                internetState();
                break;
            //go to credits button
            case R.id.credits:
                Intent intent = new Intent(this, CreditsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }
}
