package com.socialnetwork.api.v1.controller;

import com.google.common.collect.ImmutableMap;
import com.socialnetwork.api.service.MessageService;
import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.propagation.Format;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@AllArgsConstructor
public class ChatController {
    private MessageService messageService;

    @Qualifier("tracer")
    private JaegerTracer tracer;

    @SneakyThrows
    @GetMapping("/{id}/rooms")
    public ResponseEntity<?> getRooms(@PathVariable int id) {
        Span span = tracer.buildSpan("app.api.request.get-chat-rooms").start();
        try (Scope scope = tracer.scopeManager().activate(span, true)) {
            span.setTag("profileID", id);
            var res = messageService.getListOfRooms(id);
            span.log(ImmutableMap.of("event", "result", "value", res));
            return ResponseEntity.ok(
                Map.of(
                    "rooms", res
                )
            );
        } finally {
            span.finish();
        }
    }

    @SneakyThrows
    @GetMapping("/{id}/rooms/{roomId}")
    public ResponseEntity<?> getRooms(@PathVariable int id, @PathVariable int roomId) {
        Span span = tracer.buildSpan("app.api.request.get-messages").start();
        try (Scope scope = tracer.scopeManager().activate(span, true)) {
            span.setTag("profileID", id);
            var res = this.messageService.getMessages(id, roomId);
            span.log(ImmutableMap.of("event", "result", "value", res));
            span.finish();
            return ResponseEntity.ok(
                Map.of(
                    "messages",res
                )
            );
        } finally {
            span.finish();
        }
    }
}
