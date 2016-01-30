package com.leandrogodon.redditsampleapp.model;

import java.util.List;

public class Listing {

    public String kind;
    public Data data;

    public class Data {
        public String modhash;
        public List<Post> children;
    }
}
