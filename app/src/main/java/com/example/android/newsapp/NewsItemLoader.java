package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Szymon on 10.06.2018.
 */

public class NewsItemLoader extends AsyncTaskLoader<List<NewsItem>>  {
    /** Tag for log messages */


    private String mUrl;

    public NewsItemLoader(Context context, String url){
        super(context);
        mUrl = url;
    }
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<NewsItem> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of newsItem.
        List<NewsItem> newsItems = QueryUtils.fetchNewsItemData(mUrl);
        return newsItems;

    }
}
