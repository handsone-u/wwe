package com.whatweeat.wwe.service.mini_game_v0;

import com.whatweeat.wwe.controller.request.GameAnswer;
import com.whatweeat.wwe.controller.request.ResultSubmission;
import com.whatweeat.wwe.dto.MenuCreateDTO;
import com.whatweeat.wwe.dto.MenuPoint;
import com.whatweeat.wwe.entity.Menu;
import com.whatweeat.wwe.entity.mini_game_v0.V0Group;
import com.whatweeat.wwe.repository.MenuRepository;
import com.whatweeat.wwe.repository.mini_game_v0.V0ExcludeRepository;
import com.whatweeat.wwe.repository.mini_game_v0.V0GroupRepository;
import com.whatweeat.wwe.repository.mini_game_v0.V0MemberRepository;
import com.whatweeat.wwe.repository.mini_game_v0.V0NationRepository;
import com.whatweeat.wwe.service.MenuService;
import com.whatweeat.wwe.service.MenuServiceImpl;
import com.whatweeat.wwe.service.MiniGameService;
import com.whatweeat.wwe.service.QDslTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.whatweeat.wwe.entity.enums.FlavorName.*;
import static com.whatweeat.wwe.entity.enums.NationName.JAPANESE;
import static com.whatweeat.wwe.entity.enums.NationName.KOREAN;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@Import(QDslTestConfig.class)
class MiniGameV0ServiceTest {
    MiniGameService miniGameService;
    MenuService menuService; // MenuService 구현체가 정상동작 보장해야 함
    MenuCalculator menuCalculatorV0;

    @Autowired MenuRepository menuRepository;
    @Autowired V0GroupRepository v0GroupRepository;
    @Autowired V0MemberRepository v0MemberRepository;
    @Autowired V0NationRepository v0NationRepository;
    @Autowired V0ExcludeRepository v0ExcludeRepository;

    @BeforeEach
    void init() {
        menuCalculatorV0 = new MenuCalculatorV0(new KeywordsCollector());
        menuService = new MenuServiceImpl(menuRepository);
        miniGameService = new MiniGameV0ServiceImpl(menuService, v0GroupRepository, v0MemberRepository, menuCalculatorV0);
    }

    @Test @DisplayName("솔로 미니게임 제출&결과")
    void soloResult() {
        GameAnswer gameAnswer = new GameAnswer(true, true, true, true, true, true, true, true, true, true);
        gameAnswer.getNation().addAll(Set.of(KOREAN, JAPANESE));
        ResultSubmission resultSubmission = new ResultSubmission(gameAnswer,
                "", "token");
        resultSubmission.getDislikedFoods().addAll(Set.of(INTESTINE, MEAT));

        List<MenuPoint> soloResult = miniGameService.getSoloResult(resultSubmission);
        assertThat(v0MemberRepository.count()).isEqualTo(0);
        assertThat(v0NationRepository.count()).isEqualTo(0);
        assertThat(v0ExcludeRepository.count()).isEqualTo(0);
    }

    @Test @DisplayName("그룹 메뉴-미니게임 결과")
    void groupResult() {
        int pinNum = miniGameService.createGroup("host");
        List<MenuCreateDTO> menuDTOs = menuDTOs();
        Menu menu1 = menuService.save(menuDTOs.get(0));
        Menu menu2 = menuService.save(menuDTOs.get(1));

        List<GameAnswer> answers = answers();
        ResultSubmission user1 = ResultSubmission.builder()
                .gameAnswer(answers.get(0))
                .pinNumber(Integer.toString(pinNum))
                .token("user1")
                .build();
        V0Group group = miniGameService.saveResult(user1);

        System.out.println("라멘");
        assertThat(menuCalculatorV0.calculate(menu1.getMiniGameV0(), group.getMembers()).getPoint())
                .isEqualTo(4);
        System.out.println("냉면");
        assertThat(menuCalculatorV0.calculate(menu2.getMiniGameV0(), group.getMembers()).getPoint())
                .isEqualTo(6);

        miniGameService.getGroupResult(pinNum);
        group = miniGameService.findGroup(pinNum);

        assertThat(group.getIsOVer()).isTrue();
    }

    private List<MenuCreateDTO> menuDTOs() {
        ArrayList<MenuCreateDTO> menuDTOS = new ArrayList<>();
        MenuCreateDTO menu1 = MenuCreateDTO.builder()
                .menuName("라멘")
                .soup(true)
                .noodle(true)
                .rice(false)
                .instant(false)
                .alcohol(false)
                .build();
        menu1.getFlavorNames().addAll(Set.of(SEAFOOD, MEAT, GREASY, BLAND));
        menu1.getNationNames().add(JAPANESE);
        menuDTOS.add(menu1);

        MenuCreateDTO menu2 = MenuCreateDTO.builder()
                .menuName("냉면")
                .soup(true)
                .noodle(true)
                .rice(false)
                .healthy(true)
                .instant(false)
                .alcohol(false)
                .build();
        menu2.getFlavorNames().addAll(Set.of(MEAT, BLAND));
        menu2.getNationNames().add(KOREAN);
        menuDTOS.add(menu2);

        return menuDTOS;
    }

    private List<GameAnswer> answers() {
        ArrayList<GameAnswer> result = new ArrayList<>();
        GameAnswer a1 = GameAnswer.builder()
                .rice(false)
                .noodle(true)
                .soup(true)
                .hangover(false)
                .greasy(false)
                .health(null)
                .alcohol(false)
                .instant(true)
                .spicy(false)
                .rich(false)
                .build();
        a1.getNation().add(KOREAN);
        result.add(a1);

        return result;
    }
}