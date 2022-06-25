package com.whatweeat.wwe.service.mini_game_v0;

import com.whatweeat.wwe.controller.request.GameAnswer;
import com.whatweeat.wwe.controller.request.ResultSubmission;
import com.whatweeat.wwe.dto.MenuPoint;
import com.whatweeat.wwe.entity.Menu;
import com.whatweeat.wwe.entity.enums.FlavorName;
import com.whatweeat.wwe.entity.enums.NationName;
import com.whatweeat.wwe.entity.mini_game_v0.V0Exclude;
import com.whatweeat.wwe.entity.mini_game_v0.V0Group;
import com.whatweeat.wwe.entity.mini_game_v0.V0Member;
import com.whatweeat.wwe.entity.mini_game_v0.V0Nation;
import com.whatweeat.wwe.repository.mini_game_v0.V0GroupRepository;
import com.whatweeat.wwe.repository.mini_game_v0.V0MemberRepository;
import com.whatweeat.wwe.service.MenuService;
import com.whatweeat.wwe.service.MiniGameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor @Slf4j
@Transactional
// TODO : Feat,RuntimeException -> 사용자 정의 예외
public class MiniGameV0ServiceImpl implements MiniGameService {
    private final MenuService menuServiceImpl;

    private final V0GroupRepository v0GroupRepository;
    private final V0MemberRepository v0MemberRepository;

    private final MenuCalculator menuCalculatorV0;

    private final int BOUND = 10000;
    private final int LOOP_MAX = 1000;

    public int createGroup(String token) {
        int id = generatePinNum();
        V0Member host = V0Member.createHost(token);
        V0Group group = v0GroupRepository.save(new V0Group(id, host));
        log.debug("PIN : [{}], Host Token : [{}]", group.getId(), host.getToken());

        return group.getId();
    }

    @Override
    public V0Group findGroup(Integer pin) {
        return findGroupByPinNumber(pin);
    }

    @Transactional(readOnly = true)
    public List<V0Group> getAllGroup() {
        return v0GroupRepository.findAll();
    }

