package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;

import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class DetailedActivity extends AppCompatActivity {

    private final static String detail_URL = "https://hw9-backend1.wl.r.appspot.com/news/guardianSection";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        Toolbar toolbar = findViewById(R.id.toolbarDetailed);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent myIntent = getIntent(); // gets the previously created intent
        final String id = myIntent.getStringExtra("id"); // will return "FirstKeyValue"


        Log.d("id", id);
        RequestQueue queue = Volley.newRequestQueue(DetailedActivity.this);
        StringRequest stringRequest = new StringRequest(detail_URL + "?section="+id + "&apiKey=d6d06dbc-9431-4c6f-a539-18e9e6f6fbf7", new Response.Listener<String>() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {
                JSONObject baseJsonResponse = null;

                try {
                    baseJsonResponse = new JSONObject(response);

                    // Extract the JSONObject associated with the key called "response"
                    JSONObject responseJsonObject = null;

                    responseJsonObject = baseJsonResponse.getJSONObject("response");
                    final JSONObject content = responseJsonObject.getJSONObject("content");
                    final String imageUrl = content.getJSONObject("blocks").getJSONObject("main").getJSONArray("elements").getJSONObject(0).getJSONArray("assets").getJSONObject(0).getString("file");
                    final ImageView imageNews = (ImageView) findViewById(R.id.imageDetailNews);
                    Glide.with(getApplicationContext()).load(imageUrl).into(imageNews);
                    final TextView title = (TextView) findViewById(R.id.titleDetail);
                    title.setText(content.getString("webTitle"));
                    final TextView description = (TextView) findViewById(R.id.descriptionDetail);
                    description.setText(HtmlCompat.fromHtml(content.getJSONObject("blocks").getJSONArray("body").getJSONObject(0).getString("bodyHtml"), 0));
                    final TextView section = (TextView) findViewById(R.id.sectionDetail);
                    section.setText(content.getString("sectionName"));
                    final TextView date = (TextView) findViewById(R.id.dateDetail);
                    final String publishDate = convertToTimeDiff(content.getString("webPublicationDate"));
                    date.setText(publishDate);
                    final TextView link= (TextView) findViewById(R.id.URLDetail);

                    getSupportActionBar().setTitle(content.getString("webTitle"));
                    SpannableString articleLink = new SpannableString("View Full Article");
                    articleLink.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    link.setText(articleLink);
                    link.setMovementMethod(LinkMovementMethod.getInstance());
                    ImageView twitter = (ImageView) findViewById(R.id.twitter_image);

                    twitter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        Uri twitterUri = Uri.parse("https://twitter.com/intent/tweet?text=Check out this link&url="
                                                + content.getString("webUrl"));
                                        Intent urlIntent = null;
                                        urlIntent = new Intent(Intent.ACTION_VIEW, twitterUri);
                                        startActivity(urlIntent);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });

                    link.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                Uri newsUri = Uri.parse(content.getString("webUrl"));
                                Intent urlIntent = null;
                                urlIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                                startActivity(urlIntent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                    SharedPreferences sharedpreferences = getSharedPreferences("BookmarkPref",
                            Context.MODE_PRIVATE);
                    Map<String, ?> keys = sharedpreferences.getAll();
                    int i = 0;
                    for (Map.Entry<String, ?> entry : keys.entrySet()) {
                        if (entry.getKey().equals(id)){
                            Log.d("NewsCardAdapter", "article is present in shared pref");
                            findViewById(R.id.bookmark_detailed_image).setVisibility(View.GONE);
                            findViewById(R.id.bookmark_detailed_image_full).setVisibility(View.VISIBLE);

                            break;
                        }
                    }
                    if (findViewById(R.id.bookmark_detailed_image).getVisibility() == View.VISIBLE) {
                        findViewById(R.id.bookmark_detailed_image).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d("onclick", "bookmark icon clicked");
                                SharedPreferences sharedpreferences = getSharedPreferences("BookmarkPref",
                                        Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                try {
                                    editor.putString(id, imageNews + "#~" + title + "#~" + date + "#~" + section + "#~" + publishDate + "#~" + content.getString("webUrl"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                editor.commit(); // commit changes
                                Toast.makeText(getBaseContext(),title + " was added to bookmarks",
                                        Toast.LENGTH_SHORT).show();
                                findViewById(R.id.bookmark_detailed_image).setVisibility(view.GONE);
                                findViewById(R.id.bookmark_detailed_image_full).setVisibility(view.VISIBLE);

                            }
                        });
                    }


                    if (findViewById(R.id.bookmark_detailed_image_full).getVisibility() == View.VISIBLE) {
                        findViewById(R.id.bookmark_detailed_image_full).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d("onclick", "bookmark done icon clicked");
                                SharedPreferences sharedpreferences = getSharedPreferences("BookmarkPref",
                                        Context.MODE_PRIVATE);

                                findViewById(R.id.bookmark_detailed_image_full).setVisibility(view.GONE);
                                findViewById(R.id.bookmark_detailed_image).setVisibility(view.VISIBLE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.remove(id);
                                editor.commit(); // commit changes
                                Toast.makeText(getBaseContext(), title + " was removed from bookmarks",
                                        Toast.LENGTH_SHORT).show();
                                findViewById(R.id.bookmark_detailed_image_full).setVisibility(view.GONE);
                                findViewById(R.id.bookmark_detailed_image).setVisibility(view.VISIBLE);


                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("DetailedActivity", String.valueOf(e));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("DetailedActivity",String.valueOf(error));
                Toast.makeText(DetailedActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
            }

        });
        queue.add(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String convertToTimeDiff(String webPublicationDate)
    {
        LocalDateTime ldt = LocalDateTime.now();
        System.out.println(ldt);
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        LocalDateTime localDateTime = LocalDateTime.parse(webPublicationDate,inputFormatter);
        ZoneId zoneId = ZoneId.of( "America/Los_Angeles" );

        ZonedDateTime zdt = localDateTime.atZone( zoneId );
        return DateTimeFormatter.ofPattern("dd MMM yyyy").format(zdt);
    }
}



