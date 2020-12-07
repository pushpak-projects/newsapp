package com.example.newsapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.Map;
import java.util.Objects;

public class NewsCardAdapter extends RecyclerView.Adapter<NewsCardAdapter.NewsCardViewHolder> {

    private Context context;
    private News[] newsArr;
    SharedPreferences sharedpreferences;
    public NewsCardAdapter(Context context, News[] newsArr)
    {

        this.context=context;
        this.newsArr=newsArr;
    }

    @NonNull
    @Override
    public NewsCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("debug","inside onCreateViewHolder");
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.news_card_item,parent,false);
        return new NewsCardViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull final NewsCardViewHolder holder, final int position) {
        Log.d("debug","inside onBindViewHolder");
        Log.d("position",String.valueOf(position));


        Log.d("onBindViewHolder",String.valueOf(newsArr[position]));

        if(!Objects.isNull(newsArr[position])) {
            Log.d("image", newsArr[position].getImage());
            final String title = newsArr[position].getTitle();
            if(title == "")
                return;
            final String imageView = newsArr[position].getImage();
            if(imageView=="")
                return;
            final String date = newsArr[position].getDate();
            if(date == "")
                return;
            final String section = newsArr[position].getSectionName();
            if(section == "")
                return;
            final String webPublicationDate = newsArr[position].getWebPublicationDate();
            if(section == "")
                return;

            System.out.println("title:"+title);
            holder.title.setText(title);
            holder.section.setText(section);
            holder.date.setText(date);
            final String id = newsArr[position].getId();
            final String webUrl=newsArr[position].getWebUrl();
            Glide.with(holder.imageNews.getContext()).load(imageView).into(holder.imageNews);
            sharedpreferences = context.getSharedPreferences("BookmarkPref",
                    Context.MODE_PRIVATE);
            Map<String, ?> keys = sharedpreferences.getAll();
            int i = 0;
            for (Map.Entry<String, ?> entry : keys.entrySet()) {
                if (entry.getKey().equals(newsArr[position].getId())) {
                    Log.d("NewsCardAdapter", "article is present in shared pref");
                    holder.bookmark.setVisibility(View.GONE);
                    holder.bookmark_done.setVisibility(View.VISIBLE);

                    break;
                }
            }


            holder.bookmark.setImageResource(R.drawable.baseline_bookmark_border_black_18dp);
            if (holder.bookmark.getVisibility() == View.VISIBLE) {
                holder.bookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("onclick", "bookmark icon clicked");
                        sharedpreferences = context.getSharedPreferences("BookmarkPref",
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(id, imageView + "#~" + title + "#~" + date + "#~" + section + "#~" + webPublicationDate + "#~" + webUrl);
                        editor.commit(); // commit changes
                        Toast.makeText(context, title + " was added to bookmarks",
                                Toast.LENGTH_SHORT).show();
                        holder.bookmark.setVisibility(view.GONE);
                        holder.bookmark_done.setVisibility(view.VISIBLE);

                    }
                });
            }


            if (holder.bookmark_done.getVisibility() == View.VISIBLE) {
                holder.bookmark_done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("onclick", "bookmark done icon clicked");
                        sharedpreferences = context.getSharedPreferences("BookmarkPref",
                                Context.MODE_PRIVATE);

                        holder.bookmark_done.setVisibility(view.GONE);
                        holder.bookmark.setVisibility(view.VISIBLE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.remove(id);
                        editor.commit(); // commit changes
                        Toast.makeText(context, title + " was removed from bookmarks",
                                Toast.LENGTH_SHORT).show();
                        holder.bookmark_done.setVisibility(view.GONE);
                        holder.bookmark.setVisibility(view.VISIBLE);


                    }
                });
            }
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(context, DetailedActivity.class);

                    myIntent.putExtra("id", id);
                    context.startActivity(myIntent);
                }
            });


            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    Log.d("NewsCardAdapter", "onLongClick");
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.custom);

                    if (holder.bookmark_done.getVisibility() == v.VISIBLE) {
                        dialog.findViewById(R.id.bookmark_popup).setVisibility(v.GONE);
                        dialog.findViewById(R.id.bookmark_filled_popup).setVisibility(v.VISIBLE);
                    }

                    if (dialog.findViewById(R.id.bookmark_popup).getVisibility() == View.VISIBLE) {
                        dialog.findViewById(R.id.bookmark_popup).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d("onclick", "bookmark icon clicked in popup");
                                sharedpreferences = context.getSharedPreferences("BookmarkPref",
                                        Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(id, imageView + "#~" + title + "#~" + date + "#~" + section + "#~" + webPublicationDate+ "#~" + webUrl);
                                editor.commit(); // commit changes
                                Toast.makeText(context, title + " was added to bookmarks",
                                        Toast.LENGTH_SHORT).show();
                                holder.bookmark.setVisibility(view.GONE);
                                holder.bookmark_done.setVisibility(view.VISIBLE);
                                dialog.findViewById(R.id.bookmark_popup).setVisibility(view.GONE);
                                dialog.findViewById(R.id.bookmark_filled_popup).setVisibility(view.VISIBLE);

                            }
                        });
                    }
                        ImageView twitter = (ImageView) dialog.findViewById(R.id.twitter_popup);
                        twitter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    Uri twitterUri = Uri.parse("https://twitter.com/intent/tweet?text=Check out this link&url="
                                            + webUrl);
                                    Intent urlIntent = null;
                                    urlIntent = new Intent(Intent.ACTION_VIEW, twitterUri);
                                    context.startActivity(urlIntent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });



                    if (dialog.findViewById(R.id.bookmark_filled_popup).getVisibility() == View.VISIBLE) {
                        dialog.findViewById(R.id.bookmark_filled_popup).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d("onclick", "bookmark done icon clicked");
                                sharedpreferences = context.getSharedPreferences("BookmarkPref",
                                        Context.MODE_PRIVATE);

                                holder.bookmark_done.setVisibility(view.GONE);
                                holder.bookmark.setVisibility(view.VISIBLE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.remove(id);
                                editor.commit(); // commit changes
                                Toast.makeText(context, title + " was removed from bookmarks",
                                        Toast.LENGTH_SHORT).show();
                                holder.bookmark_done.setVisibility(view.GONE);
                                holder.bookmark.setVisibility(view.VISIBLE);
                                dialog.findViewById(R.id.bookmark_filled_popup).setVisibility(view.GONE);
                                dialog.findViewById(R.id.bookmark_popup).setVisibility(view.VISIBLE);

                            }
                        });
                    }
                    TextView titlePopup = (TextView) dialog.findViewById(R.id.titlePopup);
                    titlePopup.setText(title);
                    ImageView imagePopup = (ImageView) dialog.findViewById(R.id.imagePopup);
                    Glide.with(imagePopup.getContext()).load(imageView).into(imagePopup);
                    dialog.show();
                    return true;

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return newsArr.length;
    }

    public class NewsCardViewHolder extends RecyclerView.ViewHolder{
        ImageView imageNews;
        TextView title;
        TextView date;
        TextView section;
        ImageView bookmark;
        CardView cardView;
        ImageView bookmark_done;
        public NewsCardViewHolder(@NonNull View itemView) {
            super(itemView);
            imageNews=(ImageView)itemView.findViewById(R.id.imageNews);
            title = (TextView)itemView.findViewById(R.id.title);
            date= (TextView)itemView.findViewById(R.id.date);
            section = (TextView)itemView.findViewById(R.id.section);
            bookmark = (ImageView)itemView.findViewById(R.id.bookmark_image);
            cardView = (CardView)itemView.findViewById(R.id.news_card);
            bookmark_done = (ImageView)itemView.findViewById(R.id.bookmark_image_done);
        }
    }
}
