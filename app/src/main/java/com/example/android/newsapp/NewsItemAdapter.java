package com.example.android.newsapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

/**
 * Created by Szymon on 10.06.2018.
 */

public class NewsItemAdapter extends ArrayAdapter<NewsItem>{



    public NewsItemAdapter (Context context, ArrayList<NewsItem> newsItems){
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        super(context, 0, newsItems);
    }

    static class ViewHolder {
        TextView authorTextView;
        TextView articleTitleTextView;
        TextView sectionTextView;
        TextView dateTextView;
    }

    public View getView(int position, View convertView, ViewGroup parent){
          View listItemView = convertView;
          ViewHolder holder;

//        if convertView is empty inflate the view

        if (convertView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent,false);
            holder = new ViewHolder();
            holder.authorTextView = (TextView) listItemView.findViewById(R.id.authorName);
            holder.articleTitleTextView = (TextView) listItemView.findViewById(R.id.article_title);
            holder.sectionTextView = (TextView) listItemView.findViewById(R.id.sectionName);
            holder.dateTextView = (TextView) listItemView.findViewById(R.id.date);
            listItemView.setTag(holder);
        }

        else {
        holder = (ViewHolder) listItemView.getTag();
        }


    // Get the {@link NewsItem} object located at this position in the list
        NewsItem currentNewsItem = getItem(position);
        String fullTitle = currentNewsItem.getArticleTitle();
        String firstName;
        String lastName;
        String authorName;
        firstName = currentNewsItem.getAuthorName();
        lastName = currentNewsItem.getAuthorLastName();
        authorName = firstName + " " +lastName;
        if (authorName == null || authorName ==""){
            holder.authorTextView.setVisibility(View.GONE);
        }

        else {
            holder.authorTextView.setText(authorName);
        }
        holder.articleTitleTextView.setText(fullTitle);
        holder.sectionTextView.setText(currentNewsItem.getSectionName());
       // Find the TextView in the list_item.xml layout with the ID date and fill the textView in the correct format

        String date = currentNewsItem.getTimeInMilliseconds();
        String formatedDate = date.substring(0,10);
        holder.dateTextView .setText(formatedDate);

        return  listItemView;

    }

}
