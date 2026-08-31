package com.bksoft.rag.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public RagService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    public String ask(String query) {
        // 1. Search relevant documents from Milvus
        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder().query(query).topK(5).build());

        // 2. Extract document text and create context
        String context = documents.stream().map(Document::getText).collect(Collectors.joining("\n\n"));

        // 3. Create RAG prompt
        String prompt = """
                You are a helpful assistant.
                
                Answer the user's question using only the information
                provided in the context below.
                
                If the answer cannot be found in the context,
                say "I don't know based on the provided context."
                
                Context:
                %s
                
                User Question:
                %s
                """.formatted(context, query);

        // 4. Send prompt to Ollama LLM
        return chatClient.prompt().user(prompt).call().content();
    }
}
