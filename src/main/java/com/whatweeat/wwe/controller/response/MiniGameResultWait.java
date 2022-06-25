package com.whatweeat.wwe.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class MiniGameResultWait {
    Integer submissionCount;
    Boolean isGameClosed;
    Boolean isHost;
}
