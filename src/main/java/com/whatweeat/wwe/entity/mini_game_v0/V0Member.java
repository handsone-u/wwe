package com.whatweeat.wwe.entity.mini_game_v0;

import com.whatweeat.wwe.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor @Getter @Setter
public class V0Member extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    private String token;
    private Boolean complete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    private V0Group group;

    private Boolean rice;
    private Boolean noodle;
    private Boolean soup;
    private Boolean hangover;
    private Boolean greasy;
    private Boolean health;
    private Boolean alcohol;
    private Boolean instant;
    private Boolean spicy;
    private Boolean rich;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<V0Nation> nations = new HashSet<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<V0Exclude> excludes = new HashSet<>();

    /**
     * Create Host for new Group.
     * @param token : Host's token.
     */
    public V0Member(String token, Boolean complete) {
        this.token = token;
        this.complete = complete;
    }

    public V0Member saveGameResult(Boolean rice, Boolean noodle, Boolean soup, Boolean hangover,
                                   Boolean greasy, Boolean health, Boolean alcohol, Boolean instant,
                                   Boolean spicy, Boolean rich) {
        this.complete = true;
        this.rice = rice;
        this.noodle = noodle;
        this.soup = soup;
        this.hangover = hangover;
        this.greasy = greasy;
        this.health = health;
        this.alcohol = alcohol;
        this.instant = instant;
        this.spicy = spicy;
        this.rich = rich;
        return this;
    }

    public void makeInvalid() {
        this.complete = false;
        this.getNations().clear();
        this.getExcludes().clear();
    }

    public void addNation(V0Nation nation) {
        this.nations.add(nation);
        nation.setMember(this);
    }

    public void addExclude(V0Exclude exclude) {
        this.excludes.add(exclude);
        exclude.setMember(this);
    }

    public static V0Member createHost(String token) {
        return new V0Member(token, false);
    }
}
