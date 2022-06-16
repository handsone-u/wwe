package com.whatweeat.wwe.repository.mini_game_v0;

import com.whatweeat.wwe.entity.mini_game_v0.V0Group;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface V0GroupRepository extends JpaRepository<V0Group, Integer> {
    @Query("SELECT MAX(id) FROM V0Group ")
    int getMaxId();

    @EntityGraph(attributePaths = {"members"})
    Optional<V0Group> findById(Integer pin);
}
