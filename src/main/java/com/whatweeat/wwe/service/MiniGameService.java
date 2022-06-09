package com.whatweeat.wwe.service;

import com.whatweeat.wwe.controller.request.ResultSubmission;
import com.whatweeat.wwe.entity.mini_game_v0.V0Group;

public interface MiniGameService {
    int createGroup();

    V0Group saveResult(ResultSubmission dto);

    void getGroupResult(int pin);

    void deleteGroup(int pin);

    void deleteMember(String token, Integer pin);

    boolean pinValidCheck(int id);

    int countMember(int id);

    int countCompleteMember(int id);
}
