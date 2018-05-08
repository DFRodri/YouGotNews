package com.example.android.yougotnews;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.android.yougotnews.Adapter.CreditsAdapter;
import com.example.android.yougotnews.Class.Credits;

import java.util.ArrayList;

public class CreditsActivity extends AppCompatActivity {

    //global variables
    private ListView listView;
    private ArrayList<Credits> creditsArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        //to display our action bar go up icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //call and setup of the arrayList with the credits
        listView = findViewById(R.id.creditsList);

        //call to the method that populates our array list
        setupCredits();

        //setup of our custom adapter
        CreditsAdapter customCredits = new CreditsAdapter(this, creditsArrayList);
        listView.setAdapter(customCredits);
    }

    //method to add the credits objects of the resources used in this project
    private void setupCredits() {
        creditsArrayList.add(new Credits(getString(R.string.creditName0), getString(R.string.creditURL0)));
        creditsArrayList.add(new Credits(getString(R.string.creditName1), getString(R.string.creditURL1)));
        creditsArrayList.add(new Credits(getString(R.string.creditName2), getString(R.string.creditURL2)));
        creditsArrayList.add(new Credits(getString(R.string.creditName3), getString(R.string.creditURL3)));
        creditsArrayList.add(new Credits(getString(R.string.creditName4), getString(R.string.creditURL4)));
        creditsArrayList.add(new Credits(getString(R.string.creditName5), getString(R.string.creditURL5)));
    }

    //Override to make our navigation up button work
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
