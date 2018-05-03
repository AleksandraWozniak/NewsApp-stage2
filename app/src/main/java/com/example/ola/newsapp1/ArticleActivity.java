/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.ola.newsapp1;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.net.ConnectException;
import java.net.NetworkInterface;

// ArticleActivity implements the LoaderCallbacks interface, along with a generic parameter specifying what the loader will return (in this case article)
public class ArticleActivity extends AppCompatActivity implements LoaderCallbacks<List<Article>> {

    /** Adapter for the list of news/articles */
    private ArticleAdapter mAdapter;

    /**
     * Constant value for the news loader ID. We can choose any integer.
     * This really only comes into play if we're using multiple loaders. We need to specify an ID for our loader
     */
    private static final int ARTICLE_LOADER_ID = 1;

    public static final String LOG_TAG = ArticleActivity.class.getName();

    private static final String GUARDIAN_REQUEST_URL =
           //"http://content.guardianapis.com/search?order-by=newest&show-tags=contributor&page-size=20&q=politics&api-key=6f2b2fc3-131e-4731-8fbb-dd514a95c728";
            "https://content.guardianapis.com/search?";

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    /** Message for the user */
    private String mMessageForTheUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_list_item);

        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = (ListView) findViewById(R.id.list);

        // No articles have been found. Display this information on the screen
        mEmptyStateTextView = findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of news as input
        mAdapter = new ArticleAdapter(this, new ArrayList<Article>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected earthquake.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Find the current article that was clicked on
                Article currentArticle = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                assert currentArticle != null;
                Uri newsUri = Uri.parse(currentArticle.getNewsUrl());

                // Create a new intent to view the news URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Check if there is any web browser available. If there is not, display toast message
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(websiteIntent,
                        PackageManager.MATCH_DEFAULT_ONLY);

                boolean isIntentSafe = activities.size() > 0;

                if (isIntentSafe) {

                    // Start the intent
                    startActivity(websiteIntent);

                } else {
                    // Update an empty state with no internet connection error message
                    mMessageForTheUser = (String) getText(R.string.no_webbrowser);
                    mEmptyStateTextView.setText(mMessageForTheUser);
                }
            }

            });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if(networkInfo != null && networkInfo.isConnected()){
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).

            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View progressBar = findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);
            // Update an empty state with no internet connection error message
            mMessageForTheUser = (String) getText(R.string.no_internet);
            mEmptyStateTextView.setText(mMessageForTheUser);
        }

    }

    // We need to override the three methods specified in the LoaderCallbacks interface.
    // We need onCreateLoader(), for when the LoaderManager has determined that the loader with our specified ID isn't running, so we should create a new one
    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        String minArticles = sharedPrefs.getString(
                getString(R.string.settings_number_of_articles_key),
                getString(R.string.settings_number_of_articles_default));

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // "http://content.guardianapis.com/search?order-by=newest&show-tags=contributor&page-size=20&q=politics&api-key=6f2b2fc3-131e-4731-8fbb-dd514a95c728";

        // Append query parameter and its value
        uriBuilder.appendQueryParameter(getString(R.string.order_by), orderBy);
        uriBuilder.appendQueryParameter(getString(R.string.section), getString(R.string.technology));
        uriBuilder.appendQueryParameter(getString(R.string.show_tags), getString(R.string.contributor));
        uriBuilder.appendQueryParameter(getString(R.string.page_size), minArticles);
        uriBuilder.appendQueryParameter(getString(R.string.q), "");
        uriBuilder.appendQueryParameter(getString(R.string.api_key), getString(R.string.api_key_value));

        // Create a new loader for the given URL
        return new ArticleLoader(this, uriBuilder.toString());
    }

    // We need onLoadFinished()
    // and use the news/article data to update our UI - by updating the dataset in the adapter
    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        // Hide loading indicator because the data has been loaded
        View progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        // Clear the adapter of previous news data
        mAdapter.clear();

        // If there is a valid list of {@link Article}s, then add them to the adapter's
        // data set. This will trigger the ListView to update
        if(articles != null && !articles.isEmpty()){
            Log.e(LOG_TAG, "Loader load finished");
            mAdapter.addAll(articles);

            if (articles.isEmpty()) {
                // Set empty state text to display "No news found."
                mMessageForTheUser = (String) getText(R.string.no_news);
                mEmptyStateTextView.setText(mMessageForTheUser);
            }
        }
    }

    // We need onLoaderReset(), we're being informed that the data from our loader is no longer valid
    // The correct thing to do is to remove all the news data from our UI by clearing out the adapter’s data set
    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        Log.e(LOG_TAG, "Loader reset");
        // Loader reset, so we can clear out our existing data
        mAdapter.clear();
    }

    // This method initialize the contents of the Activity's options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // This method is called whenever an item in the options menu is selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
