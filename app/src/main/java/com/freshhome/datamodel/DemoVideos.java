package com.freshhome.datamodel;

public class DemoVideos {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String isWatched() {
        return isWatched;
    }

    public void setisWatched(String watched) {
        isWatched = watched;
    }

    String name;
    String url;
    String id;

    public int getCurrentVideo() {
        return currentVideo;
    }

    public void setCurrentVideo(int currentVideo) {
        this.currentVideo = currentVideo;
    }

    int currentVideo;
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    String description;
    String isWatched;


}
