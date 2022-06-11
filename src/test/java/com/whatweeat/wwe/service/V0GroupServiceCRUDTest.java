package com.whatweeat.wwe.service;

import com.whatweeat.wwe.controller.request.GameAnswer;
import com.whatweeat.wwe.controller.request.ResultSubmission;
import com.whatweeat.wwe.entity.enums.FlavorName;
import com.whatweeat.wwe.entity.enums.NationName;
import com.whatweeat.wwe.entity.mini_game_v0.V0Group;
import com.whatweeat.wwe.entity.mini_game_v0.V0Member;
import com.whatweeat.wwe.repository.mini_game_v0.V0ExcludeRepository;
import com.whatweeat.wwe.repository.mini_game_v0.V0GroupRepository;
import com.whatweeat.wwe.repository.mini_game_v0.V0MemberRepository;
import com.whatweeat.wwe.repository.mini_game_v0.V0NationRepository;
import com.whatweeat.wwe.service.mini_game_v0.MiniGameV0ServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class V0GroupServiceCRUDTest {
    @Autowired
    V0GroupRepository v0GroupRepository;
    @Autowired
    V0MemberRepository v0MemberRepository;
    @Autowired
    V0ExcludeRepository v0excludeRepository;
    @Autowired
    V0NationRepository v0nationRepository;

    @Test
    void idGenerator() {
        MiniGameV0ServiceImpl service = new MiniGameV0ServiceImpl(null, v0GroupRepository, v0MemberRepository);
        service.createGroup();
        service.createGroup();
        assertThat(v0GroupRepository.count()).isEqualTo(2);
    }

    @Test @DisplayName("그룹 유효 확인")
    void groupPinNumValidCheck() {
        MiniGameV0ServiceImpl service = new MiniGameV0ServiceImpl(null, v0GroupRepository, v0MemberRepository);

        int pin1 = service.createGroup();
        int pin2 = service.createGroup();
        int pin3 = (pin1 + pin2) / 2;
        assertThat(v0GroupRepository.count()).isEqualTo(2);

        assertThat(service.pinValidCheck(pin1)).isTrue();
        assertThat(service.pinValidCheck(pin2)).isTrue();
        assertThat(service.pinValidCheck(pin3)).isFalse();
    }

    @Test @DisplayName("그룹 생성 & 그룹 참여")
    void createGroupAndJoinGroup() {
        MiniGameV0ServiceImpl service = new MiniGameV0ServiceImpl(null, v0GroupRepository, v0MemberRepository);

        int pin = service.createGroup();
        System.out.println("pin = " + pin);
        assertThat(v0GroupRepository.count()).isEqualTo(1);
        assertThat(v0MemberRepository.count()).isEqualTo(0);

        ResultSubmission hello = makeDTO("hello", pin);

        V0Group save = service.saveResult(hello);
        assertThat(v0GroupRepository.count()).isEqualTo(1);
        assertThat(v0MemberRepository.count()).isEqualTo(1);
        assertThat(v0MemberRepository.findAll()).extracting("complete")
                .containsOnly(true);
        assertThat(v0excludeRepository.count()).isEqualTo(1);
        assertThat(v0excludeRepository.findAll()).extracting("excludeName")
                .containsExactly(FlavorName.INTESTINE);
        assertThat(v0nationRepository.count()).isEqualTo(2);
        assertThat(v0nationRepository.findAll()).extracting("nationName")
                .containsOnly(NationName.KOREAN, NationName.EXOTIC);

        assertThat(save.getId()).isEqualTo(pin);
    }

    @Test @DisplayName("그룹 제거")
    void deleteGroup() {
        MiniGameV0ServiceImpl service = new MiniGameV0ServiceImpl(null, v0GroupRepository, v0MemberRepository);

        int pin = service.createGroup();
        System.out.println("pin = " + pin);
        assertThat(v0GroupRepository.count()).isEqualTo(1);
        assertThat(v0MemberRepository.count()).isEqualTo(0);

        ResultSubmission hello = makeDTO("hello", pin);
        ResultSubmission bye = ResultSubmission.builder()
                .pinNumber(pin)
                .token("bye")
                .gameAnswer(GameAnswer.builder().build())
                .build();

        service.saveResult(hello);
        service.saveResult(bye);

        assertThat(v0MemberRepository.count()).isEqualTo(2);
        assertThat(v0excludeRepository.count()).isEqualTo(1);
        assertThat(v0nationRepository.count()).isEqualTo(2);

        service.deleteGroup(pin);
        assertThat(v0GroupRepository.count()).isEqualTo(0);
        assertThat(v0MemberRepository.count()).isEqualTo(0);
        assertThat(v0excludeRepository.count()).isEqualTo(0);
        assertThat(v0nationRepository.count()).isEqualTo(0);
    }

    @Test @DisplayName("그룹-멤버 검색")
    void findMember() {
        MiniGameV0ServiceImpl service = new MiniGameV0ServiceImpl(null, v0GroupRepository, v0MemberRepository);

        int pinNum = service.createGroup();

        ResultSubmission resultSubmission = makeDTO("find", pinNum);
        V0Group group = service.saveResult(resultSubmission);
        group = v0GroupRepository.findById(group.getId())
                .orElseThrow(RuntimeException::new);
        V0Member member = group.getMembers().get(0);

        assertThat(member.getToken()).isEqualTo("find");
        assertThat(member.getComplete()).isTrue();
        assertThat(member.getAlcohol()).isTrue();
        assertThat(member.getGreasy()).isTrue();
        assertThat(member.getNoodle()).isTrue();
        assertThat(member.getRice()).isFalse();
        assertThat(member.getSoup()).isNull();
        assertThat(member.getHealth()).isNull();
        assertThat(member.getNations()).extracting("nationName")
                .containsOnly(NationName.KOREAN, NationName.EXOTIC);
    }

    @Test @DisplayName("멤버 제거")
    void deleteMember() {
        MiniGameService service = new MiniGameV0ServiceImpl(null, v0GroupRepository, v0MemberRepository);

        assertThat(v0GroupRepository.count()).isEqualTo(0);
        int pinNum = service.createGroup();
        assertThat(v0GroupRepository.count()).isEqualTo(1);

        ResultSubmission dto = makeDTO("hello", pinNum);
        V0Group group = service.saveResult(dto);
        V0Member member = group.getMembers().get(0);
        assertThat(v0MemberRepository.count()).isEqualTo(1);
        assertThat(group.getMembers().size()).isEqualTo(1);
        assertThat(v0excludeRepository.count()).isNotEqualTo(0);
        assertThat(v0nationRepository.count()).isNotEqualTo(0);

        System.out.println("DELETE!!!");
        service.deleteMember(member.getToken(), group.getId());
        group = v0GroupRepository.findById(group.getId())
                .orElseThrow(RuntimeException::new);

        assertThat(group.getMembers().size()).isEqualTo(0);
        assertThat(v0MemberRepository.count()).isEqualTo(0);
        assertThat(v0excludeRepository.count()).isEqualTo(0);
        assertThat(v0nationRepository.count()).isEqualTo(0);
    }

    private ResultSubmission makeDTO(String token, int pin) {
        GameAnswer gameAnswer = GameAnswer.builder()
                .alcohol(true)
                .greasy(true)
                .noodle(true)
                .rice(false)
                .health(null)
                .build();
        gameAnswer.getNation().addAll(Set.of(NationName.KOREAN, NationName.EXOTIC));
        ResultSubmission resultSubmission = ResultSubmission.builder()
                .gameAnswer(gameAnswer)
                .pinNumber(pin)
                .token(token)
                .build();
        resultSubmission.getDislikedFoods().add(FlavorName.INTESTINE);
        return resultSubmission;
    }
}