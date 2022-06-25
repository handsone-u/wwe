package com.whatweeat.wwe.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data @AllArgsConstructor
public class RecommendMenu {
    private String name;
    private String imageURL;
    private List<String> keywords;
}
