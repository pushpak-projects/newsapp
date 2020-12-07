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

import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {

    private Context context;
    private News[] newsArr;
    SharedPreferences sharedpreferences;
    public BookmarkAdapter(Context context,News[] newsArr)
    {
        this.context=context;
        this.newsArr=newsArr;

    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("debug","inside onCreateViewHolder");
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.bookmark_card_item,parent,false);
        return new BookmarkViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return newsArr.length;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final BookmarkViewHolder holder,int position) {
        Log.d("debug", "inside onBindViewHolder");
        Log.d("position", String.valueOf(position));
        //final News news = newsArr[position];

        Log.d("onBindViewHolder", String.valueOf(newsArr[position]));
        Log.d("image", newsArr[position].getImage());
        final String title = newsArr[position].getTitle();
        final String imageView = newsArr[position].getImage();
        final String date = newsArr[position].getDate();
        final String section = newsArr[position].getSectionName();

        final String webPublicationDate = newsArr[position].getWebPublicationDate();
        final String webUrl = newsArr[position].getWebUrl();
        Log.d("BookmarkAdapter", webPublicationDate);
        holder.bookmark_title.setText(title);
        holder.bookmark_section.setText(section);

        holder.bookmark_date.setText(convertToTimeDiff(webPublicationDate));
        final String id = newsArr[position].getId();
        Glide.with(holder.bookmark_image.getContext()).load(imageView).into(holder.bookmark_image);
        holder.bookmark_icon.setImageResource(R.drawable.baseline_bookmark_black_18dp);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(context, DetailedActivity.class);

                myIntent.putExtra("id", id);
                context.startActivity(myIntent);
            }
        });


        holder.bookmark_icon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                holder.cardView.setVisibility(view.GONE);

                Toast.makeText(context, title + " was removed from bookmarks",
                        Toast.LENGTH_SHORT).show();
                sharedpreferences = context.getSharedPreferences("BookmarkPref",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.remove(id);


            }
        });


        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom);
                dialog.findViewById(R.id.bookmark_filled_popup).setVisibility(v.VISIBLE);
                dialog.findViewById(R.id.bookmark_popup).setVisibility(v.GONE);

                if (dialog.findViewById(R.id.bookmark_filled_popup).getVisibility() == View.VISIBLE) {
                    dialog.findViewById(R.id.bookmark_filled_popup).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("onclick", "bookmark icon clicked in popup");
                            dialog.findViewById(R.id.bookmark_filled_popup).setVisibility(View.GONE);
                            dialog.findViewById(R.id.bookmark_popup).setVisibility(View.VISIBLE);
                            sharedpreferences = context.getSharedPreferences("BookmarkPref",
                                    Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.remove(id);
                            editor.commit(); // commit changes
                            Toast.makeText(context, title + " was removed from bookmarks",
                                    Toast.LENGTH_SHORT).show();
                            holder.cardView.setVisibility(view.GONE);

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


                TextView titlePopup = (TextView) dialog.findViewById(R.id.titlePopup);
                titlePopup.setText(title);
                ImageView imagePopup = (ImageView) dialog.findViewById(R.id.imagePopup);
                Glide.with(imagePopup.getContext()).load(imageView).into(imagePopup);
                dialog.show();
                return true;

            }
        });

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
        return DateTimeFormatter.ofPattern("dd MMM").format(zdt) + " | ";
    }



    public class BookmarkViewHolder extends RecyclerView.ViewHolder{
        ImageView bookmark_image;
        TextView bookmark_title;
        TextView bookmark_date;
        TextView bookmark_section;
        CardView cardView;
        ImageView bookmark_icon;
        ImageView bookmark_icon_remove;
        public BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            bookmark_image=(ImageView)itemView.findViewById(R.id.bookmark_image);
            bookmark_title = (TextView)itemView.findViewById(R.id.bookmark_title);
            bookmark_date= (TextView)itemView.findViewById(R.id.bookmark_date);
            bookmark_section = (TextView)itemView.findViewById(R.id.bookmark_section);
            bookmark_icon = (ImageView)itemView.findViewById(R.id.bookmark_icon);
            cardView = (CardView)itemView.findViewById(R.id.bookmark_card);
            bookmark_icon_remove=(ImageView)itemView.findViewById(R.id.bookmark_icon_remove);
        }
    }
}
