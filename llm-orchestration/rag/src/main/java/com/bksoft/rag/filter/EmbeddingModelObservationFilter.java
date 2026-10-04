package com.bksoft.rag.filter;

import io.micrometer.common.KeyValue;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationFilter;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationContext;
import org.springframework.ai.observation.ObservabilityHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class EmbeddingModelObservationFilter implements ObservationFilter {

    @Override
    public Observation.Context map(Observation.Context context) {
        if (!(context instanceof EmbeddingModelObservationContext embeddingContext)) {
            return context;
        }
        List<String> inputs = processInputs(embeddingContext);
        embeddingContext.addHighCardinalityKeyValue(new KeyValue() {
            @Override
            public String getKey() {
                return "gen_ai.prompt";
            }
            @Override
            public String getValue() {
                return ObservabilityHelper.concatenateStrings(inputs);
            }
        });
        return embeddingContext;
    }

    private List<String> processInputs(EmbeddingModelObservationContext context) {
        if (context.getRequest() == null || CollectionUtils.isEmpty(context.getRequest().getInstructions())) {
            return List.of();
        }
        return context.getRequest().getInstructions().stream().toList();
    }
}
