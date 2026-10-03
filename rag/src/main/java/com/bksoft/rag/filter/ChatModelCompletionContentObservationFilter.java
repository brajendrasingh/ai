package com.bksoft.rag.filter;

import io.micrometer.common.KeyValue;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.observation.ChatModelObservationContext;
import org.springframework.ai.content.Content;
import org.springframework.ai.observation.ObservabilityHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Component
public class ChatModelCompletionContentObservationFilter implements ObservationFilter {

    @Override
    public Observation.Context map(Observation.Context context) {
        if (!(context instanceof ChatModelObservationContext chatContext)) {
            return context;
        }
        var prompts = processPrompts(chatContext);
        var completions = processCompletion(chatContext);
        chatContext.addHighCardinalityKeyValue(new KeyValue() {
            @Override
            public String getKey() {
                return "gen_ai.prompt";
            }

            @Override
            public String getValue() {
                return ObservabilityHelper.concatenateStrings(prompts);
            }
        });

        chatContext.addHighCardinalityKeyValue(new KeyValue() {
            @Override
            public String getKey() {
                return "gen_ai.completion";
            }

            @Override
            public String getValue() {
                return ObservabilityHelper.concatenateStrings(completions);
            }
        });
        return chatContext;
    }

    private List<String> processPrompts(ChatModelObservationContext chatContext) {
        if (chatContext.getRequest() == null || CollectionUtils.isEmpty(chatContext.getRequest().getInstructions())) {
            return List.of();
        }
        return chatContext.getRequest().getInstructions().stream().map(Content::getText).toList();
    }

    private List<String> processCompletion(ChatModelObservationContext chatContext) {
        if (chatContext.getResponse() == null || CollectionUtils.isEmpty(chatContext.getResponse().getResults())) {
            return List.of();
        }
        return chatContext.getResponse().getResults().stream().filter(generation ->
                        generation.getOutput() != null && StringUtils.hasText(generation.getOutput().getText()))
                .map(generation -> generation.getOutput().getText()).toList();
    }
}