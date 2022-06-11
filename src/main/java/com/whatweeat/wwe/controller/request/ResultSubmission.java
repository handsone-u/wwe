package com.whatweeat.wwe.controller.request;

import com.whatweeat.wwe.entity.enums.FlavorName;
import com.whatweeat.wwe.entity.mini_game_v0.V0Member;
import lombok.Builder;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data @Builder
public class ResultSubmission {
    private GameAnswer gameAnswer;
    private Integer pinNumber;
    private String token;
    private final Set<FlavorName> dislikedFoods = new HashSet<>();

    public V0Member toV0Member() {
        return new V0Member(token, true, gameAnswer.getRice(), gameAnswer.getNoodle(), gameAnswer.getSoup(),
                gameAnswer.getHangover(), gameAnswer.getGreasy(), gameAnswer.getHealth(), gameAnswer.getAlcohol(),
                gameAnswer.getInstant(), gameAnswer.getSpicy(), gameAnswer.getRich());
    }
}
