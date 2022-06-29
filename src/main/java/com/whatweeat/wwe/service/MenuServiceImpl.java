package com.whatweeat.wwe.service;

import com.whatweeat.wwe.dto.MenuCreateDTO;
import com.whatweeat.wwe.entity.Flavor;
import com.whatweeat.wwe.entity.Menu;
import com.whatweeat.wwe.entity.MiniGameV0;
import com.whatweeat.wwe.entity.Nation;
import com.whatweeat.wwe.entity.enums.FlavorName;
import com.whatweeat.wwe.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuServiceImpl implements MenuService {
    private final MenuRepository menuRepository;

    public Long count() {
        return menuRepository.count();
    }

    @Transactional(readOnly = false)
    public Menu save(MenuCreateDTO dto) {
        Menu newMenu = new Menu(dto.getMenuName(), dto.getMenuImage());
        MiniGameV0 newMiniGameV0 = new MiniGameV0(newMenu, dto.getRice(), dto.getNoodle(), dto.getSoup(),
                dto.getHealthy(), dto.getInstant(), dto.getAlcohol(), dto.getExpenseName());

        Set<Flavor> flavors = newMiniGameV0.getFlavors();
        Set<Nation> nations = newMiniGameV0.getNations();

        dto.getFlavorNames().forEach(name ->
                flavors.add(new Flavor(newMiniGameV0, name)));
        dto.getNationNames().forEach(name ->
                nations.add(new Nation(newMiniGameV0, name)));

        return menuRepository.save(newMenu);
    }

    public Menu findById(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException());
    }

    public Menu findByMenuName(String menuName) {
        return menuRepository.findByMenuName(menuName)
                .orElseThrow(() -> new RuntimeException());
    }

    public Menu findOneRandom() {
        int count = (int)menuRepository.count();
        Random random = new Random(LocalDateTime.now().getSecond());
        return menuRepository.findAll().get(random.nextInt(count));
    }

    public List<Menu> findAll() {
        return menuRepository.findAll();
    }

    public List<Menu> findAllExceptFlavorNames(Set<FlavorName> flavorNames) {
        return menuRepository.findAllFlavorNameNotIn(flavorNames);
    }
}
