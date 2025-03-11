package com.example.newsapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newsapp.databinding.ActivityNewsEntryBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class NewsArticleAdapter extends
        RecyclerView.Adapter<NewsArticleViewHolder> {

    private final ArrayList<Article> articleList;
    private static final String TAG = "ArticleAdapter";

    public NewsArticleAdapter(ArrayList<Article> articleList) {
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public NewsArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ActivityNewsEntryBinding binding =
                ActivityNewsEntryBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false);

        return new NewsArticleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsArticleViewHolder holder, int position) {
        Article article = articleList.get(position);

        // Setting text views
        holder.binding.title.setText(article.getTitle());


        String RDate = convertDateFormat(article.getPublishedAt());
        holder.binding.date.setText(RDate);

        Log.d(TAG, "TextView height before GONE: " + holder.binding.author.getHeight());
        Log.d(TAG, "TextView visibility before setting: " + holder.binding.author.getVisibility());

        if (article.getAuthor() == null || article.getAuthor().isEmpty()) {
            holder.binding.author.setVisibility(View.GONE);
        } else {
            holder.binding.author.setVisibility(View.VISIBLE);
            holder.binding.author.setText(article.getAuthor());
        }
        Log.d(TAG, "Author value: " + article.getAuthor() + " for position: " + position);

        if (article.getDescription() == null || article.getDescription().isEmpty()) {
            holder.binding.description.setVisibility(View.GONE);
        } else {
            holder.binding.description.setVisibility(View.VISIBLE);
            holder.binding.description.setText(article.getDescription());
        }

        // Set the article count text
        String articleCountText = (position + 1) + " of " + getItemCount();
        holder.binding.articleCount.setText(articleCountText);


        Glide.with(holder.binding.urlToImage.getContext())
                .load(article.getUrlToImage())
                .error(R.drawable.noimage)
                .into(holder.binding.urlToImage);

        // Set the article as the tag for the clickable views
        holder.binding.title.setTag(article);
        holder.binding.urlToImage.setTag(article);
        holder.binding.description.setTag(article);
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }


    public String convertDateFormat(String oldDate){
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        SimpleDateFormat changeFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

        try {
            Date date = isoFormat.parse(oldDate);
            return changeFormat.format(date);
        } catch (ParseException e) {
            Log.d(TAG, "convertToHumanReadableDate: " + e);
            return "";
        }
    }

}