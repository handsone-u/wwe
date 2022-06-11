package com.whatweeat.wwe.controller.response;

import com.whatweeat.wwe.entity.Menu;
import com.whatweeat.wwe.entity.enums.ExpenseName;
import com.whatweeat.wwe.entity.enums.FlavorName;
import com.whatweeat.wwe.entity.enums.NationName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuInfo {
    private Long menuId;
    private String menuName;
    private String menuURL;
    private Integer frequency;

    private Long miniGameV0Id;
    private Boolean rice;
    private Boolean noodle;
    private Boolean soup;
    private Boolean healthy;
    private Boolean instant;
    private Boolean alcohol;
    private ExpenseName expenseName;

    private final Set<FlavorName> flavors = new HashSet<>();
    private final Set<NationName> nations = new HashSet<>();

    static public MenuInfo ofMenuInfo(Menu menu) {
        MenuInfo result = MenuInfo.builder()
                .menuId(menu.getId())
                .menuName(menu.getMenuName())
                .menuURL(menu.getMenuImage())
                .frequency(menu.getFrequency())
                .miniGameV0Id(menu.getMiniGameV0().getId())
                .rice(menu.getMiniGameV0().getRice())
                .noodle(menu.getMiniGameV0().getNoodle())
                .soup(menu.getMiniGameV0().getSoup())
                .healthy(menu.getMiniGameV0().getHealthy())
                .instant(menu.getMiniGameV0().getInstant())
                .alcohol(menu.getMiniGameV0().getAlcohol())
                .expenseName(menu.getMiniGameV0().getExpenseName())
                .build();

        menu.getMiniGameV0().getFlavors()
                .forEach(f -> result.flavors.add(f.getFlavorName()));
        menu.getMiniGameV0().getNations()
                .forEach(n -> result.nations.add(n.getNationName()));
        return result;
    }
}
