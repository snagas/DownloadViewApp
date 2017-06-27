package com.example.lenovot550.downloadviewapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Lenovo T550 on 23/06/2017.
 */

public class ArticleResponse {

    // Parasom kokius JSON objektus is GET atsakymo reikia pasiimt
    @SerializedName("status")
    public String status;

    @SerializedName("source")
    public String source;

    public String getStatus() {

        return status;
    }

    public String getSource() {
        return source;
    }
}
