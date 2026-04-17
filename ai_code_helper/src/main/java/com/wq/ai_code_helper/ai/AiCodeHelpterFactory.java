package com.wq.ai_code_helper.ai;

import com.wq.ai_code_helper.ai.mcp.McpConfig;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class AiCodeHelpterFactory {

//    @Resource
//    private ChatModel wqqwenchatModel;

    @Resource(name = "myQwenChatModel")
    private ChatModel myQwenChatModel;

    @Resource
    private ContentRetriever contentRetriever;

    @Resource
    private McpToolProvider mcpToolProvider;

    @Resource
    private StreamingChatModel qwenStreamingChatModel;

    @Bean
    public AiCodeHelpterService aiCodeHelpterService() {
        //会话记忆
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        /*
        MessageWindowChatMemory.withMaxMessages(10) 是 LangChain4j 里的滑动窗口会话记忆：
        此外 也可以按照token数作为记忆标准
         */
        //创建实例
        AiCodeHelpterService aiCodeHelpterService = AiServices.builder(AiCodeHelpterService.class)
                .chatModel(myQwenChatModel)
                .streamingChatModel(qwenStreamingChatModel)
                .chatMemory(chatMemory) //会话记忆
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))//每个会话独立存储
                .contentRetriever(contentRetriever)//RAG检索增强生成
                .toolProvider(mcpToolProvider)//MCP工具调用
                .build();
        return aiCodeHelpterService;


    }

}
