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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class V0GroupServiceCRUDTest {
    MiniGameV0ServiceImpl service;

    @Autowired V0GroupRepository v0GroupRepository;
    @Autowired V0MemberRepository v0MemberRepository;
    @Autowired V0ExcludeRepository v0excludeRepository;
    @Autowired V0NationRepository v0nationRepository;

    @PersistenceContext EntityManager entityManager;

    @BeforeEach
    void init() {
        service = new MiniGameV0ServiceImpl(null, v0GroupRepository, v0MemberRepository, null);
    }

    @Test
    void idGenerator() {
        int pin1 = service.createGroup("host1");
        int pin2 = service.createGroup("host2");
        assertThat(v0GroupRepository.count()).isEqualTo(2);
        assertThat(v0MemberRepository.count()).isEqualTo(2);

        V0Group group1 = v0GroupRepository.findById(pin1).get();
        V0Group group2 = v0GroupRepository.findById(pin2).get();

        assertThat(group1.getIsOVer()).isFalse();
        assertThat(group2.getIsOVer()).isFalse();
    }

    @Test @DisplayName("그룹 유효 확인")
    void groupPinNumValidCheck() {
        int pin1 = service.createGroup("host1");
        int pin2 = service.createGroup("host2");
        int pin3 = (pin1 + pin2) / 2;
        assertThat(v0GroupRepository.count()).isEqualTo(2);

        assertThat(service.pinValidCheck(pin1)).isTrue();
        assertThat(service.pinValidCheck(pin2)).isTrue();
        assertThat(service.pinValidCheck(pin3)).isFalse();

        assertThat(v0MemberRepository.count()).isEqualTo(2);
    }

    @Test @DisplayName("그룹 생성 & 그룹 참여")
    void createGroupAndJoinGroup() {
        int pin = service.createGroup("host");
        System.out.println("pin = " + pin);
        assertThat(v0GroupRepository.count()).isEqualTo(1);
        assertThat(v0MemberRepository.count()).isEqualTo(1);

        ResultSubmission hello = makeDTO("hello", pin,
                Set.of(NationName.KOREAN, NationName.EXOTIC),
                Set.of(FlavorName.INTESTINE, FlavorName.SEAFOOD));

        System.out.println("SAVING");
        V0Group save = service.saveResult(hello);

        assertThat(save.getId()).isEqualTo(pin);
        assertThat(v0GroupRepository.count()).isEqualTo(1);
        assertThat(v0MemberRepository.count()).isEqualTo(2);
        assertThat(v0MemberRepository.findAll()).extracting("complete")
                .containsExactly(false, true);
        assertThat(v0excludeRepository.count()).isEqualTo(2);
        assertThat(v0excludeRepository.findAll()).extracting("excludeName")
                .containsOnly(FlavorName.INTESTINE, FlavorName.SEAFOOD);
        assertThat(v0nationRepository.count()).isEqualTo(2);
        assertThat(v0nationRepository.findAll()).extracting("nationName")
                .containsOnly(NationName.KOREAN, NationName.EXOTIC);

        System.out.println("NON FETCH");
        entityManager.flush();
        entityManager.clear();
        List<V0Member> alls = v0MemberRepository.findAll();
        for (V0Member all : alls) {
            all.getExcludes().forEach(System.out::println);
            all.getNations().forEach(System.out::println);
        }

        System.out.println("FETCH JOIN --- V0Nation, V0Exclude 까지 한번에 조회해야 함.");
        entityManager.flush();
        entityManager.clear();
        List<V0Member> members = v0MemberRepository.findAllByGroup_id(pin);
        for (V0Member member : members) {
            member.getExcludes().forEach(System.out::println);
            member.getNations().forEach(System.out::println);
        }
        assertThat(members.size()).isEqualTo(2);
    }

    @Test @DisplayName("그룹 제거")
    void deleteGroup() {
        int pin = service.createGroup("host");
        System.out.println("pin = " + pin);
        assertThat(v0GroupRepository.count()).isEqualTo(1);
        assertThat(v0MemberRepository.count()).isEqualTo(1);

        ResultSubmission hello = makeDTO("hello", pin,Set.of(NationName.KOREAN, NationName.EXOTIC), Set.of(FlavorName.INTESTINE));
        ResultSubmission bye = ResultSubmission.builder()
                .pinNumber(Integer.toString(pin))
                .token("bye")
                .gameAnswer(GameAnswer.builder().build())
                .build();

        service.saveResult(hello);
        service.saveResult(bye);

        assertThat(v0MemberRepository.count()).isEqualTo(3);
        assertThat(v0excludeRepository.count()).isEqualTo(1);
        assertThat(v0nationRepository.count()).isEqualTo(2);

        service.deleteGroup(pin);
        assertThat(v0GroupRepository.count()).isEqualTo(0);
        assertThat(v0MemberRepository.count()).isEqualTo(0);
        assertThat(v0excludeRepository.count()).isEqualTo(0);
        assertThat(v0nationRepository.count()).isEqualTo(0);
    }

    @Test @DisplayName("멤버 미니게임 결과 무효")
    void makeInvalid() {
        int pinNum = service.createGroup("host");

        ResultSubmission resultSubmission = makeDTO("member0",
                pinNum,Set.of(NationName.KOREAN, NationName.EXOTIC),
                Set.of(FlavorName.INTESTINE));

        V0Group group = service.saveResult(resultSubmission);

        assertThat(group.getMembers().size()).isEqualTo(2);
        assertThat(v0GroupRepository.count()).isEqualTo(1);
        assertThat(v0MemberRepository.count()).isEqualTo(2);
        assertThat(v0excludeRepository.count()).isEqualTo(1);
        assertThat(v0nationRepository.count()).isEqualTo(2);
        assertThat(service.countCompleteMember(pinNum)).isEqualTo(1);
        assertThat(service.countMember(pinNum)).isEqualTo(2);

        service.makeMemberInvalid("member0", pinNum);
        assertThat(v0MemberRepository.count()).isEqualTo(2);
        assertThat(v0excludeRepository.count()).isEqualTo(0);
        assertThat(v0nationRepository.count()).isEqualTo(0);
        assertThat(service.countCompleteMember(pinNum)).isEqualTo(0);
        assertThat(service.countMember(pinNum)).isEqualTo(2);
    }

    @Test @DisplayName("호스트 결과 제출")
    void hostSubmitResult() {
        int pinNum = service.createGroup("host");

        assertThat(v0GroupRepository.count()).isEqualTo(1);
        assertThat(v0MemberRepository.count()).isEqualTo(1);
        assertThat(service.countMember(pinNum)).isEqualTo(1);
        assertThat(service.countCompleteMember(pinNum)).isEqualTo(0);

        ResultSubmission resultSubmission = makeDTO("host",
                pinNum,Set.of(NationName.KOREAN, NationName.EXOTIC),
                Set.of(FlavorName.INTESTINE));
        V0Group group = service.saveResult(resultSubmission);

        assertThat(v0MemberRepository.count()).isEqualTo(1);
        assertThat(service.countMember(pinNum)).isEqualTo(1);
        assertThat(service.countCompleteMember(pinNum)).isEqualTo(1);
        assertThat(group.getHost().getToken()).isEqualTo("host");
        assertThat(group.getHost().getComplete()).isTrue();
        assertThat(group.getMembers().size()).isEqualTo(1);
        assertThat(group.getIsOVer()).isFalse();
    }

    @Test @DisplayName("그룹-멤버 검색")
    void findMember() {
        int pinNum = service.createGroup("host");

        ResultSubmission resultSubmission = makeDTO("find", pinNum,Set.of(NationName.KOREAN, NationName.EXOTIC), Set.of(FlavorName.INTESTINE));
        V0Group group = service.saveResult(resultSubmission);
        group = v0GroupRepository.findById(group.getId())
                .orElseThrow(RuntimeException::new);
        V0Member host = group.getHost();
        V0Member member = group.getMembers().get(1);

        assertThat(host).isEqualTo(group.getMembers().get(0));
        assertThat(host).isEqualTo(v0MemberRepository.findByTokenAndGroup("host", group).get());

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

        List<V0Member> members = v0MemberRepository.findAllByGroup_id(group.getId());
        assertThat(members.size()).isEqualTo(2);
    }

    @Test @DisplayName("멤버 제거")
    void deleteMember() {
        int pinNum = service.createGroup("host");

        ResultSubmission dto = makeDTO("hello", pinNum,Set.of(NationName.KOREAN, NationName.EXOTIC), Set.of(FlavorName.INTESTINE));
        V0Group group = service.saveResult(dto);
        V0Member host = group.getHost();
        V0Member member = group.getMembers().get(1);

        assertThat(host).isNotNull();
        assertThat(host.getComplete()).isFalse();

        assertThat(v0MemberRepository.count()).isEqualTo(2);
        assertThat(group.getMembers().size()).isEqualTo(2);
        assertThat(v0excludeRepository.count()).isNotEqualTo(0);
        assertThat(v0nationRepository.count()).isNotEqualTo(0);

        System.out.println("DELETE!!!");
        service.deleteMember(member.getToken(), group.getId());
        group = v0GroupRepository.findById(group.getId())
                .orElseThrow(RuntimeException::new);

        assertThat(group.getMembers().size()).isEqualTo(1);
        assertThat(v0MemberRepository.count()).isEqualTo(1);
        assertThat(v0excludeRepository.count()).isEqualTo(0);
        assertThat(v0nationRepository.count()).isEqualTo(0);
    }

    private ResultSubmission makeDTO(String token, int pin, Set<NationName> nationNames, Set<FlavorName> flavorNames) {
        GameAnswer gameAnswer = GameAnswer.builder()
                .alcohol(true)
                .greasy(true)
                .noodle(true)
                .rice(false)
                .health(null)
                .build();
        gameAnswer.getNation().addAll(nationNames);
        ResultSubmission resultSubmission = ResultSubmission.builder()
                .gameAnswer(gameAnswer)
                .pinNumber(Integer.toString(pin))
                .token(token)
                .build();
        resultSubmission.getDislikedFoods().addAll(flavorNames);
        return resultSubmission;
    }
}