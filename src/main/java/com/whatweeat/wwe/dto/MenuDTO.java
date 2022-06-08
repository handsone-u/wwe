package com.whatweeat.wwe.dto;

import com.whatweeat.wwe.entity.enums.ExpenseName;
import com.whatweeat.wwe.entity.enums.FlavorName;
import com.whatweeat.wwe.entity.enums.NationName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@SuperBuilder
public class MenuDTO {
    protected Boolean rice;
    protected Boolean noodle;
    protected Boolean soup;
    protected Boolean healthy;
    protected Boolean instant;
    protected Boolean alcohol;
    protected ExpenseName expenseName;
    protected final Set<FlavorName> flavorNames = new HashSet<>();
    protected final Set<FlavorName> excludeNames = new HashSet<>();
    protected final Set<NationName> nationNames = new HashSet<>();

    protected String expenseValue;
    protected String flavorValues;
    protected String excludeValues;
    protected String nationValues;

    public MenuDTO(Boolean rice, Boolean noodle, Boolean soup, Boolean healthy, Boolean instant, Boolean alcohol) {
        this.rice = rice;
        this.noodle = noodle;
        this.soup = soup;
        this.healthy = healthy;
        this.instant = instant;
        this.alcohol = alcohol;
    }

    public void lookup() {
        expenseName = ExpenseName.lookup(expenseValue);
        String[] flavorSplit = flavorValues.split(", ");
        String[] excludeSplit = excludeValues.split(", ");
        String[] nationSplit = nationValues.split(", ");
        for (String s : flavorSplit) {
            if(s.isEmpty()) continue;
            flavorNames.add(FlavorName.lookup(s));
        }
        for (String s : excludeSplit) {
            if(s.isEmpty()) continue;
            excludeNames.add(FlavorName.lookup(s));
        }
        for (String s : nationSplit) {
            if(s.isEmpty()) continue;
            nationNames.add(NationName.lookup(s));
        }
    }

    public void collectionToString() {
        expenseValue = expenseName.getDesc();
        flavorValues = flavorNames.stream()
                .map(FlavorName::getDesc)
                .collect(Collectors.joining());
        excludeValues = excludeNames.stream()
                .map(FlavorName::getDesc)
                .collect(Collectors.joining());
        nationValues = nationNames.stream()
                .map(NationName::getDesc)
                .collect(Collectors.joining());
    }
}
