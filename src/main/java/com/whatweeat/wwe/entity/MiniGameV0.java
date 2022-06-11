package com.whatweeat.wwe.entity;

import com.whatweeat.wwe.entity.enums.ExpenseName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) @Getter @Setter
public class MiniGameV0 {
    @Id @GeneratedValue
    @Column(name = "MINI_V0_ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "miniGameV0")
    private Menu menu;

    private Boolean rice;
    private Boolean noodle;
    private Boolean soup;
    private Boolean healthy;
    private Boolean instant;
    private Boolean alcohol;

    @Enumerated(EnumType.STRING)
    @Column(name = "EXPENSE")
    private ExpenseName expenseName;

    @OneToMany(mappedBy = "miniGameV0", cascade = CascadeType.ALL)
    private final Set<Flavor> flavors = new HashSet<>();

    @OneToMany(mappedBy = "miniGameV0", cascade = CascadeType.ALL)
    private final Set<Nation> nations = new HashSet<>();

    public MiniGameV0(Menu menu, Boolean rice, Boolean noodle, Boolean soup, Boolean healthy, Boolean instant, Boolean alcohol, ExpenseName expenseName) {
        this.menu = menu;
        this.rice = rice;
        this.noodle = noodle;
        this.soup = soup;
        this.healthy = healthy;
        this.instant = instant;
        this.alcohol = alcohol;
        this.expenseName = expenseName;
        this.menu.setMiniGameV0(this);
    }
}
