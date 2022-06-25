package com.whatweeat.wwe.controller.request;

import com.whatweeat.wwe.entity.enums.FlavorName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class ResultSubmission {
    private GameAnswer gameAnswer;
    private String pinNumber;
    private String token;
    private final Set<FlavorName> dislikedFoods = new HashSet<>();
}
