package com.leandrogodon.redditsampleapp.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Post extends Thing {

    public Data data;

    public boolean hasThumbnail() {
        return data.thumbnail != null && !TextUtils.isEmpty(data.thumbnail);
    }

    public String getImageUrl() {
        if (data.preview != null
                && data.preview.images != null
                && !data.preview.images.isEmpty()
                && data.preview.images.get(0).source != null) {
            return data.preview.images.get(0).source.url;
        }
        return null;
    }

    public String getFullname() {
        return kind + "_" + data.id;
    }

    public class Data {
        public String id;
        public String title;
        public String author;
        public String thumbnail;
        public String permalink;
        public long created;
        @SerializedName("num_comments")
        public int comments;
        public int ups;
        public int downs;
        public Preview preview;
    }

    public class Preview {
        public List<Image> images;
    }
}
