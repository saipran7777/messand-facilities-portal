package com.example.sai_praneeth7777.hasportal.Objects;

/**
 * Created by sai_praneeth7777 on 13-Jun-16.
 */
public class ObjectClass {
    private String name,id,type;
    public ObjectClass(String name,String id,String type){
        this.setName(name);
        this.setId(id);
        this.setType(type);
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}


