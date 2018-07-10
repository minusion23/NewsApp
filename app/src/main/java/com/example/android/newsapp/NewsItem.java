package com.example.android.newsapp;

/**
 * Created by Szymon on 10.06.2018.
 */

public class NewsItem {

    private String mTimeInMilliseconds;

    private String mArticleTitle;

    private String mUrl;

    private String mAuthorName;

    private String mAuthorLastName;

    private String mSectionName;

    public NewsItem (String timeInMilliseconds, String ArticleTitle, String Url, String authorName, String authorLastName, String sectionName ){

        mTimeInMilliseconds = timeInMilliseconds;
        mArticleTitle = ArticleTitle;
        mUrl = Url;
        mAuthorName = authorName;
        mAuthorLastName = authorLastName;
        mSectionName = sectionName;

    }

    public String getTimeInMilliseconds (){

        return    mTimeInMilliseconds;
    }

    public String getArticleTitle(){
        return mArticleTitle;
    }

    public String getmUrl() {
        return mUrl;
    }

    public String getAuthorName() {
        return mAuthorName;
    }

    public String getAuthorLastName(){
        return mAuthorLastName;
    }

    public String getSectionName() {
        return mSectionName;
    }
}
