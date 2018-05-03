package com.example.ola.newsapp1;

/**
 * Created by Ola on 30.04.2018.
 */

public class Article {
    // Title of the news/article
    private String mNewsTitle;

    // News category
    private String mNewsSection;

    // Author of the news/article
    private String mNewsAuthor;

    // Date of the news/article
    private String mNewsDate;

    /** Website URL of the news/article */
    private String mNewsUrl;

    /// Constructor of news/article object
    /**
     * Constructs a new {@link Article} object.
     *
     * @param title is the title of news/article
     * @param section is the news category
     * @param author is the author's name
     * @param date is when the news was published
     * @param url is the website URL to find more details about the news
     */
    public Article(String title, String section, String author, String date, String url){
        mNewsTitle = title;
        mNewsSection = section;
        mNewsAuthor = author;
        mNewsDate = date;
        mNewsUrl = url;
    }

    // Public getter methods so that each data type is returned

    /** Returns the title of the news/article */
    public String getNewsTitle(){
        return mNewsTitle;
    }

    /** Returns the category of the news. */
    public String getNewsSection(){
        return mNewsSection;
    }

    /** Returns the category of the news. */
    public String getAuthorsName(){
        return mNewsAuthor;
    }

    /** Returns the date when the news was published.*/
    public String getNewsDate(){
        return mNewsDate;
    }

    /** Returns the website URL to find more information about the news. */
    public String getNewsUrl(){
        return mNewsUrl;
    }
}
