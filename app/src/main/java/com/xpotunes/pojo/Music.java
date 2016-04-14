package com.xpotunes.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Music implements Cloneable {

    @SerializedName("_id")
    @Expose
    private String Id;
    @SerializedName("file")
    @Expose
    private String file;
    @SerializedName("size")
    @Expose
    private Integer size;
    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("filename")
    @Expose
    private String filename;
    @SerializedName("artist")
    @Expose
    private String artist;
    @SerializedName("genre")
    @Expose
    private String genre;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("album")
    @Expose
    private String album;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("dislikes")
    @Expose
    private Integer dislikes;
    @SerializedName("likes")
    @Expose
    private Integer likes;
    @SerializedName("views")
    @Expose
    private Integer views;
    @SerializedName("start")
    @Expose
    private Integer start;
    @SerializedName("end")
    @Expose
    private Integer end;



    @SerializedName("trailers")
    @Expose
    private Integer trailers;

    @Expose(serialize = false, deserialize = false)
    private boolean mIsTrailer;

    /**
     * @return The end
     */
    public Integer getEnd() {
        return end;
    }

    /**
     * @param end The end
     */
    public void setEnd(Integer end) {
        this.end = end;
    }

    /**
     * @return The start
     */
    public Integer getStart() {
        return start;
    }

    /**
     * @param start The start
     */
    public void setStart(Integer start) {
        this.start = start;
    }

    /**
     * @return The Id
     */
    public String getId() {
        return Id;
    }

    /**
     * @param Id The _id
     */
    public void setId(String Id) {
        this.Id = Id;
    }

    /**
     * @return The file
     */
    public String getFile() {
        return file;
    }

    /**
     * @param file The file
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * @return The size
     */
    public Integer getSize() {
        return size;
    }

    /**
     * @param size The size
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * @return The user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user The user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return The filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename The filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return The artist
     */
    public String getArtist() {
        return artist;
    }

    /**
     * @param artist The artist
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * @return The album
     */
    public String getAlbum() {
        return album;
    }

    /**
     * @param album The album
     */
    public void setAlbum(String album) {
        this.album = album;
    }

    /**
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The dislikes
     */
    public Integer getDislikes() {
        return dislikes;
    }

    /**
     * @param dislikes The dislikes
     */
    public void setDislikes(Integer dislikes) {
        this.dislikes = dislikes;
    }

    /**
     * @return The likes
     */
    public Integer getLikes() {
        return likes;
    }

    /**
     * @param likes The likes
     */
    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    /**
     * @return The views
     */
    public Integer getViews() {
        return views;
    }

    /**
     * @param views The views
     */
    public void setViews(Integer views) {
        this.views = views;
    }

    /**
     * @return The genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * @param genre The genre
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTrailers() {
        return trailers;
    }

    public void setTrailers(Integer trailers) {
        this.trailers = trailers;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public void setTrailer(boolean trailer) {
        mIsTrailer = trailer;
    }

    public boolean isTrailer() {
        return mIsTrailer;
    }

    public static Music clone(Music music){
        Music clone = new Music();

        clone.title = music.title;
        clone.album = music.album;
        clone.artist = music.artist;
        clone.genre = music.genre;
        clone.description = music.description;
        clone.filename = music.filename;
        clone.size = music.size;
        clone.start = music.start;
        clone.end = music.end;
        clone.user = music.user;
        clone.file = music.file;
        clone.trailers = music.trailers;
        clone.views = music.views;
        clone.likes = music.likes;
        clone.dislikes = music.dislikes;
        clone.Id = music.Id;
        clone.mIsTrailer = music.mIsTrailer;

        return clone;
    }
}