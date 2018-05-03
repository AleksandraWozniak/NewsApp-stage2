package com.example.ola.newsapp1;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.util.Log;
import java.util.ArrayList;

/**
 * Created by Ola on 30.04.2018.
 */

/**
 * {@link ArticleAdapter} is an {@link ArrayAdapter} that can provide the layout for each list item
 * based on a data source, which is a list of {@link Article} objects.
 */

public class ArticleAdapter extends ArrayAdapter<Article> {

    /**
     * Create a new {@link ArticleAdapter} object.
     *
     * @param context is the current context (i.e. Activity) that the adapter is being created in.
     * @param articles is the list of {@link Article}s to be displayed.
     */
    private static final String LOG_TAG = ArticleAdapter.class.getName();


    public ArticleAdapter(Context context, ArrayList<Article> articles){
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for some TextViews, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context,0,articles);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_activity,parent,false);
        }

        // Get the {@link ArticleClass} object located at this position in the list
        Article currentArticleClass = getItem(position);

        // Find the TextView with article title in the news_activity.xml layout with the ID
        TextView articleTitleTextView = listItemView.findViewById(R.id.news_title);
        // Get the title from the current object and set this text on that TextView
        assert currentArticleClass != null;
        articleTitleTextView.setText(currentArticleClass.getNewsTitle());

        // Find the TextView with article section in the news_activity.xml layout with the ID
        TextView articleSectionTextView = listItemView.findViewById(R.id.section);
        // Get the section from the current object and set this text on that TextView
        articleSectionTextView.setText(currentArticleClass.getNewsSection());

        // Find the TextView with author of the article in the news_activity.xml layout with the ID
        TextView authorArticleTextView = listItemView.findViewById(R.id.author);
        // Get the author of the article from the current object and set this text on that TextView
        authorArticleTextView.setText(currentArticleClass.getAuthorsName());

        // Find the TextView with date of article in the news_activity.xml layout with the ID
        TextView dateArticleTextView = listItemView.findViewById(R.id.date);
        // Get the date of the article from the current object and set this text on that TextView
        dateArticleTextView.setText(currentArticleClass.getNewsDate());

        // Return the whole list item layout
        // so that it can be shown in the ListView
        return listItemView;
    }
}
