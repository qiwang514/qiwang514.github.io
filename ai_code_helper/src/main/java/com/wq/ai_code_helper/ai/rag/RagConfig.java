package com.wq.ai_code_helper.ai.rag;


import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
    加载RAG
*/
@Configuration
public class RagConfig {

    @Resource
    private EmbeddingModel qwenEmbeddingModel;

//    向量数据库的存储接口
    @Resource
    private EmbeddingStore<TextSegment> embeddingStore;

    @Bean
    public ContentRetriever contentRetriever() {
        //------RAG------
        //1 加载文档
        List<Document> documents = FileSystemDocumentLoader.loadDocuments("src/main/resources/");
        //2 切割文档 每个文档按照段落分割 最大1000个字符 每次最多重叠200个
        //使用段落文档切割器
        DocumentByParagraphSplitter documentByParagraphSplitter = new DocumentByParagraphSplitter(1000, 200);
        //3 自定义文档加载器 把文档转化为向量 并保存到向量数据库中
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(documentByParagraphSplitter)

                //为了提高文档质量 切割后的文档碎片 添加文档名称作为元信息
                .textSegmentTransformer(textSegment ->
                        TextSegment.from(textSegment.metadata().getString("file_name") + "\n" +
                                textSegment.text(), textSegment.metadata()))
                /*
                 *TextSegment.from(新文本, 原元数据)
                 * 元数据就是「描述数据的数据」—— 它不包含核心内容本身，而是用来标注核心内容的「附加信息」
                 * 比如file_name=xxx  file_path=/xxx/xxx/ 等 以键值对形式存在
                 * 用「新文本 + 原元数据」创建一个新的 TextSegment，替换原来的片段
                 */

                .embeddingModel(qwenEmbeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        //加载文档
        ingestor.ingest(documents);
        //4 自定义内容加载器 内容检索器：负责「动态检索」，每次用户提问都要执行，从向量库找相关内容；
        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(qwenEmbeddingModel)
                .maxResults(5)
                .minScore(0.75)//余弦相似度
                .build();
        return contentRetriever;
    }
}
