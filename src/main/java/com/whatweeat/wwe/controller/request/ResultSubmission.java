package com.whatweeat.wwe.controller.request;

import com.whatweeat.wwe.entity.enums.FlavorName;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ResultSubmission {
    private GameAnswer gameAnswer;
    private Integer pinNumber;
    private String token;
    private Set<FlavorName> dislikedFoods = new HashSet<>();
}
