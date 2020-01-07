package util;

import android.app.Application;
import android.location.Address;

import com.google.android.gms.maps.model.LatLng;

public class TodoApi extends Application {

    private String usename;
    private String userid;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    private Address address;



    private static TodoApi instance;

    public static TodoApi getInstance(){
        if(instance==null)
            instance=new TodoApi();
        return instance;
    }

    public TodoApi(){}

    public String getUsename() {
        return usename;
    }

    public void setUsename(String usename) {
        this.usename = usename;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public static void setInstance(TodoApi instance) {
        TodoApi.instance = instance;
    }
}
