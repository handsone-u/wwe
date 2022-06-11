package com.whatweeat.wwe.dto;

import lombok.Data;

@Data
public class MenuPoint implements Comparable<MenuPoint>{
    private String menuName;
    private String menuURL;
    private Double point;

    public MenuPoint(String menuName, String menuURL, Double point) {
        this.menuName = menuName;
        this.menuURL = menuURL;
        this.point = point;
    }

    @Override
    public int compareTo(MenuPoint o) {
        return Double.compare(this.point, o.point);
    }
}
