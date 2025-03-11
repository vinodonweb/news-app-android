package com.example.newsapp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Source implements Parcelable {

    private final String id;
    private final String name;
    private final String category;
    private final String language;
    private final String country;

    Source(String id, String name, String category, String language, String country){
        this.id = id;
        this.name = name; //news sources which will display in the drawer
        this.category = category;
        this.language = language;
        this.country = country;
    }

    //  state
    protected Source(Parcel in) {
        id = in.readString();
        name = in.readString();
        category = in.readString();
        language = in.readString();
        country = in.readString();
    }

    public static final Creator<Source> CREATOR = new Creator<Source>() {
        @Override
        public Source createFromParcel(Parcel in) {
            return new Source(in);
        }

        @Override
        public Source[] newArray(int size) {
            return new Source[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(category);
        dest.writeString(language);
        dest.writeString(country);
    }

    public String getId(){return id; }

    public String getName(){return name;}

    public String getCategory(){return category; }

    public String getLanguage(){ return language; }

    public String getCountry() { return country; }


    @NonNull
    public String toString(){
        return "Source{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", language='" + language + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}


