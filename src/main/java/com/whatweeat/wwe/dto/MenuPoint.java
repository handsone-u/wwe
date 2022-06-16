package com.whatweeat.wwe.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class MenuPoint implements Comparable<MenuPoint>{
    private String menuName;
    private String menuURL;
    private Double point;
    private List<String> keywords = new ArrayList<>();

    public MenuPoint(String menuName, String menuURL, Double point) {
        this.menuName = menuName;
        this.menuURL = menuURL;
        this.point = point;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords.addAll(
                keywords.stream()
                        .sorted(Comparator.reverseOrder())
                        .filter(keyword -> keyword.getFrequency() > 0)
                        .limit(3)
                        .map(Keyword::getKeyword)
                        .collect(Collectors.toList()));
    }

    @Override
    public int compareTo(MenuPoint o) {
        return Double.compare(this.point, o.point);
    }

    @Data
    static public class Keyword implements Comparable<Keyword> {
        private Double frequency;
        private String keyword;

        public Keyword(Double frequency, String keyword) {
            this.frequency = frequency;
            this.keyword = keyword;
        }

        @Override
        public int compareTo(Keyword o) {
            return Double.compare(this.frequency, o.frequency);
        }
    }
}
