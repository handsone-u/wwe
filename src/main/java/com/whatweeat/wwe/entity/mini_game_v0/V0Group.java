package com.whatweeat.wwe.entity.mini_game_v0;

import com.whatweeat.wwe.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter @Setter
public class V0Group extends BaseEntity {
    @Id @Column(name = "GROUP_ID")
    private Integer id;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<V0Member> members = new ArrayList<>();

    public void addMember(V0Member member) {
        this.members.add(member);
        member.setGroup(this);
    }

    public void removeMember(V0Member member) {
        this.members.remove(member);
        member.setGroup(null);
    }

    public V0Group(Integer id) {
        this.id = id;
    }
}
