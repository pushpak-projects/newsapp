package com.example.newsapp;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class BusinessFragment extends Fragment {

    private static final String URL="https://hw9-backend1.wl.r.appspot.com/news/guardianSection?section=business&apiKey=d6d06dbc-9431-4c6f-a539-18e9e6f6fbf7";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_business,container,false);
        final RecyclerView newsList = (RecyclerView) view.findViewById(R.id.newsListBusiness);
        newsList.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.d("onCreateView","inside business fragment");
        view.findViewById(R.id.progress_business).setVisibility(view.VISIBLE);
        view.findViewById(R.id.business_text).setVisibility(view.VISIBLE);
        view.findViewById(R.id.newsListBusiness).setVisibility(view.GONE);
        StringRequest request = new StringRequest(URL, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {
                view.findViewById(R.id.progress_business).setVisibility(view.GONE);
                view.findViewById(R.id.business_text).setVisibility(view.GONE);
                view.findViewById(R.id.newsListBusiness).setVisibility(view.VISIBLE);

                Log.d("response",response);
                News[] newsArr = new News[10];
                JSONObject baseJsonResponse = null;

                try {
                    baseJsonResponse = new JSONObject(response);

                    // Extract the JSONObject associated with the key called "response"
                    JSONObject responseJsonObject = null;

                    responseJsonObject = baseJsonResponse.getJSONObject("response");

                    // Extract the JSONArray associated with the key called "results"
                    JSONArray resultsArray = null;

                    resultsArray = responseJsonObject.getJSONArray("results");

                    int j=0;
                    // For each element in the resultsArray, create a {@link News} object
                    for (int i = 0; i < resultsArray.length(); i++) {

                        // Get a single news at position i within the list of news
                        JSONObject currentNews = resultsArray.getJSONObject(i);
                        // For a given news, extract the value for the key called "webTitle"
                        String webTitle = currentNews.getString("webTitle");
                        // For a given news, extract the value for the key called "sectionName"
                        String sectionName = currentNews.getString("sectionName");
                        String id = currentNews.getString("id");

                        //ZonedDateTime zdt_curr= ldt.atZone( zoneId );
                        // For a given news, extract the value for the key called "webPublicationDate"
                        String webPublicationDate = currentNews.getString("webPublicationDate");
                        String webUrl = currentNews.getString("webUrl");
                        String timeDiff = convertToTimeDiff(webPublicationDate);
                        if(!currentNews.has("blocks"))
                            continue;
                        if(!currentNews.getJSONObject("blocks").has("main"))
                            continue;
                        if(!currentNews.getJSONObject("blocks").getJSONObject("main").has("elements"))
                            continue;
                        JSONArray elementArr=currentNews.getJSONObject("blocks").getJSONObject("main").getJSONArray("elements");
                        if(elementArr.length() == 0)
                        {
                            Log.d("BusinessFragment","element array is empty");
                            continue;
                        }
                        if(!elementArr.getJSONObject(0).has("assets"))
                        {
                            Log.d("BusinessFragment","element array does not have any asset");
                            continue;
                        }
                        JSONArray assetsArr = elementArr.getJSONObject(0).getJSONArray("assets");
                        if(assetsArr.length() == 0)
                        {
                            Log.d("BusinessFragment","assets array is empty");
                            continue;
                        }
                        JSONObject imgObj = assetsArr.getJSONObject(0);
                        if(!imgObj.has("file") || imgObj.getString("file").equals(""))
                        {
                            Log.d("BusinessFragment","imgObj does not have any file");
                            continue;
                        }
                        String image = imgObj.getString("file");
                        Log.d("image", image);

                        News news = new News(webTitle, sectionName, timeDiff, image,id,webPublicationDate,webUrl);

                        newsArr[j]=news;
                        j++;
                    }
                    Log.d("array length",String.valueOf(newsArr.length));
                    newsList.setAdapter(new NewsCardAdapter(getContext(),newsArr));

                }
                catch(Exception e)
                {
                    Log.e("Exception","Error occured while JSON parsing");
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("onErrorResponse","Error when calling Business news URL");
                        Toast.makeText(getContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
        return view;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String convertToTimeDiff(String webPublicationDate)
    {
        LocalDateTime ldt = LocalDateTime.now();
        System.out.println(ldt);
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        LocalDateTime localDateTime = LocalDateTime.parse(webPublicationDate,inputFormatter);
        ZoneId zoneId = ZoneId.of( "America/Los_Angeles" );
        ZonedDateTime zdtCurrTime = ldt.atZone( zoneId );
        ZonedDateTime zdt = localDateTime.atZone( zoneId );
        System.out.println(zdt);
        Duration duration = Duration.between(zdt,zdtCurrTime);
        long sec = duration.getSeconds();
        System.out.println(sec);
        long absSec=Math.abs(sec);
        if(absSec<60){
            Log.d("sec diff",absSec+"s ago | ");
            return absSec+"s ago | ";
        }

        else if(absSec>60 && absSec<3600)
        {
            long min=absSec/60;
            Log.d("min diff",min+"m ago | ");
            return min+"m ago | ";
        }
        else
        {
            long hr=absSec/3600;
            Log.d("hr diff",hr+"h ago | ");
            return hr+"h ago | ";
        }
    }
}
