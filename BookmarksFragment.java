package com.example.newsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Map;

public class BookmarksFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("BookmarksFragment","inside onCreateView");
        SharedPreferences pref = getActivity().getSharedPreferences("BookmarkPref",
                Context.MODE_PRIVATE);
        if(pref.getAll().size() == 0)
            return inflater.inflate(R.layout.empty_bookmark,container,false);

        View view = inflater.inflate(R.layout.fragment_bookmark,container,false);

        int pref_size=pref.getAll().size();
        Log.d("bookmarked articles",String.valueOf(pref_size));
        News[] newsArr = new News[pref_size];

        Map<String,?> keys = pref.getAll();
        int i=0;
        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.d("map values",entry.getKey() + ": " +
                    entry.getValue().toString());
            String news = entry.getValue().toString();
            System.out.print("news" + news);
            String[] params = news.split("#~");
            Log.d("length" ,String.valueOf(params.length));
            Log.d("key" ,entry.getKey());
            Log.d("BookmarksFragment",params[2]);
            Log.d("params",params[0]+','+params[1]+','+params[2]+','+params[3]+','+params[4]);

            newsArr[i]=new News(params[1],params[3],params[2],params[0],entry.getKey(),params[4],params[5]);
            i++;
        }
        final RecyclerView bookmarkList = (RecyclerView) view.findViewById(R.id.bookmark_list);
        bookmarkList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        bookmarkList.setAdapter(new BookmarkAdapter(getContext(),newsArr));
        return view;
    }
}
