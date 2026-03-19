package rinhacampusiv.api.v2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rinhacampusiv.api.v2.domain.user.UserRepository;
import rinhacampusiv.api.v2.infra.security.TokenService;


@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserRepository repository;

    @Autowired
    private TokenService tokenService;

    @GetMapping
    public ResponseEntity testAction() {

        return ResponseEntity.ok("Chegou");

    }
}
