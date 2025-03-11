package com.example.newsapp;

import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.databinding.ActivityNewsEntryBinding;

public class NewsArticleViewHolder extends RecyclerView.ViewHolder {

    ActivityNewsEntryBinding binding;

    public NewsArticleViewHolder(ActivityNewsEntryBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
