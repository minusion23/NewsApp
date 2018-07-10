package com.example.android.newsapp;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.net.ConnectivityManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsItem>>{

    public static final String LOG_TAG = MainActivity.class.getName();


    private NewsItemAdapter mAdapter;
    private static final String GUARDIAN_REQUEST_URL ="http://content.guardianapis.com/search?";
//    private static final String GUARDIAN_REQUEST_URL ="http://content.guardianapis.com/search?q=poland&tag=politics/politics&show-tags=contributor&api-key=test";
    private static final int NEWSITEM_LOADER_ID = 1;
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView newsItemListView = (ListView) findViewById(R.id.list);
        mEmptyStateTextView =(TextView) findViewById(R.id.empty_view);
        newsItemListView.setEmptyView(mEmptyStateTextView);
        mAdapter = new NewsItemAdapter(this, new ArrayList<NewsItem>());
        newsItemListView.setAdapter(mAdapter);

        newsItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current NewsItem that was clicked on
                NewsItem currentNewsItem = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsItemUri = Uri.parse(currentNewsItem.getmUrl());

                // Create a new intent to view the newsItem URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsItemUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        }
        );

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // Get a reference to the LoaderManager, in order to interact with loaders.
        if (networkInfo != null && networkInfo.isConnected()){
             // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            getLoaderManager().initLoader(NEWSITEM_LOADER_ID,null, this);
        }

        else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
        SwipeRefreshLayout mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        recreate();
                    }
                });
    }

    @Override
    public Loader<List<NewsItem>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
//
        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String sectionName = sharedPrefs.getString(
                getString(R.string.settings_section_name_key),
               getString(R.string.settings_section_name_default));
        String testString = getString(R.string.settings_order_by_newest_value);
        Log.e(LOG_TAG,testString);
        String orderBy  = sharedPrefs.getString(
              getString(R.string.settings_order_by_key),
              getString(R.string.settings_order_by_default)
        );
        Log.e(LOG_TAG,"order by is "+ orderBy);
//        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
//
//        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();
//
        // Append query parameter and its value. For example, the `format=geojson`
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("api-key","test");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        if (!sectionName.equalsIgnoreCase("") || sectionName.contentEquals(" ") ) {
            uriBuilder.appendQueryParameter("section", sectionName);
        }
        uriBuilder.appendQueryParameter("q","Poland");

        //    private static final String GUARDIAN_REQUEST_URL ="http://content.guardianapis.com/search?q=poland&tag=politics/politics&show-tags=contributor&api-key=test";
        Log.e(LOG_TAG,"Query is" + uriBuilder);

        return new NewsItemLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<NewsItem>> loader, List<NewsItem> newsItems) {
        // Clear the adapter of previous News item data
        mAdapter.clear();
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_indicator);
        progressBar.setVisibility(View.GONE);
        // If there is a valid list of {@link NewsItem}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (newsItems != null && !newsItems.isEmpty()) {
            mAdapter.addAll(newsItems);
        }
        // Set empty state text to display "No newsItems found."

        mEmptyStateTextView.setText(R.string.no_newsItems);
    }

    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

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

