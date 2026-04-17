package com.wq.ai_code_helper.ai;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AiCodeHelper {

    @Resource
    private ChatModel qwenchatModel;

    private static final String SYSTEM_MESSAGE = """
        你是编程领域的小助手，帮助用户解答编程学习和求职面试相关的问题，并给出建议。重点关注 4 个方向：
        1. 规划清晰的编程学习路线
        2. 提供项目学习建议
        3. 给出程序员求职全流程指南（比如简历优化、投递技巧）
        4. 分享高频面试题和面试技巧
        请用简洁易懂的语言回答，助力用户高效学习与求职。
        """;


    public String chat(String message) {

        SystemMessage systemMessage = SystemMessage.from(SYSTEM_MESSAGE);

/*      为什么qwenchatModel.chat方法后面的参数不直接传入message？
        因为 qwenchatModel.chat() 是通义千问（或对应 LLM 框架）封装的接口，
        它要求传入标准化的消息对象（UserMessage），而不是原始字符串。
        UserMessage 封装了角色（user）、内容、元数据等信息，
        告诉模型输入的时候 必须按照规范
        框架需要用这个对象来构建符合 LLM API 规范的请求格式（比如 OpenAI 风格的 messages 数组）
*/
        UserMessage userMessage = UserMessage.from(message);
        ChatResponse chatResponse = qwenchatModel.chat(systemMessage,userMessage);


/*      ChatResponse 是完整响应容器，包含了 LLM 返回的所有信息：
        它不仅有 AI 回复内容（AiMessage），还可能包含 token 用量、模型版本、错误信息、元数据等。
        .aiMessage() 是提取核心回复内容的方法，专门用来获取助手生成的消息对象，避免你直接操作整个响应结构。
*/
        AiMessage aiMessage = chatResponse.aiMessage();
        log.info("AI输出："+ aiMessage.toString());

/*        .text() 是专门提取纯文本回复内容的方法，
            把结构化的 AiMessage 里的核心文本剥离出来，
*/
        return aiMessage.text();
    }


    public String chatWithMessage(UserMessage userMessage) {
        ChatResponse chatResponse = qwenchatModel.chat(userMessage);
        AiMessage aiMessage = chatResponse.aiMessage();
        log.info("AI 输出：" + aiMessage.toString());
        return aiMessage.text();
    }

}
