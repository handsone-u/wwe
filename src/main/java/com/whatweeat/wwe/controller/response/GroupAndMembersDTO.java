package com.whatweeat.wwe.controller.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data @NoArgsConstructor
public class GroupAndMembersDTO {
    private Integer pinNum;
    private final List<String> tokens = new ArrayList<>();

    public GroupAndMembersDTO(Integer pinNum) {
        this.pinNum = pinNum;
    }
}
