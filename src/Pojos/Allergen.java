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
public class Allergen implements Serializable
{

    String userName;
    boolean gluten;
    boolean lactose;
    boolean nuts;
    boolean eggs;
    boolean seaFood;
    
    public Allergen(String userName, boolean gluten, boolean lactose, boolean nuts, boolean eggs, boolean seaFood)
    {
        this.userName = userName;
        this.gluten = gluten;
        this.lactose = lactose;
        this.nuts = nuts;
        this.eggs = eggs;
        this.seaFood = seaFood;
        
    }
    
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isGluten() {
        return gluten;
    }

    public void setGluten(boolean gluten) {
        this.gluten = gluten;
    }

    public boolean isLactose() {
        return lactose;
    }

    public void setLactose(boolean lactose) {
        this.lactose = lactose;
    }

    public boolean isNuts() {
        return nuts;
    }

    public void setNuts(boolean nuts) {
        this.nuts = nuts;
    }

    public boolean isEggs() {
        return eggs;
    }

    public void setEggs(boolean eggs) {
        this.eggs = eggs;
    }
    
    public boolean isSeaFood() {
        return seaFood;
    }

    public void setSeaFood(boolean seaFood) {
        this.seaFood = seaFood;
    }
    
    
}
