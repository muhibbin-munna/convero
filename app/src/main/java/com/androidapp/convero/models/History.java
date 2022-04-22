package com.androidapp.convero.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class History implements Serializable {

    private int id;
    String date, item1,  item2,  item_name;

    public History() {
    }

    public History(int id, String date, String item1, String item2, String item_name) {
        this.id = id;
        this.date = date;
        this.item1 = item1;
        this.item2 = item2;
        this.item_name = item_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getItem1() {
        return item1;
    }

    public void setItem1(String item1) {
        this.item1 = item1;
    }

    public String getItem2() {
        return item2;
    }

    public void setItem2(String item2) {
        this.item2 = item2;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }
}
