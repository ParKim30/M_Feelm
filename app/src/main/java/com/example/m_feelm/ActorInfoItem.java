package com.example.m_feelm;

public class ActorInfoItem {
    String poster;
    String title;

    ActorInfoItem(String title,String poster)
    {
        this.poster=poster;
        this.title=title;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
