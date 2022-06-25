package com.whatweeat.wwe.service;

import com.whatweeat.wwe.controller.request.ResultSubmission;
import com.whatweeat.wwe.dto.MenuPoint;
import com.whatweeat.wwe.entity.mini_game_v0.V0Group;

import java.util.List;

public interface MiniGameService {
    int createGroup(String token);

    V0Group findGroup(Integer pin);

    V0Group saveResult(ResultSubmission dto);

    List<V0Group> getAllGroup();

    List<MenuPoint> getSoloResult(ResultSubmission resultSubmission);

    List<MenuPoint> getGroupResult(int pin);

    void deleteGroup(int pin);

    void deleteMember(String token, Integer pin);

    void makeMemberInvalid(String token, Integer pin);

    boolean pinValidCheck(int pin);

    int countMember(int pin);

    int countCompleteMember(int pin);
}
