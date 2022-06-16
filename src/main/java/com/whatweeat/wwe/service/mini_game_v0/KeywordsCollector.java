package com.whatweeat.wwe.service.mini_game_v0;

import com.whatweeat.wwe.dto.MenuPoint;
import com.whatweeat.wwe.entity.MiniGameV0;
import com.whatweeat.wwe.entity.enums.ExpenseName;
import com.whatweeat.wwe.entity.enums.FlavorName;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Set;

@Component
public class KeywordsCollector {
    public ArrayList<MenuPoint.Keyword> getKeywords(MiniGameV0 menuV0, double hangoverPoint, double greasyPoint, double healthPoint, double alcoholPoint, double instantPoint, double spicyPoint, double richPoint, double ricePoint, double noodlePoint, double soupPoint, Set<FlavorName> flavorNames) {
        ArrayList<MenuPoint.Keyword> keywords = new ArrayList<>();
        if(flavorNames.contains(FlavorName.HOT)|| flavorNames.contains(FlavorName.COOL))
            keywords.add(new MenuPoint.Keyword(hangoverPoint, "해장"));
        else keywords.add(new MenuPoint.Keyword(hangoverPoint, "해장 X"));
        if(flavorNames.contains(FlavorName.GREASY))
            keywords.add(new MenuPoint.Keyword(greasyPoint, "기름칠"));
        else keywords.add(new MenuPoint.Keyword(greasyPoint, "담백"));
        if(menuV0.getHealthy()!=null&& menuV0.getHealthy())
            keywords.add(new MenuPoint.Keyword(healthPoint, "건강식"));
        else keywords.add(new MenuPoint.Keyword(healthPoint, "맛 있으면 0 칼로리"));
        if(menuV0.getAlcohol()!=null&& menuV0.getAlcohol())
            keywords.add(new MenuPoint.Keyword(alcoholPoint, "안주"));
        else keywords.add(new MenuPoint.Keyword(alcoholPoint, "안주 X"));
        if(menuV0.getInstant()!=null&& menuV0.getInstant())
            keywords.add(new MenuPoint.Keyword(instantPoint, "간편"));
        else keywords.add(new MenuPoint.Keyword(instantPoint, "여유 있는"));
        if(flavorNames.contains(FlavorName.SPICY))
            keywords.add(new MenuPoint.Keyword(spicyPoint, "매콤"));
        else keywords.add(new MenuPoint.Keyword(spicyPoint, "매콤 X"));
        if(menuV0.getExpenseName()== ExpenseName.EXPENSIVE1|| menuV0.getExpenseName()==ExpenseName.EXPENSIVE2)
            keywords.add(new MenuPoint.Keyword(richPoint, "돈 걱정 없음"));
        else keywords.add(new MenuPoint.Keyword(ricePoint, "가성비"));
        if(menuV0.getRice())
            keywords.add(new MenuPoint.Keyword(ricePoint, "밥"));
        else keywords.add(new MenuPoint.Keyword(ricePoint, "밥 x"));
        if(menuV0.getNoodle())
            keywords.add(new MenuPoint.Keyword(noodlePoint, "면"));
        else keywords.add(new MenuPoint.Keyword(noodlePoint, "면 X"));
        if(menuV0.getSoup())
            keywords.add(new MenuPoint.Keyword(soupPoint, "국"));
        else keywords.add(new MenuPoint.Keyword(soupPoint, "국 X"));
        return keywords;
    }
}
