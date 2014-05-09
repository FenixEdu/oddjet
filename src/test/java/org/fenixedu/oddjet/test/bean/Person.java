package org.fenixedu.oddjet.test.bean;

import java.io.Serializable;

public class Person implements Serializable {

    private String name;
    private int age;
    private localeObject favouriteFruit;
    private String dob;
    private Shirt currentShirt;

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public localeObject getFavouriteFruit() {
        return favouriteFruit;
    }

    public String getDob() {
        return dob;
    }

    public Shirt getCurrentShirt() {
        return currentShirt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setFavouriteFruit(localeObject favouriteFruit) {
        this.favouriteFruit = favouriteFruit;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setCurrentShirt(Shirt currentShirt) {
        this.currentShirt = currentShirt;
    }

}
