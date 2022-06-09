package com.whatweeat.wwe.service.mini_game_v0;

import com.whatweeat.wwe.controller.request.ResultSubmission;
import com.whatweeat.wwe.entity.enums.FlavorName;
import com.whatweeat.wwe.entity.enums.NationName;
import com.whatweeat.wwe.entity.mini_game_v0.V0Exclude;
import com.whatweeat.wwe.entity.mini_game_v0.V0Group;
import com.whatweeat.wwe.entity.mini_game_v0.V0Member;
import com.whatweeat.wwe.entity.mini_game_v0.V0Nation;
import com.whatweeat.wwe.repository.mini_game_v0.V0GroupRepository;
import com.whatweeat.wwe.repository.mini_game_v0.V0MemberRepository;
import com.whatweeat.wwe.service.MiniGameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor @Slf4j
@Transactional
public class MiniGameV0ServiceImpl implements MiniGameService {
    private final V0GroupRepository v0GroupRepository;
    private final V0MemberRepository v0MemberRepository;
    private final int BOUND = 10000;
    private final int LOOP_MAX = 1000;

    public int createGroup() {
        int id = generatePinNum();
        V0Group v0Group = new V0Group(id);
        v0Group = v0GroupRepository.save(v0Group);

        return v0Group.getId();
    }

    public V0Group saveResult(ResultSubmission dto) {
        V0Group group = v0GroupRepository.findById(dto.getPinNumber())
                .orElseThrow(() -> new RuntimeException());

        return saveGroup(dto, group);
    }

    private V0Group saveGroup(ResultSubmission dto, V0Group group) {
        V0Member member = dto.toV0Member();
        member = saveMember(dto, member);

        group.addMember(member);
        return v0GroupRepository.save(group);
    }

    private V0Member saveMember(ResultSubmission dto, V0Member member) {
        log.info("MEMBER SAVE Token = [{}]", member.getToken());
        saveExcludes(dto.getDislikedFoods(), member);
        saveNations(dto.getGameAnswer().getNation(), member);

        return v0MemberRepository.save(member);
    }

    private void saveExcludes(Set<FlavorName> excludeNames, V0Member member) {
        for (FlavorName excludeName : excludeNames) {
            V0Exclude exclude = new V0Exclude(member, excludeName);
            member.addExclude(exclude);
        }
    }
    private void saveNations(Set<NationName> nationNames, V0Member member) {
        for (NationName nationName : nationNames) {
            V0Nation nation = new V0Nation(member, nationName);
            member.addNation(nation);
        }
    }

    public void getGroupResult(int pin) {
        V0Group v0Group = v0GroupRepository.findById(pin)
                .orElseThrow(() -> new RuntimeException());
        List<V0Member> members = v0Group.getMembers();
        // TODO: FEAT
        // 그룹 미니게임 추론 로직
    }

    public void deleteGroup(int pin) {
        V0Group v0Group = v0GroupRepository.findById(pin)
                .orElseThrow(() -> new RuntimeException());

        v0GroupRepository.delete(v0Group);
    }

    public void deleteMember(String token, Integer pin) {
        V0Group v0Group = v0GroupRepository.findById(pin)
                .orElseThrow(() -> new RuntimeException());
        V0Member v0Member = v0MemberRepository.findByTokenAndGroup(token, v0Group)
                .orElseThrow(() -> new RuntimeException());

        v0Group.removeMember(v0Member);
    }

    public boolean pinValidCheck(int id) {
        return v0GroupRepository.findById(id).isPresent();
    }

    public int countMember(int id) {
        V0Group v0Group = v0GroupRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException());
        return v0Group.getMembers().size();
    }

    public int countCompleteMember(int id) {
        V0Group v0Group = v0GroupRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException());
        return (int) v0Group.getMembers().stream()
                .filter(V0Member::getComplete)
                .count();
    }

    private int generatePinNum() {
        // TODO: VALID
        // PIN 갯수 이상의 group 있다면 생성 불가능 할것.
        int groupCount = (int) v0GroupRepository.count();
        log.info("GROUP Total Count = [{}]", groupCount);

        Random random = new Random(LocalDateTime.now().hashCode());
        int total = random.nextInt(BOUND);
        int count = 0;
        while (v0GroupRepository.findById(total).isPresent()) {
            log.warn("PIN NUM : [{}] DUP, GENERATING NEW NUM", total);
            total = random.nextInt(BOUND);
            count++;
        }
        if(count>=LOOP_MAX) total = v0GroupRepository.getMaxId() + 1;

        log.info("GROUP CREATED... PIN = [{}]", total);
        return total;
    }
}
