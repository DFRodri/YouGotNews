package com.example.android.yougotnews.Class;

public class Article {

    private String title;
    private String summary;
    private String section;
    private String date;
    private String author;
    private String url;
    private String image;

    public Article(String title, String summary, String section, String date, String author, String url, String image) {
        this.title = title;
        this.summary = summary;
        this.section = section;
        this.date = date;
        this.author = author;
        this.url = url;
        this.image = image;
    }

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

    public String getSection() {
        return section;
    }
}
