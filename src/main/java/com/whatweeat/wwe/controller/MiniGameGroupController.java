package com.whatweeat.wwe.controller;

import com.whatweeat.wwe.controller.request.ResultSubmission;
import com.whatweeat.wwe.controller.response.GroupAndMembersDTO;
import com.whatweeat.wwe.controller.response.MiniGameResultWait;
import com.whatweeat.wwe.controller.response.RecommendMenu;
import com.whatweeat.wwe.controller.response.RecommendResponse;
import com.whatweeat.wwe.entity.mini_game_v0.V0Group;
import com.whatweeat.wwe.entity.mini_game_v0.V0Member;
import com.whatweeat.wwe.service.MiniGameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor @Slf4j
@RequestMapping("/group")
public class MiniGameGroupController {
    private final MiniGameService miniGameV0ServiceImpl;

    @GetMapping("/all")
    public List<GroupAndMembersDTO> getAllGroups() {
        List<V0Group> result = miniGameV0ServiceImpl.getAllGroup();
        return result.stream()
                .map(g -> {
                    List<String> tokens = g.getMembers().stream()
                            .map(V0Member::getToken)
                            .collect(Collectors.toList());
                    GroupAndMembersDTO dto = new GroupAndMembersDTO(g.getId());
                    dto.getTokens().addAll(tokens);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<Integer> createGroup(
            @RequestParam String token) {
        log.info("CREATING GROUP...");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(miniGameV0ServiceImpl.createGroup(token));
    }

    @DeleteMapping("/{pin}")
    public ResponseEntity<Boolean> deleteGroup(
            @PathVariable String pin) {
        miniGameV0ServiceImpl.deleteGroup(Integer.parseInt(pin));
        log.info("DELETING GROUP :: {}", pin);
        return ResponseEntity.status(HttpStatus.OK)
                .body(true);
    }

    @GetMapping("/{pin}/host")
    public Boolean isHost(
            @PathVariable String pin,
            @RequestParam String token) {
        return miniGameV0ServiceImpl.findGroup(Integer.parseInt(pin))
                .getHost().getToken().equals(token);
    }

    @GetMapping("/{pin}")
    public Boolean validGroupPin(
            @PathVariable String pin) {
        log.debug("RECEIVED :: {}", LocalDateTime.now());
        return miniGameV0ServiceImpl.pinValidCheck(Integer.parseInt(pin));
    }

    @PutMapping("/{pin}")
    public ResponseEntity<Void> putMember(
            @PathVariable String pin,
            @RequestParam String token) {
        miniGameV0ServiceImpl.makeMemberInvalid(token, Integer.parseInt(pin));
        return ResponseEntity.ok(null);
    }

    @PostMapping("/{pin}")
    public ResponseEntity<Void> submitMember(
            @PathVariable String pin,
            @RequestBody ResultSubmission resultSubmission) {
        if(!pin.equals(resultSubmission.getPinNumber())){ log.error("HTTP URL PIN' AND 'HTTP BODY PIN' NOT MATCHED");}
        if(miniGameV0ServiceImpl.findGroup(Integer.parseInt(pin)).getIsOVer())
            return ResponseEntity.ok(null);
        miniGameV0ServiceImpl.saveResult(resultSubmission);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/solo")
    public ResponseEntity<RecommendResponse> submitAndGetSoloResult(
            @RequestBody ResultSubmission resultSubmission) {
        List<RecommendMenu> recommendMenus = miniGameV0ServiceImpl.getSoloResult(resultSubmission).stream()
                .limit(3)
                .map(menuPoint -> new RecommendMenu(menuPoint.getMenuName(), menuPoint.getMenuURL(), menuPoint.getKeywords()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new RecommendResponse(recommendMenus, 1));
    }

    @GetMapping("/{pin}/gameresult")
    public ResponseEntity<RecommendResponse> getGroupResult(
            @PathVariable String pin) {
        int count = miniGameV0ServiceImpl.countCompleteMember(Integer.parseInt(pin));
        List<RecommendMenu> recommendMenus = miniGameV0ServiceImpl.getGroupResult(Integer.parseInt(pin)).stream()
                .limit(3)
                .map(menuPoint -> new RecommendMenu(menuPoint.getMenuName(), menuPoint.getMenuURL(), menuPoint.getKeywords()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new RecommendResponse(recommendMenus, count));
    }

    @GetMapping("/{pin}/gameresultwait")
    public ResponseEntity<Object> countMembers(
            @PathVariable String pin,
            @RequestParam String token) {
        int count = miniGameV0ServiceImpl.countCompleteMember(Integer.parseInt(pin));
        V0Group group = miniGameV0ServiceImpl.findGroup(Integer.parseInt(pin));
        boolean isOVer = group.getIsOVer();
        boolean isHost = group.getHost().getToken().equals(token);

        return ResponseEntity.ok(new MiniGameResultWait(count, isOVer, isHost));
    }
}
