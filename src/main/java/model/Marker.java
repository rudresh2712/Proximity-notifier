package model;


import android.location.Address;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Timestamp;

public class Marker {

    private String title;
    private String todoinfo;
    private Address address;
    private String ImageUri;
    private String username;
    private String userid;
    private Timestamp timeadded;


    public Marker(String title, String todoinfo, Address address, String imageUri, String username, String userid, Timestamp timeadded) {
        this.title = title;
        this.todoinfo = todoinfo;
        this.address = address;
        ImageUri = imageUri;
        this.username = username;
        this.userid = userid;
        this.timeadded = timeadded;
    }

    public Marker() {//for storage purposes
         }

    public Timestamp getTimeadded() {
        return timeadded;
    }

    public void setTimeadded(Timestamp timeadded) {
        this.timeadded = timeadded;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTodoinfo() {
        return todoinfo;
    }

    public void setTodoinfo(String todoinfo) {
        this.todoinfo = todoinfo;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getImageUri() {
        return ImageUri;
    }

    public void setImageUri(String imageUri) {
        ImageUri = imageUri;
    }
}
