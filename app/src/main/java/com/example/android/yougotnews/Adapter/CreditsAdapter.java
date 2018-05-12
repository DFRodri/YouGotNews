package com.example.android.yougotnews.Adapter;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.yougotnews.Class.Credits;
import com.example.android.yougotnews.R;

import java.util.ArrayList;

//Class of the adapter to be used in our CreditsActivity
//Makes use of ViewHolder to make things load faster
public class CreditsAdapter extends ArrayAdapter<Credits> {

    static class ViewHolder {
        TextView creatorName;
        TextView creatorURL;
    }

    /**
     * Constructor of our article adapter with the following parameters
     *
     * @param context     - context of the app
     * @param creditsList - list of the resources used
     */
    public CreditsAdapter(Activity context, ArrayList<Credits> creditsList) {
        super(context, 0, creditsList);
    }

    //The getView method to display (return) each row of what we want as we want with a certain layout as a base
    //Makes use of a ViewHolder class created previously to recycle data even further
    //R.id.<layout name> -> the layout
    //getMethods -> what we want to be displayed in each recycled view
    //the views loaded with the holder -> what we want to populate with data
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        //Checks if we already have or not a list view (convertView) to reuse
        //If we have none, create a new one
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.credits_item, parent, false);

            holder = new ViewHolder();
            holder.creatorName = convertView.findViewById(R.id.name);
            holder.creatorURL = convertView.findViewById(R.id.url);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //each element (Item) of our list has data stored in different positions
        //this makes sure that we're accessing the right one
        Credits currentCredits = getItem(position);

        //fetches the info from the credits object stored
        String newName = currentCredits.getNewCreditName();
        String newURL = currentCredits.getNewCreditURL();

        //sets the info fetched from the get methods so we can have something displayed
        holder.creatorName.setText(newName);
        holder.creatorURL.setText(newURL);

        //returns all the work made related the convertView to the caller
        return convertView;

    }
}