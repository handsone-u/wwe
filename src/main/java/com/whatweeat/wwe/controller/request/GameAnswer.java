package com.whatweeat.wwe.controller.request;

import com.whatweeat.wwe.entity.enums.NationName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class GameAnswer {
    private Boolean rice;
    private Boolean noodle;
    private Boolean soup;
    private Boolean hangover;
    private Boolean greasy;
    private Boolean health;
    private Boolean alcohol;
    private Boolean instant;
    private Boolean spicy;
    private Boolean rich;
    private final Set<NationName> nation = new HashSet<>();
}
