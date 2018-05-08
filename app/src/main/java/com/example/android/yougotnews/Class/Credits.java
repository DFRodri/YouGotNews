package com.example.android.yougotnews.Class;

/**
 * Custom Class to create the custom Object Credits that holds two elements
 * @Param creditName - name of the website
 * @Param creditURL - url of the website
 **/
public class Credits {

    private final String newCreditName;
    private final String newCreditURL;

    //constructor needed to create the custom Object
    public Credits(String newCreditName, String newCreditURL) {
        this.newCreditName = newCreditName;
        this.newCreditURL = newCreditURL;
    }

    //get methods to retrieve their values
    public String getNewCreditName() {
        return newCreditName;
    }

    public String getNewCreditURL() {
        return newCreditURL;
    }

}