    public V0Group saveResult(ResultSubmission dto) {
        return saveGroup(dto, findGroupByPinNumber(Integer.parseInt(dto.getPinNumber())));
    }
    private V0Group saveGroup(ResultSubmission dto, V0Group group) {
        boolean present = v0MemberRepository.findByTokenAndGroup(dto.getToken(), group).isPresent();
        V0Member member = saveMember(dto, group);
        if (present)
            saveMember(dto, group);
        else
            group.addMember(member);

        return v0GroupRepository.save(group);
    }
    private V0Member saveMember(ResultSubmission dto, V0Group group) {
        V0Member member = v0MemberRepository.findByTokenAndGroup(dto.getToken(), group)
                .orElseGet(() -> new V0Member(dto.getToken(), true));
        log.debug("MEMBER SAVE Token = [{}]", member.getToken());
        member = resultToMember(dto, member);

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

    @Transactional(readOnly = true)
    public List<MenuPoint> getSoloResult(ResultSubmission resultSubmission) {
        log.debug("SOLO 추론 시작");

        V0Member v0Member = new V0Member(resultSubmission.getToken(), true);
        v0Member = resultToMember(resultSubmission, v0Member);
        Set<FlavorName> groupExclude = new HashSet<>();

        v0Member.getExcludes().forEach(exclude -> groupExclude.add(exclude.getExcludeName()));
        List<Menu> menus = menuServiceImpl.findAllExceptFlavorNames(groupExclude);

        List<MenuPoint> result = new ArrayList<>(menus.size());
        for (Menu menu : menus) {
            MenuPoint menuPoint = menuCalculatorV0.calculate(menu.getMiniGameV0(), List.of(v0Member));

            log.debug("MENU Name:[{}] Point=[{}]", menuPoint.getMenuName(), menuPoint.getPoint());
            result.add(menuPoint);
        }
        result.sort(Comparator.reverseOrder());
        return result;
    }

    private V0Member resultToMember(ResultSubmission resultSubmission, V0Member v0Member) {
        GameAnswer answer = resultSubmission.getGameAnswer();
        v0Member = v0Member.saveGameResult(answer.getRice(), answer.getNoodle(), answer.getSoup(), answer.getHangover(),
                answer.getGreasy(), answer.getHealth(), answer.getAlcohol(), answer.getInstant(),
                answer.getSpicy(), answer.getRich());
        saveExcludes(resultSubmission.getDislikedFoods(), v0Member);
        saveNations(resultSubmission.getGameAnswer().getNation(), v0Member);
        return v0Member;
    }

    public List<MenuPoint> getGroupResult(int pin) {
        log.debug("GROUP PIN:[{}] 추론 시작",pin);

        // 그룹 조회
        V0Group v0Group = findGroupByPinNumber(pin);
        List<V0Member> members = v0Group.getMembers();
        Set<FlavorName> groupExclude = new HashSet<>();

        // 못 먹는 음식 제외 조회
        members.forEach(m -> m.getExcludes().forEach(exclude -> groupExclude.add(exclude.getExcludeName())));
        List<Menu> menus = menuServiceImpl.findAllExceptFlavorNames(groupExclude);

        // 메뉴 점수화
        List<MenuPoint> result = new ArrayList<>(menus.size());
        for (Menu menu : menus) { // N
            MenuPoint menuPoint = menuCalculatorV0.calculate(menu.getMiniGameV0(), members);

            log.debug("MENU Name:[{}] Point=[{}]", menuPoint.getMenuName(), menuPoint.getPoint());
            result.add(menuPoint);
        }
        v0Group.setIsOVer(true);

        result.sort(Comparator.reverseOrder());
        return result;
    }

    public void deleteGroup(int pin) {
        v0GroupRepository.delete(findGroupByPinNumber(pin));
    }

    public void makeMemberInvalid(String token, Integer pin) {
        V0Group group = findGroupByPinNumber(pin);
        V0Member member = v0MemberRepository.findByTokenAndGroup(token, group)
                .orElseThrow(() -> new RuntimeException("맴버를 찾을 수 없음. 잘못된 Token."));

        member.makeInvalid();
    }

    public void deleteMember(String token, Integer pin) {
        V0Group group = findGroupByPinNumber(pin);
        V0Member member = v0MemberRepository.findByTokenAndGroup(token, group)
                .orElseThrow(() -> new RuntimeException("맴버를 찾을 수 없음. 잘못된 Token."));

        group.removeMember(member);
    }

    private V0Group findGroupByPinNumber(int pin) {
        return v0GroupRepository.findById(pin)
                .orElseThrow(() -> new RuntimeException("그룹을 찾을 수 없음. 잘못된 PIN 번호."));
    }

    public boolean pinValidCheck(int pin) {
        return v0GroupRepository.findById(pin).isPresent();
    }

    @Transactional(readOnly = true)
    public int countMember(int pin) {
        V0Group v0Group = findGroupByPinNumber(pin);
        return v0Group.getMembers().size();
    }

    @Transactional(readOnly = true)
    public int countCompleteMember(int pin) {
        V0Group v0Group = findGroupByPinNumber(pin);
        return (int) v0Group.getMembers().stream()
                .filter(V0Member::getComplete)
                .count();
    }

    private int generatePinNum() {
        int groupCount = (int) v0GroupRepository.count();
        log.debug("GROUP Total Count = [{}]", groupCount);

        Random random = new Random(LocalDateTime.now().hashCode());
        int total = random.nextInt(BOUND);
        int count = 0;
        while (v0GroupRepository.findById(total).isPresent()) {
            log.warn("PIN NUM : [{}] DUP, GENERATING NEW NUM", total);
            total = random.nextInt(BOUND);
            count++;
        }
        if(count>=LOOP_MAX) total = v0GroupRepository.getMaxId() + 1;

        log.debug("GROUP CREATED... PIN = [{}]", total);
        return total;
    }
}
