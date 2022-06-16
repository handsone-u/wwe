package com.whatweeat.wwe.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.whatweeat.wwe.dto.MenuCreateDTO;
import com.whatweeat.wwe.entity.Menu;
import com.whatweeat.wwe.entity.MiniGameV0;
import com.whatweeat.wwe.repository.FlavorRepository;
import com.whatweeat.wwe.repository.MenuRepository;
import com.whatweeat.wwe.repository.MiniGameV0Repository;
import com.whatweeat.wwe.repository.NationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;

import static com.whatweeat.wwe.dto.MenuCreateDTO.getDto;
import static com.whatweeat.wwe.entity.enums.FlavorName.*;
import static com.whatweeat.wwe.entity.enums.NationName.*;
import static org.assertj.core.api.Assertions.assertThat;

@Import(QDslTestConfig.class)
@DataJpaTest
@ActiveProfiles("test")
class MenuServiceImplTest {
    MenuService menuService;
    @Autowired MenuRepository menuRepository;
    @Autowired MiniGameV0Repository miniGameV0Repository;
    @Autowired FlavorRepository flavorRepository;
    @Autowired NationRepository nationRepository;

    @Autowired EntityManager entityManager;
    JPAQueryFactory jpaQueryFactory;

    @BeforeEach
    void beforeEach() {
        menuService = new MenuServiceImpl(menuRepository);
        jpaQueryFactory = new JPAQueryFactory(entityManager);

        assertThat(menuService.count()).isEqualTo(0L);
        MenuCreateDTO dto = MenuCreateDTO.builder()
                .menuName("찌개/전골류 (김치/순두부/부대찌개)")
                .menuImage("www~")
                .rice(true)
                .noodle(false)
                .soup(true)
                .healthy(null)
                .instant(false)
                .alcohol(false)
                .expenseName(null)
                .build();
        dto.getFlavorNames().addAll(Set.of(HOT, BLAND, SPICY));
        dto.getNationNames().add(KOREAN);

        menuService.save(dto);
    }

    @Test @DisplayName("메뉴 저장")
    void save() {
        assertThat(menuRepository.count()).isEqualTo(1);
        assertThat(miniGameV0Repository.count()).isEqualTo(1);
        assertThat(flavorRepository.count()).isEqualTo(3);
        assertThat(nationRepository.count()).isEqualTo(1);

        Menu menu = menuService.findByMenuName("찌개/전골류 (김치/순두부/부대찌개)");
        MiniGameV0 miniGameV0 = menu.getMiniGameV0();
        assertThat(miniGameV0).isNotNull();
        assertThat(miniGameV0.getRice()).isTrue();
        assertThat(miniGameV0.getNoodle()).isFalse();
        assertThat(miniGameV0.getSoup()).isTrue();
        assertThat(miniGameV0.getHealthy()).isNull();
        assertThat(miniGameV0.getInstant()).isFalse();
        assertThat(miniGameV0.getAlcohol()).isFalse();
        assertThat(miniGameV0.getExpenseName()).isNull();
        assertThat(miniGameV0.getFlavors()).extracting("flavorName")
                .containsOnly(HOT, BLAND, SPICY);
        assertThat(miniGameV0.getNations()).extracting("nationName")
                .containsExactly(KOREAN);
    }

    @Test @DisplayName("메뉴 전체 조회")
    void findAll() {
        MenuCreateDTO dto = MenuCreateDTO.builder()
                .menuName("짬뽕")
                .menuImage("www~")
                .rice(false)
                .noodle(true)
                .soup(true)
                .healthy(null)
                .instant(true)
                .alcohol(false)
                .expenseName(null)
                .build();
        dto.getFlavorNames().addAll(Set.of(SPICY, SEAFOOD, MEAT, HOT));
        dto.getNationNames().add(CHINESE);
        menuService.save(dto);

        assertThat(menuRepository.count()).isEqualTo(2);
        assertThat(miniGameV0Repository.count()).isEqualTo(2);
        assertThat(flavorRepository.count()).isEqualTo(7);
        assertThat(nationRepository.count()).isEqualTo(2);

        List<Menu> menus = menuService.findAll();
        assertThat(menuService.count()).isEqualTo(2);
        assertThat(menus.size()).isEqualTo(2);
        assertThat(menus).extracting("menuName")
                .containsOnly("찌개/전골류 (김치/순두부/부대찌개)", "짬뽕");
    }

    @Test @DisplayName("못 먹는 음식 제외 조회1")
    void findAllExceptFlavorNames1() {
        MenuCreateDTO dto = getDto("족발/보쌈", "hello", false, false, false, null,
                false, true, null,
                Set.of(MEAT, GREASY), Set.of(KOREAN));
        menuService.save(dto);
        dto = getDto("라멘", "hello", false, true, true, null,
                false, false, null,
                Set.of(SEAFOOD, MEAT, GREASY, BLAND), Set.of(JAPANESE));
        menuService.save(dto);

        assertThat(menuService.count()).isEqualTo(3);

        System.out.println("------1");
        List<Menu> result = menuService.findAllExceptFlavorNames(Set.of(MEAT));
        assertThat(result.size()).isEqualTo(1);
        assertThat(result).extracting("menuName").containsExactly("찌개/전골류 (김치/순두부/부대찌개)");

        System.out.println("------2");
        result = menuService.findAllExceptFlavorNames(Set.of(SEAFOOD, SPICY));
        assertThat(result.size()).isEqualTo(1);
        assertThat(result).extracting("menuName").containsExactly("족발/보쌈");
    }

    @Test @DisplayName("못 먹는 음식 제외 조회2")
    void findAllExceptFlavorNames2() {
        MenuCreateDTO dto =
                getDto("족발/보쌈", "hello",false, false, false, null,
                false, true, null,
                Set.of(MEAT, GREASY), Set.of(KOREAN));
        menuService.save(dto);
        dto = getDto("라멘", "hello", false, true, true, null,
                false, false, null,
                Set.of(SEAFOOD, MEAT, GREASY, BLAND), Set.of(JAPANESE));
        menuService.save(dto);
        dto = getDto("비빔밥", "hello", true, false, false, true,
                false, false, null,
                Set.of(BLAND), Set.of(KOREAN));
        menuService.save(dto);
        dto = getDto("삼계탕", "hello", true, false, true, true,
                false, false, null,
                Set.of(MEAT), Set.of(KOREAN));
        menuService.save(dto);

        assertThat(menuService.count()).isEqualTo(5);

        List<Menu> result = menuService.findAllExceptFlavorNames(Set.of(MEAT, SEAFOOD));
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).extracting("menuName").containsOnly("찌개/전골류 (김치/순두부/부대찌개)", "비빔밥");
    }
}