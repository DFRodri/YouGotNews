package com.example.android.yougotnews.Class;

/**
 * Custom Class to create the custom Object Credits that holds seven elements
 * @Params title - tile of the article
 * @Params summary - summary of the article*
 * @Params section - section of the article from where we want news
 * @Params date - when the article was published
 * @Params author - who wrote the article
 * @Params url - the url of the article
 * @Params image - url of the header image used in the article
 **/
public class Article {

    private String title;
    private String summary;
    private String section;
    private String date;
    private String author;
    private String url;
    private String image;

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
