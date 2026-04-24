package rinhacampusiv.api.v2.controller.users;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import rinhacampusiv.api.v2.domain.user.UserDataUpdateDTO;
import rinhacampusiv.api.v2.service.user.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;



    @PatchMapping(value = "/me", consumes =  MediaType.MULTIPART_FORM_DATA_VALUE)  // /users/me → atualiza o próprio usuário autenticado
    public ResponseEntity<?> update(
            @RequestPart("data") @Valid UserDataUpdateDTO data,
            @RequestPart(value = "profilePic", required = false) MultipartFile profilePic,
            Authentication authentication) {

        var updatedUser = userService.updateUser(data, profilePic, authentication);

        return ResponseEntity.ok(updatedUser);
    }
}