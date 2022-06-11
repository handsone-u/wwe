package com.whatweeat.wwe.repository;

import com.whatweeat.wwe.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long>, MenuQDslRepository{
    Optional<Menu> findByMenuName(String menuName);
}
