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
public class RestaurantUser implements Serializable
{
    	String nameUser;
	String nameRestaurant;
	float percentage;
        String color;
        
        public RestaurantUser(String nameUser, String nameRestaurant, float percentage, String color)
        {
            this.color = color;
            this.nameRestaurant = nameRestaurant;
            this.nameUser = nameUser;
            this.percentage = percentage;
        }
        
            public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getNameRestaurant() {
        return nameRestaurant;
    }

    public void setNameRestaurant(String nameRestaurant) {
        this.nameRestaurant = nameRestaurant;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
    
    public void printObjectInfo()
    {
        System.out.println("("+getColor()+") " +getNameRestaurant()+ ": "+getPercentage()+" for "+getNameUser()+"");
    }
}
