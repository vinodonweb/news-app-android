package com.example.newsapp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Article implements Parcelable {

    private final String author;
    private  final String title;
    private final String description;
    private final String url;
    private final String urlToImage;
    private final String publishedAt;


    Article(String author, String title, String description, String url, String urlToImage, String publishedAt){
        this.author = author != null ? author : "";
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
    }

    //for the to maintain State
    protected Article(Parcel in) {
        author = in.readString();
        title = in.readString();
        description = in.readString();
        url = in.readString();
        urlToImage = in.readString();
        publishedAt = in.readString();
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(url);
        dest.writeString(urlToImage);
        dest.writeString(publishedAt);
    }

    public String getAuthor(){
        return author;
    }

    public String getTitle(){return title; }

    public String getDescription() {return description;}

    public String getUrl() {return url; }

    public String getUrlToImage() { return urlToImage;}

    public String getPublishedAt() {return publishedAt; }


    @Override
    public String toString(){
        return "Article{" +
                "author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", urlToImage='" + urlToImage + '\'' +
                ", publishedAt='" + publishedAt + '\'' +
                '}';
    }


}
