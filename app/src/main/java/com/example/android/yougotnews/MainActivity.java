package com.example.android.yougotnews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private static final int ARTICLE_LOADER_ID = 1;
    private ArticleAdapter articleAdapter;
    private TextView emptyStateTextView;
    private View progressBar;

    //to make sure that our adapter doesn't display double info
    @Override
    protected void onResume() {
        super.onResume();
        articleAdapter.clear();
    }

    @Override
    protected void onPause() {
        super.onPause();
        articleAdapter.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //to make sure that the app loads with the default values the first time the user runs it
        PreferenceManager.setDefaultValues(this, R.xml.settings_main, false);

        //call and set of empty text view to make load bar and error warnings display on the screen
        ListView listView = findViewById(R.id.articleList);
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
    private void internetState() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;
        //checks if there is a connectivity manager for the internet
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
            //and if it's active and ready to work
            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                LoaderManager loaderManager = getLoaderManager();
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

        //check to default settings
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //check to the default value and the one stored in the key of each section of the settings fragment
        String sectionSelected = sharedPreferences.getString(getString(R.string.sectionKey), getString(R.string.world_value));
        String pageSize = sharedPreferences.getString(getString(R.string.pageSizeKey), getString(R.string.fiveArticles_value));
        boolean thumbnailCheck = sharedPreferences.getBoolean(getString(R.string.thumbnailKey), true);

        //queries or not for thumbnails depending on what was selected
        String fieldsPrefs;
        if (!thumbnailCheck) {
            fieldsPrefs = "trail-text,byline";
        } else {
            fieldsPrefs = "thumbnail,trail-text,byline";
        }

        //creates the query
        String QUERY_URL = "https://content.guardianapis.com/search";
        Uri baseUri = Uri.parse(QUERY_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon()
                .appendQueryParameter("order-by", "newest")
                .appendQueryParameter("use-date", "published")
                .appendQueryParameter("show-fields", fieldsPrefs)
                .appendQueryParameter("page-size", pageSize)
                .appendQueryParameter("section", sectionSelected)
                .appendQueryParameter("api-key", "876f6b80-7481-48c9-9599-9e15ddc5ded3");
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
            //credits button
            case R.id.credits:
                Intent intentCredits = new Intent(this, CreditsActivity.class);
                startActivity(intentCredits);
                break;
            //settings button
            case R.id.settings:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentSettings);
            default:
                break;
        }
        return true;
    }
}
