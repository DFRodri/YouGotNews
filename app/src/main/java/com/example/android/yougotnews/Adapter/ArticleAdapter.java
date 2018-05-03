package com.example.android.yougotnews.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.yougotnews.Class.Article;
import com.example.android.yougotnews.R;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class ArticleAdapter extends ArrayAdapter<Article> {

    private static final String TAG = "Error: ";

    static class ViewHolder {
        TextView title;
        TextView summary;
        ImageView thumbnail;
        TextView author;
        TextView date;
    }

    /**
     * Constructor of our article adapter with the following parameters
     *
     * @param context     - context of the app
     * @param articleList - list of articles
     *
     */
    public ArticleAdapter(Context context, List<Article> articleList) {
        super(context, 0, articleList);
    }

    //The getView method to display (return) each row of what we want as we want with a certain layout as a base
    //Makes use of a ViewHolder class created previously to recycle data
    //R.id.<layout name> -> the layout
    //the views loaded with the holder -> what we want to populate with data
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder = null;
        //Checks if we already have or not a list view (convertView) to reuse
        //If we have none, create a new one
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, parent, false);

            holder = new ViewHolder();
            holder.title = convertView.findViewById(R.id.title);
            holder.summary = convertView.findViewById(R.id.summary);
            holder.thumbnail = convertView.findViewById(R.id.thumbnail);
            holder.author = convertView.findViewById(R.id.author);
            holder.date = convertView.findViewById(R.id.date);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Article article = getItem(position);

        String title = article.getTitle();
        String summary = article.getSummary();
        CharSequence convertedSummary = Html.fromHtml(summary);
        if (convertedSummary.length() > 50) {
            String currentSummary = (String) convertedSummary.subSequence(0, 50);
            convertedSummary = currentSummary.subSequence(0, currentSummary.lastIndexOf(" ") + 1);
            convertedSummary = convertedSummary + "...";
        }
        String author = article.getAuthor();
        if (author.length() == 0){
            author = "<i>The Guardian</i>";
        }

        String date = article.getDate();
        String currentTime = formatDate(date);


        String imageUrl = article.getImage();
        Bitmap fetchedImage = imageDecoded(imageUrl);

        holder.title.setText(title);
        holder.summary.setText(convertedSummary);
        holder.thumbnail.setImageBitmap(fetchedImage);
        holder.author.setText(author);
        holder.date.setText(currentTime);

        return convertView;
    }

    private String formatDate(String date) {
        String[] time = date.split("T");
        String[] timeUnit = time[0].split("-");
        String year = timeUnit[0];
        String month = timeUnit[1];
        String day = timeUnit[2];
        date = day + "-" + month + "-" + year;

        return date;
    }

    //method to fetch our image based on the url string in the List
    private Bitmap imageDecoded(String imageUrl) {
        Bitmap decodedImage = null;
        try {
            InputStream inputStream = new URL(imageUrl).openStream();
            decodedImage = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return decodedImage;
    }
}
