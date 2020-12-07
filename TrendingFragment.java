package com.example.newsapp;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.widget.SearchView;
//import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class TrendingFragment extends Fragment {


    private static final String chart_URL = "https://hw9-backend1.wl.r.appspot.com/news/google-trends";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trending, container, false);
        SearchView searchView = view.findViewById(R.id.searchView); // inititate a search view
        final LineChart lineChart = (LineChart) view.findViewById(R.id.lineChart);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                getMPAndroidChart(query,lineChart);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return view;
    }

    public void getMPAndroidChart(final String query, final LineChart lineChart) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
            Log.d("query",query);
            StringRequest stringRequest = new StringRequest(chart_URL + "?keyword=" + query , new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        Log.d("mpandroidchart","try block in mpandroidchart");
                        JSONObject jsonOb = new JSONObject(response);
                        JSONArray arr = null;
                         arr = jsonOb.getJSONObject("default").getJSONArray("timelineData");
                         ArrayList<Entry> entries = new ArrayList<>();
                            String yVal = "";
                            for (int i = 0; i < arr.length(); i++) {

                                yVal = arr.getJSONObject(i).getString("value");
                                yVal=yVal.replace("[","");
                                yVal=yVal.replace("]","");
                                entries.add(new Entry(i, Float.valueOf(yVal)));
                            }
                            LineDataSet lineDataSet = new LineDataSet(entries, "Trending Chart for " + query);
                            lineDataSet.setColor(Color.rgb(0,0,139));
                            lineDataSet.setCircleColor(Color.rgb(0,0,139));
                            List dataSets = new ArrayList<>();
                            dataSets.add(lineDataSet);
                            Legend legend = lineChart.getLegend();
                            legend.setTextColor(Color.BLACK);
                            LineData data = new LineData(dataSets);
                            lineChart.setData(data);
                        lineChart.getXAxis().setDrawGridLines(false);
                        lineChart.getAxisRight().setDrawGridLines(false);
                        lineChart.getAxisLeft().setDrawGridLines(false);
                        
                            lineChart.invalidate();

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("volley error",String.valueOf(error));

                }

            });

                                queue.add(stringRequest);
    }
}
