package com.socialnetwork.api.v1.controller;

import com.socialnetwork.api.service.FeedService;
import com.socialnetwork.api.service.FriendService;
import com.socialnetwork.api.service.MessageService;
import com.socialnetwork.api.service.ProfileService;
import com.socialnetwork.api.v1.domain.ProfileDto;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
@Slf4j
public class ProfileController {

    @Autowired
    ProfileService profileService;

    @Autowired
    FriendService friendService;

    @Autowired
    MessageService messageService;

    @Autowired
    FeedService feedService;

    @GetMapping("")
    public List<ProfileDto> getProfiles(
        @RequestParam(required = false) String firstName,
        @RequestParam(required = false) String lastName
    ) {
        if (firstName != null && lastName != null) {
            return profileService.getProfiles(firstName, lastName);
        }
        return profileService.getProfiles();
    }

    @GetMapping("/{id}")
    public ProfileDto getProfile(@PathVariable int id) {
        // TODO prevent sending java exceptions in case of non-existed profile
        return profileService.getProfileById(id);
    }

    @PostMapping("/{id}/feed")
    public boolean postToFeed(
        @PathVariable int id,
        @RequestBody String text
    ) {
        return feedService.postFeed(id, text);
    }

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
                messageService.getMessages(roomId)
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

    @PostMapping("")
    public ResponseEntity<?> createProfile(@RequestBody ProfileDto profileDto) {
        // TODO add logic that ROLE_ADMIN can create any profile, but ROLE_USER can create only its own profile
        var res = profileService.createProfile(profileDto);
        if (res) {
            return ResponseEntity.ok(
                String.format(
                    "Profile created for ownerId %d, %s %s",
                    profileDto.getOwnerId(),
                    profileDto.getFirstName(),
                    profileDto.getLastName()
                ));
        }
        return ResponseEntity.badRequest().build();
    }
}
