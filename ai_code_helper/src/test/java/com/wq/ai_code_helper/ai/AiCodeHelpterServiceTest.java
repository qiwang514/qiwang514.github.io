package com.wq.ai_code_helper.ai;

import dev.langchain4j.service.Result;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiCodeHelpterServiceTest {

    @Resource
    private AiCodeHelpterService aiCodeHelpterService;

    @Test
    void chat() {
        String result = aiCodeHelpterService.chat("你好 我是王琦");
        System.out.println(result);
        result = aiCodeHelpterService.chat("你知道我是谁吗");
        System.out.println(result);
    }


    @Test
    void chatWithMemory() {
        String result = aiCodeHelpterService.chat("你好，我是程序员王琦");
        System.out.println(result);
        result = aiCodeHelpterService.chat("你好，我是谁来着？");
        System.out.println(result);
    }

    @Test
    void chatForReport() {//结构化输出
        String userMessage = "你好 我是程序员王琦 学编程一年 请帮我制定学习报告";
        AiCodeHelpterService.Report report = aiCodeHelpterService.chatForReport(userMessage);
        System.out.println(report);
    }

    @Test
    void chatWithRAG() {
        Result<String> result = aiCodeHelpterService.chatWithRag("你好，我是程序员王琦 怎么学习java？有哪些常见的面试题");
        System.out.println(result.sources());
        System.out.println(result.content());

    }

    @Test
    void chatWithMCP() {
        String result = aiCodeHelpterService.chat("什么是编程导航？");
        System.out.println(result);

    }

    @Test
    void chatWithGuardRail() {
        String result = aiCodeHelpterService.chat(" 什么是java");
        System.out.println(result);

    }
}