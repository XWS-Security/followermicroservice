package org.nistagram.followermicroservice.controller;

import org.modelmapper.ModelMapper;
import org.nistagram.followermicroservice.controller.dto.FollowRequestDto;
import org.nistagram.followermicroservice.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/interactions")
public class InteractionController {
    private final FollowService followService;
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public InteractionController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/")
    public ResponseEntity<String> follow(@RequestBody @Valid FollowRequestDto followRequestDto) {
        // TODO: process follow request
        followService.follow(followRequestDto.getFollowerUsername(), followRequestDto.getFolloweeUsername());
        return new ResponseEntity<>("Follow request processed", HttpStatus.OK);
    }
}
