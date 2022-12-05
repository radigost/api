package com.socialnetwork.api.v1.controller;

import com.socialnetwork.api.service.MessageService;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ChatController {
    private MessageService messageService;

    @SneakyThrows
    @GetMapping("/{id}/rooms")
    public ResponseEntity<?> getRooms(@PathVariable int id) {
        return ResponseEntity.ok(
            Map.of(
                "rooms",
                messageService.getListOfRooms(id)
            )
        );
    }

    @SneakyThrows
    @GetMapping("/{id}/rooms/{roomId}")
    public ResponseEntity<?> getRooms(@PathVariable int id, @PathVariable int roomId) {
        return ResponseEntity.ok(
            Map.of(
                "messages",
                this.messageService.getMessages(roomId)
            )
        );
    }
}
