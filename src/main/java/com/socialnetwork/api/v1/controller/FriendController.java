package com.socialnetwork.api.v1.controller;

import com.socialnetwork.api.service.FriendService;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class FriendController {
    private FriendService friendService;

    @SneakyThrows
    @GetMapping("/{id}/friend")
    public ResponseEntity<?> getFriends(@PathVariable int id) {
        return ResponseEntity.ok(
            Map.of(
                "friends",
                friendService.getListOfFriendsIds(id)
            )
        );

    }


    @PostMapping("/{id}/friend")
    public ResponseEntity<?> addFriend(
        @PathVariable int id,
        @RequestBody List<Integer> friendIds
    ) {
        var res = friendService.addFriend(id, friendIds);
        if (res) {
            return ResponseEntity.ok("Added list of friends");
        }
        return ResponseEntity.badRequest().build();

    }
}
