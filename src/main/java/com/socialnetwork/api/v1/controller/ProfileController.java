package com.socialnetwork.api.v1.controller;

import com.socialnetwork.api.service.ProfileService;
import com.socialnetwork.api.v1.domain.ProfileDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
@Slf4j
public class ProfileController {

    @Autowired
    ProfileService profileService;

    @GetMapping("")
    public List<ProfileDto> getProfiles() {
        return profileService.getProfiles();
    }

    @GetMapping("/{id}")
    public ProfileDto getProfile(@PathVariable int id) {
        // TODO prevent sending java exceptions in case of non-existed profile
        return profileService.getProfileById(id);
    }

    @PostMapping("")
    public ResponseEntity<?> createProfile(@RequestBody ProfileDto profileDto) {
        // TODO add logic that ROLE_ADMIN can create any profile, but ROLE_USER can create only its own profile
        var res = profileService.createProfile(profileDto);
        if (res) {
            return ResponseEntity.ok("Profile created");
        }
        return ResponseEntity.badRequest().build();
    }
}
