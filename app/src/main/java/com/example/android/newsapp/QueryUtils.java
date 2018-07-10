package com.example.android.newsapp;

/**
 * Created by Szymon on 10.06.2018.
 */
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.newsapp.MainActivity.LOG_TAG;


public final class QueryUtils {
public static final int readTimeout = 10000;
public static final int connectionTimeout = 15000;
public static final int isOk = 200;

    private static List<NewsItem> extractFeatureFromJson (String jsonresponse){
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonresponse)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding newsItems to
        List<NewsItem> newsItems = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(jsonresponse);
            JSONObject baseJsonResponse2 = baseJsonResponse.getJSONObject("response");
            // Extract the JSONArray associated with the key called "results",
            // which represents a list of news items).
            JSONArray newsItemsArray = baseJsonResponse2.getJSONArray("results");

            // For each newsItems in the NewsItems, create an {@link NewsItems} object
            for (int i = 0; i < newsItemsArray.length(); i++) {
                String authorName ="";
                String authorLastName = "";
                // Get a single newsItem at position i within the list of NewsItems
                JSONObject currentNewsItem = newsItemsArray.getJSONObject(i);
                JSONArray authorDetails = currentNewsItem.getJSONArray("tags");
                if (authorDetails.length() > 0) {
                    JSONObject author = authorDetails.getJSONObject(0);
                    if (author.has("firstName")){
                        authorName = author.optString("firstName");
                    }
                    if (author.has("lastName")){
                        authorLastName = author.optString("lastName");
                    }
                }


                // For a given newsItem, extract the JSONObject associated with the
                // key called "properties", which represents a list of all properties
                // for that Newsitem.

                 String timeInMilliseconds = currentNewsItem.optString("webPublicationDate");

                 String articleTitle = currentNewsItem.optString("webTitle");

                 String url = currentNewsItem.optString("webUrl");

                 String sectionName = currentNewsItem.optString("sectionName");

                // Create a new {@link newsItem} object with the time, articleTitle, url,authorName, authorLastName, sectionName
                // and url from the JSON response.
                NewsItem newsItem = new NewsItem(timeInMilliseconds, articleTitle, url, authorName, authorLastName, sectionName);

                // Add the new {@link newsItem} to the list of newsItems.
                newsItems.add(newsItem);
            }


        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the  JSON results", e);
        }

        // Return the list of NewsItems
        return newsItems;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e("LOG_TAG", "Error with creating URL", exception);

            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
//            if URL is null, then return
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(readTimeout /* milliseconds */);
            urlConnection.setConnectTimeout(connectionTimeout /* milliseconds */);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == isOk) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("LOG_TAG", "Error response code:" + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e("Exception", "An Exception was thrown", e);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
    /**
     * Query the Guardian dataset return a list of {@link NewsItem} objects.
     */
    public static List<NewsItem> fetchNewsItemData(String requestUrl) {
        // Create URL object

        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link NewsItems}s
        Log.e(LOG_TAG,jsonResponse);
        List<NewsItem> newsItems = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link newsItems}
        return newsItems;
    }

}
