package com.example.android.yougotnews.Class;

/**
 * Custom Class to create the custom Object Credits that holds seven elements
 * @Param title - tile of the article
 * @Param summary - summary of the article*
 * @Param section - section of the article from where we want news
 * @Param date - when the article was published
 * @Param author - who wrote the article
 * @Param url - the url of the article
 * @Param image - url of the header image used in the article
 **/
public class Article {

    private final String title;
    private final String summary;
    private final String section;
    private final String date;
    private final String author;
    private final String url;
    private final String image;

    //constructor needed to create the custom Object
    public Article(String title, String summary, String section, String date, String author, String url, String image) {
        this.title = title;
        this.summary = summary;
        this.section = section;
        this.date = date;
        this.author = author;
        this.url = url;
        this.image = image;
    }

    //get methods to retrieve their values
    public String getSummary() {
        return summary;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getAuthor() {
        return author;
    }

    public String getUrl() {
        return url;
    }

    public String getImage() {
        return image;
    }
}
