package com.wq.ai_code_helper.ai;

import com.wq.ai_code_helper.ai.guardrail.SafeInputGuardRail;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.guardrail.InputGuardrails;
import dev.langchain4j.service.spring.AiService;
import reactor.core.publisher.Flux;

import java.util.List;

//@AiService
@InputGuardrails({SafeInputGuardRail.class})
public interface AiCodeHelpterService {

    @SystemMessage(fromResource = "system-prompt.txt")
//    String chat(@MemoryId int memoryId, String userMessage);
    String chat(String userMessage);

    @SystemMessage(fromResource = "system-prompt.txt")
    Report chatForReport(String userMessage);


    /*
    record 是 Java 16 引入的不可变数据类语法糖， record是类 Report 是类名
    专门用来存数据，自动生成 equals()、hashCode()、toString() 以及构造器和访问器。
    LangChain4j 会自动把 AI 的 JSON 输出解析成这个 Report 对象。
    record 的设计目标就是只存数据，不写业务逻辑
     */
    record Report(String name, List<String> suggestionList ){};


    //返回封装后的结果
    @SystemMessage(fromResource = "system-prompt.txt")
    Result<String> chatWithRag(String userMessage);

    //流式输出
    @SystemMessage(fromResource = "system-prompt.txt")
    Flux<String> chatStream(@MemoryId int memoryId,@UserMessage String message);


}
