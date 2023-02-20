package com.socialnetwork.api.v1.controller;

import com.google.common.collect.ImmutableMap;
import com.socialnetwork.api.service.CounterService;
import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;
import io.opentracing.tag.Tags;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@AllArgsConstructor
public class CounterController {
    private CounterService counterService;

    @Qualifier("tracer")
    private JaegerTracer tracer;

    @SneakyThrows
    @GetMapping("/{profileId}/rooms/{chatId}/counter")
    public ResponseEntity<?> getCounter(
        @PathVariable int profileId,
        @PathVariable int chatId,
        @RequestHeader HttpHeaders headers

    ) {
        Span span = startServerSpan(tracer, headers, "app.chat.request.counter");
        try (Scope scope = tracer.scopeManager().activate(span)) {
            span.setTag("profileId", chatId);

            var res = counterService.getNumberOfMessages(chatId, profileId);

            span.log(ImmutableMap.of("event", "result", "value", res));
            return ResponseEntity.ok(res);
        } finally {
            span.finish();
        }
    }

    @SneakyThrows
    @PutMapping("/{profileId}/rooms/{chatId}/counter")
    public ResponseEntity<?> getCounter(
        @PathVariable int profileId,
        @PathVariable int chatId,
        @RequestHeader HttpHeaders headers,
        @RequestBody Integer numberOfNewMessages
    ) {
        Span span = startServerSpan(tracer, headers, "app.chat.request.counter");
        try (Scope scope = tracer.scopeManager().activate(span)) {
            span.setTag("profileId", chatId);

            var res = counterService.updateCounter(chatId, numberOfNewMessages, profileId);

            span.log(ImmutableMap.of("event", "result", "value", res));
            return ResponseEntity.ok(res);
        } finally {
            span.finish();
        }
    }


    public static Span startServerSpan(Tracer tracer, HttpHeaders httpHeaders, String operationName) {
        // format the headers for extraction
        Map<String, String> rawHeaders = httpHeaders.toSingleValueMap();
        final HashMap<String, String> headers = new HashMap<String, String>();
        for (String key : rawHeaders.keySet()) {
            headers.put(key, rawHeaders.get(key));
        }

        Tracer.SpanBuilder spanBuilder;

        try {
            SpanContext parentSpanCtx = tracer.extract(Format.Builtin.HTTP_HEADERS, new TextMapAdapter(headers));
            if (parentSpanCtx == null) {
                spanBuilder = tracer.buildSpan(operationName);
            } else {
                spanBuilder = tracer.buildSpan(operationName).asChildOf(parentSpanCtx);
            }
        } catch (IllegalArgumentException e) {
            spanBuilder = tracer.buildSpan(operationName);
        }
        return spanBuilder.withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_SERVER).start();
    }
}
