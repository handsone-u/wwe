package com.whatweeat.wwe.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data @AllArgsConstructor
public class RecommendResponse {
    private List<RecommendMenu> menus = new ArrayList<>();
    private Integer playerCount;
}
