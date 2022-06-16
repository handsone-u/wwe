package com.whatweeat.wwe.service.mini_game_v0;

import com.whatweeat.wwe.dto.MenuPoint;
import com.whatweeat.wwe.entity.Flavor;
import com.whatweeat.wwe.entity.MiniGameV0;
import com.whatweeat.wwe.entity.Nation;
import com.whatweeat.wwe.entity.enums.ExpenseName;
import com.whatweeat.wwe.entity.enums.FlavorName;
import com.whatweeat.wwe.entity.enums.NationName;
import com.whatweeat.wwe.entity.mini_game_v0.V0Member;
import com.whatweeat.wwe.entity.mini_game_v0.V0Nation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class MenuCalculatorV0 implements MenuCalculator{
    private final KeywordsCollector keywordsCollector;
    private final double POSITIVE = 1;
    private final double NEGATIVE = -1;

    public MenuPoint calculate(MiniGameV0 menuV0, List<V0Member> members) {
        /*
         * TODO: 국가별 키워드 갯수
         */
        double point = 0;
        double hangoverPoint = 0;
        double greasyPoint = 0;
        double healthPoint = 0;
        double alcoholPoint = 0;
        double instantPoint = 0;
        double spicyPoint = 0;
        double richPoint = 0;
        double ricePoint = 0;
        double noodlePoint = 0;
        double soupPoint = 0;
        double nationPoint = 0; // 국가 별 키워드?
        Set<FlavorName> flavorNames = menuV0.getFlavors().stream()
                .map(Flavor::getFlavorName)
                .collect(Collectors.toSet());
        for (V0Member member : members) {
            if(!member.getComplete()) continue;
            hangoverPoint += hangover(flavorNames, member);
            greasyPoint += greasy(flavorNames, member);
            healthPoint += health(menuV0, member);
            alcoholPoint += alcohol(menuV0, member);
            instantPoint += instant(menuV0, member);
            spicyPoint += spicy(menuV0, member);
            richPoint += rich(menuV0, member);
            ricePoint += rice(menuV0, member);
            noodlePoint += noodle(menuV0, member);
            soupPoint += soup(menuV0, member);
            nationPoint += nation(menuV0, member);
        }

        ArrayList<MenuPoint.Keyword> keywords = keywordsCollector.getKeywords(menuV0, hangoverPoint, greasyPoint, healthPoint, alcoholPoint, instantPoint, spicyPoint, richPoint, ricePoint, noodlePoint, soupPoint, flavorNames);

        point += hangoverPoint + greasyPoint + healthPoint + alcoholPoint + instantPoint + spicyPoint + richPoint +
                ricePoint + noodlePoint + soupPoint + nationPoint;
        MenuPoint result = new MenuPoint(menuV0.getMenu().getMenuName(), menuV0.getMenu().getMenuImage(), point);
        result.setKeywords(keywords);
        return result;
    }

    private double hangover(Set<FlavorName> flavorNames, V0Member member) {
        double point = 0;
        if(member.getHangover()==null) {
            log.trace("KEYWORD1 해장 상관없음...");
            return point;
        }
        if(flavorNames.containsAll(Set.of(FlavorName.COOL, FlavorName.HOT)))
            point += 2 * (member.getHangover() ? POSITIVE : NEGATIVE);
        else if(flavorNames.contains(FlavorName.COOL)||flavorNames.contains(FlavorName.HOT))
            point += member.getHangover() ? POSITIVE : NEGATIVE;
        log.trace("KEYWORD1 해장 점수:[{}]", point);
        return point;
    }

    private double greasy(Set<FlavorName> flavorNames, V0Member member) {
        double point = 0;
        if (member.getGreasy() == null) {
            log.trace("KEYWORD2 기름칠 상관없음...");
            return point;
        }
        if(flavorNames.contains(FlavorName.GREASY))
            point += member.getGreasy() ? POSITIVE : NEGATIVE;
        log.trace("KEYWORD2 기름칠 점수:[{}]", point);
        return point;
    }

    private double health(MiniGameV0 menu, V0Member member) {
        double point = 0;
        if (member.getHealth() == null) {
            log.trace("KEYWORD3 건강 상관없음...");
            return point;
        }
        point += menu.getHealthy() == member.getHealth() ? POSITIVE : NEGATIVE;
        log.trace("KEYWORD3 건강 점수:[{}]", point);
        return point;
    }

    private double alcohol(MiniGameV0 menu, V0Member member) {
        double point = 0;
        if (member.getAlcohol() == null) {
            log.trace("KEYWORD4 안주 상관없음...");
            return point;
        }
        point += menu.getAlcohol() == member.getAlcohol() ? POSITIVE : NEGATIVE;
        log.trace("KEYWORD4 안주 점수:[{}]", point);
        return point;
    }

    private double instant(MiniGameV0 menu, V0Member member) {
        double point = 0;
        if (member.getInstant() == null) {
            log.trace("KEYWORD5 간편 상관없음...");
            return point;
        }
        point += menu.getInstant() == member.getInstant() ? POSITIVE : NEGATIVE;
        log.trace("KEYWORD5 간편 점수:[{}]", point);
        return point;
    }

    private double spicy(MiniGameV0 menu, V0Member member) {
        double point = 0;
        if (member.getSpicy() == null) {
            log.trace("KEYWORD6 매콤 상관없음...");
            return point;
        }
        Set<FlavorName> flavorNames = menu.getFlavors().stream()
                .map(Flavor::getFlavorName)
                .collect(Collectors.toSet());
        if(flavorNames.contains(FlavorName.SPICY))
            point += member.getSpicy() ? POSITIVE : NEGATIVE;
        else
            point += member.getSpicy() ? NEGATIVE : POSITIVE;
        log.trace("KEYWORD6 매콤 점수:[{}]", point);
        return point;
    }

    private double rich(MiniGameV0 menu, V0Member member) {
        double point = 0;
        if (member.getRich() == null) {
            log.trace("KEYWORD7 돈 걱정 상관 없음...");
            return point;
        }
        if(menu.getExpenseName()==null)
            point += member.getRich() ? NEGATIVE : POSITIVE;
        else if(menu.getExpenseName().equals(ExpenseName.EXPENSIVE2))
            point += 2 * (member.getRich() ? POSITIVE : NEGATIVE);
        else if(menu.getExpenseName().equals(ExpenseName.EXPENSIVE1))
            point += member.getRich() ? POSITIVE : NEGATIVE;
        log.trace("KEYWORD7 돈 걱정 점수:[{}]", point);
        return point;
    }

    private double rice(MiniGameV0 menu, V0Member member) {
        double point = 0;
        if (member.getRice() == null) {
            log.trace("KEYWORD8_1 밥 상관없음...");
            return point;
        }
        point = menu.getRice() == member.getRice() ? POSITIVE : NEGATIVE;
        log.trace("KEYWORD8_1 밥 점수:[{}]", point);
        return point;
    }
    private double noodle(MiniGameV0 menu, V0Member member) {
        double point = 0;
        if (member.getNoodle() == null) {
            log.trace("KEYWORD8_2 면 상관없음...");
            return point;
        }
        point = menu.getNoodle() == member.getNoodle() ? POSITIVE : NEGATIVE;
        log.trace("KEYWORD8_2 면 점수:[{}]", point);
        return point;
    }
    private double soup(MiniGameV0 menu, V0Member member) {
        double point = 0;
        if (member.getSoup() == null) {
            log.trace("KEYWORD8_3 국 상관없음...");
            return point;
        }
        point = menu.getSoup() == member.getSoup() ? POSITIVE : NEGATIVE;
        log.trace("KEYWORD8_3 국 점수:[{}]", point);
        return point;
    }

    private double nation(MiniGameV0 menu, V0Member member) {
        double point = 0;
        if (member.getNations().isEmpty()) {
            log.trace("KEYWORD9 음식 종류 상관없음...");
            return point;
        }
        Set<NationName> menuSet = menu.getNations().stream()
                .map(Nation::getNationName)
                .collect(Collectors.toSet());
        Set<NationName> memberSet = member.getNations().stream()
                .map(V0Nation::getNationName)
                .collect(Collectors.toSet());
        menuSet.retainAll(memberSet);
        point += menuSet.size();
        log.trace("KEYWORD9 음식 종류 점수:[{}]", point);
        return point;
    }
}