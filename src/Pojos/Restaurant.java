/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pojos;

import java.io.Serializable;

/**
 *
 * @author Pablo
 */
public class Restaurant implements Serializable
{

    String nameRestaurant;
    String url;
    String url_map;
    double latitude;
    double longitude;

    public Restaurant(String nameRestaurant, String url, String url_map, double latitude, double longitude) {
        this.nameRestaurant = nameRestaurant;
        this.url = url;
        this.url_map = url_map;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public String getNameRestaurant() {
        return nameRestaurant;
    }

    public void setNameRestaurant(String nameRestaurant) {
        this.nameRestaurant = nameRestaurant;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl_map() {
        return url_map;
    }

    public void setUrl_map(String url_map) {
        this.url_map = url_map;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    
}
