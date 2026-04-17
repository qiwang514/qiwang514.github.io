package com.wq.ai_code_helper.controller;


import com.wq.ai_code_helper.ai.AiCodeHelpterService;
import jakarta.annotation.Resource;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private AiCodeHelpterService aiCodeHelpterService;

    @GetMapping("/chat")
    public Flux<ServerSentEvent<String>> chat(int memoryId,String message) {
        return aiCodeHelpterService.chatStream(memoryId, message)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());

    }
}
