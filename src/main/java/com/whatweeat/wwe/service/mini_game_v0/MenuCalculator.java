package com.whatweeat.wwe.service.mini_game_v0;

import com.whatweeat.wwe.dto.MenuPoint;
import com.whatweeat.wwe.entity.MiniGameV0;
import com.whatweeat.wwe.entity.mini_game_v0.V0Member;

import java.util.List;

public interface MenuCalculator {
//    double calculateV0(MiniGameV0 menuV0, V0Member member);
    MenuPoint calculate(MiniGameV0 menuV0, List<V0Member> members);
}
