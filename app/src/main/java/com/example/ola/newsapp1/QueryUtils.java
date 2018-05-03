package com.example.ola.newsapp1;

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

import static com.example.ola.newsapp1.ArticleActivity.LOG_TAG;
/**
 * Helper methods related to requesting and receiving articles data from Guardian.
 */
public class QueryUtils {

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */

    // Keys used for the JSON response
    private static final String json_response = "response";
    private static final String json_results = "results";
    private static final String json_section = "sectionName";
    private static final String json_date = "webPublicationDate";
    private static final String json_title = "webTitle";
    private static final String json_url = "webUrl";
    private static final String json_tags = "tags";
    private static final String json_author = "webTitle";

    private QueryUtils() {
    }

    /**
     * Query the Guardian dataset and return a list of {@link Article} objects.
     */
    public static List<Article> fetchArticleData(String requestUrl){

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Article}s
        List<Article> articlesList = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Article}s
        return articlesList;
    }


    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl){
        URL url = null;
        try{
            // Try to create an URL from String
            url = new URL(stringUrl);
        } catch (MalformedURLException e){
            // In case that request failed, print the error message into log
            Log.e(LOG_TAG,"Problem building the URL", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException{
        String jsonResponse = "";

    // if url is empty, return earlier
        if(url == null){
            return jsonResponse;
        }

        // Initialize variables for the HTTP connection and for the InputStream
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            // Send a request to connect
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if(urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG,"Error response code " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            // If the connection was not established, print it to the log
            Log.e(LOG_TAG, "Problem retrieving the Guardian JSON results.", e);
        } finally {

            // Disconnect the HTTP connection if it has been not yet disconnected
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream != null){
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            // Append the data of the BufferedReader line by line to the StringBuilder
            String line = bufferedReader.readLine();
            while(line != null){
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        // Convert the output into String and return it
        return output.toString();
    }

    /**
     * Return a list of {@link Article} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Article> extractFeatureFromJson(String articlesJson){
        // If the JSON string is empty or null, then return early.
        if(TextUtils.isEmpty(articlesJson)){
            return null;
        }

        // Create an empty ArrayList that we can start adding articles to
        List<Article> articlesList = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try{
            String articleAuthor = "N/A";
            JSONArray currentArticleAuthorArray;
            JSONObject articleTag;

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(articlesJson);

            JSONObject response = baseJsonResponse.getJSONObject(json_response);

            JSONArray results = response.getJSONArray(json_results);

            for (int i = 0; i <results.length() ; i++) {

                // Get a single article at position i within the list of articles
                JSONObject currentArticle = results.getJSONObject(i);

                // For a given article, extract the JSONObject associated with the key ,
                // which represents a list of all properties for that article.

                // Extract the article name (value) for the key "webTitle"
                String title = currentArticle.getString(json_title);

                // Extract the section name (value) for the key "sectionName"
                String section = currentArticle.getString(json_section);

                // Extract the date (value) for the key "webPublicationDate"
                String date = currentArticle.getString(json_date );

                // Extract the url (value) for the key "webUrl"
                String url = currentArticle.getString(json_url);

                // Extract the author (value) for the key "webTitle"
                if (currentArticle.has(json_tags)) {
                    currentArticleAuthorArray = currentArticle.getJSONArray(json_tags);

                    if (currentArticleAuthorArray.length() > 0) {
                        for (int j = 0; j < 1; j++) {
                            articleTag = currentArticleAuthorArray.getJSONObject(j);
                            if (articleTag.has(json_author)) {
                                articleAuthor = articleTag.getString(json_author);
                            }
                        }
                    }
                }

                // Create a new {@link Article} object with the title, date, section, author
                // and url from the JSON response.
                Article articlesObject = new Article(title, section, date, articleAuthor, url);

                // Add the new {@link Article} to the list of articles.
                articlesList.add(articlesObject);
            }


        } catch (JSONException e) {
            Log.e("QueryUtils","Problem parsing the earthquake JSON results", e);
        }

        // Return the list of articles
        return articlesList;
    }


}
