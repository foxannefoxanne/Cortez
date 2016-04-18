package eecs581_582.cortez.frontend;

import eecs581_582.cortez.backend.Constants;

/**
 * Credit: https://github.com/hmkcode/Android/blob/master/android-pro-listview/src/com/hmkcode/android/Model.java
 */
public class MediaSelectItem {

    private Constants.MediaType mediaType;
    private int icon;
    private String title;
    private String counter;
    private String link;

    public MediaSelectItem(Constants.MediaType mediaType, String title, String counter, String link) {
        super();
        this.mediaType = mediaType;
        this.icon = mediaType.getIcon();
        this.title = title;
        this.counter = counter;
        this.link = link;
    }
    public int getIcon() {
        return icon;
    }
    public void setIcon(int icon) {
        this.icon = icon;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getCounter() {
        return counter;
    }
    public String getLink() {
        return link;
    }
    public Constants.MediaType getMediaType() {
        return mediaType;
    }
    public void setCounter(String counter) {
        this.counter = counter;
    }
}