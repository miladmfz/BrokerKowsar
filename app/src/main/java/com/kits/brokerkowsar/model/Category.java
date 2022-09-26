package com.kits.brokerkowsar.model;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class Category extends ExpandableGroup<Product> {

    public int id ;
    public int childno ;
    public String name ;


    public Category(String title, List<Product> items,int id,int childno) {
        super(title, items);
        this.id=id;
        this.name=title;
        this.childno=childno;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
