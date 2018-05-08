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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

//Class of the adapter to be used in our MainActivity
//Makes use of ViewHolder to make things load faster
public class ArticleAdapter extends ArrayAdapter<Article> {

    //global variable
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

        //each element (Item) of our list has data stored in different positions
        //this makes sure that we're accessing the right one
        Article article = getItem(position);

        //fetches the title info from the article Object stored
        String title = article.getTitle();

        //Because some authors like to add their names to the title, we have to take care of these exceptions
        //They seem rare (until now I only notice it from one single person - Ante Jukic) but cleaner headers at 100% are always better
        //There's a dedicated section for them after all
        if (title.contains("|")) {
            String[] fixedTitle = title.split("\\|");
            title = fixedTitle[0];
        }

        //fetches the summary info from the article Object stored
        String summary = article.getSummary();
        //because the summary sometimes comes with exclusive html tags such as <i></i>
        //we need to format it to proper usable data by our app and system
        CharSequence convertedSummary = Html.fromHtml(summary);
        //Since screen space is limited with an image, sometimes a summary can be more than what
        //we've free to use so we limit it a bit more and add a sign that there is more after that
        if (convertedSummary.length() > 100) {
            String currentSummary = convertedSummary.subSequence(0, 100).toString();

            convertedSummary = currentSummary.subSequence(0, currentSummary.lastIndexOf(" ") + 1);
            convertedSummary = convertedSummary + "...";
        }
        //fetches the author info from the article Object stored
        String author = getContext().getString(R.string.byAuthorPrefix) + " " + article.getAuthor();
        //in case there is nothing there, we get a generic text
        if (author.length() == 0) {
            author = "<i>The Guardian</i>";
        }
        //fetches the date info from the article Object stored
        String date = article.getDate();
        //and formats it as we want, even taking consideration of the time zone of the user
        String currentTime = formatDate(date);

        //variables needed to display our images
        final String imageUrl = article.getImage();
        final ViewHolder finalHolder = holder;

        //To prevent networkOnMainThreadExceptions, we need to call our images in another thread
        //For that, since we can't use AsyncTask in this project, just loaders, we make use of a Runnable
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Bitmap articleImage = BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent());
                    finalHolder.thumbnail.post(new Runnable() {
                        @Override
                        public void run() {
                            if (articleImage != null) {
                                finalHolder.thumbnail.setImageBitmap(articleImage);
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Houston, we got an exception " + e);
                }
            }
        }).start();

        //sets the previous info fetched (note: we already took care of the image)
        holder.title.setText(title);
        holder.summary.setText(convertedSummary);
        holder.author.setText(author);
        holder.date.setText(currentTime);

        //returns the convertView data to the caller
        return convertView;
    }


    //Formats the date of the article to the appropriate time of the user
    private String formatDate(String date) {
        SimpleDateFormat responseDate = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'", Locale.getDefault());
        responseDate.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date newDate = responseDate.parse(date);
            long fixedArticleDate = newDate.getTime();

            Date newArticleDate = new Date(fixedArticleDate);

            SimpleDateFormat fixedArticleDateFormatter = new SimpleDateFormat("dd-LL-YYYY, kk:mm:ss", Locale.getDefault());
            String articleDate = fixedArticleDateFormatter.format(newArticleDate);

            return articleDate;
        } catch (ParseException e) {
            Log.e(TAG, "Parse exception" + e);
            return null;
        }
    }
}
