package io.kinoplan.utils.zio.opentelemetry.provider;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.data.LinkData;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.sdk.trace.samplers.SamplingResult;
import io.opentelemetry.semconv.HttpAttributes;
import io.opentelemetry.semconv.UrlAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class TracerSampler implements Sampler {

    private final List<Pattern> ignoreNamePatterns;

    public TracerSampler(Set<String> ignoreNames) {
        List<Pattern> ignoreNamePatterns = new ArrayList<>();

        for (String ignoreName : ignoreNames) {
            Pattern pattern = Pattern.compile(ignoreName);
            ignoreNamePatterns.add(pattern);
        }

        this.ignoreNamePatterns = ignoreNamePatterns;
    }

    @Override
    public SamplingResult shouldSample(Context parentContext, String traceId, String name, SpanKind spanKind, Attributes attributes, List<LinkData> parentLinks) {

        String targetName = name;

        if (spanKind == SpanKind.SERVER) {
            String requestMethod = attributes.get(HttpAttributes.HTTP_REQUEST_METHOD);
            String requestUrl = attributes.get(UrlAttributes.URL_FULL);

            String requestName = Stream.of(requestMethod, requestUrl)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(" "));

            if (!requestName.isEmpty()) targetName = requestName;
        }

        if (shouldIgnoreName(targetName))
            return SamplingResult.drop();
        else
            return SamplingResult.recordAndSample();
    }

    private boolean shouldIgnoreName(String name) {
        for (Pattern ignoreNamePattern : ignoreNamePatterns) {
            if (ignoreNamePattern.matcher(name).matches()) return true;
        }

        return false;
    }

    @Override
    public String getDescription() {
        return "TracerSampler";
    }
}

