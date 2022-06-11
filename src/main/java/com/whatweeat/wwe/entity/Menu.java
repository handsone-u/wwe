package com.whatweeat.wwe.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor @Getter @Setter
public class Menu {
    @Id
    @GeneratedValue
    @Column(name = "MENU_ID")
    private Long id;

    private String menuName;
    private String menuImage;
    private Integer frequency;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "MINI_V0_ID")
    private MiniGameV0 miniGameV0;

    public Menu(String menuName) {
        this(menuName, null, 0);
    }

    public Menu(String menuName, String menuImage) {
        this(menuName, menuImage, 0);
    }

    public Menu(String menuName, String menuImage, Integer frequency) {

        this.menuName = menuName;
        this.menuImage = menuImage;
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "id=" + id +
                ", menuName='" + menuName + '\'' +
                ", menuImage='" + menuImage + '\'' +
                ", frequency=" + frequency +
                '}';
    }
}
