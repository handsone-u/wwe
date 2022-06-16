package com.whatweeat.wwe.controller;

import com.whatweeat.wwe.controller.request.ResultSubmission;
import com.whatweeat.wwe.controller.response.GroupAndMembersDTO;
import com.whatweeat.wwe.dto.MenuPoint;
import com.whatweeat.wwe.entity.mini_game_v0.V0Group;
import com.whatweeat.wwe.entity.mini_game_v0.V0Member;
import com.whatweeat.wwe.service.MiniGameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public Integer createGroup() {
        log.info("CREATING GROUP...");
        return miniGameV0ServiceImpl.createGroup();
    }

    @DeleteMapping("/{pin}")
    public Boolean deleteGroup(
            @PathVariable String pin) {
        miniGameV0ServiceImpl.deleteGroup(Integer.parseInt(pin));
        log.info("DELETING GROUP :: {}", pin);
        return true;
    }

    @GetMapping("/{pin}")
    public Boolean validGroupPin(
            @PathVariable String pin) {
        log.debug("RECEIVED :: {}", LocalDateTime.now());
        return miniGameV0ServiceImpl.pinValidCheck(Integer.parseInt(pin));
    }

    @PostMapping("/{pin}")
    public ResponseEntity<Object> submitMember(
            @PathVariable String pin,
            @RequestBody ResultSubmission resultSubmission) {
        if(!pin.equals(resultSubmission.getPinNumber())){ log.error("'HTTP URL PIN' AND 'HTTP BODY PIN' NOT MATCHED");}
        return ResponseEntity.ok(miniGameV0ServiceImpl.saveResult(resultSubmission).getId());
    }

    @PostMapping("/solo")
    public ResponseEntity<List<MenuPoint>> submitAndGetSoloResult(
            @RequestBody ResultSubmission resultSubmission) {
        return ResponseEntity.ok(miniGameV0ServiceImpl.getSoloResult(resultSubmission).stream()
                .limit(3)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{pin}/gameresult")
    public ResponseEntity<List<MenuPoint>> getGroupResult(
            @PathVariable String pin) {
        return ResponseEntity.ok(miniGameV0ServiceImpl.getGroupResult(Integer.parseInt(pin)).stream()
                .limit(3)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{pin}/player-count")
    public Integer countMembers(
            @PathVariable String pin) {
        return miniGameV0ServiceImpl.countCompleteMember(Integer.parseInt(pin));
    }
}
