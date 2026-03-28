package rinhacampusiv.api.v2.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rinhacampusiv.api.v2.domain.user.User;
import rinhacampusiv.api.v2.domain.user.UserDataUpdateDTO;
import rinhacampusiv.api.v2.domain.user.UserRepository;

import java.util.Map;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;



    @PatchMapping("/me")  // /users/me → atualiza o próprio usuário autenticado
    @Transactional
    public ResponseEntity<?> update(@RequestBody @Valid UserDataUpdateDTO data, Authentication authentication) {

        // Pega o usuário autenticado direto do SecurityContext — sem precisar de ID na URL

        User user = (User) authentication.getPrincipal();

        // Recarrega do banco para garantir dados frescos
        User managedUser = repository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        // Se enviou nova senha, valida a senha atual antes de alterar
        if (data.newPassword() != null) {
            if (data.currentPassword() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Informe a senha atual para alterá-la"));
            }

            boolean currentPasswordMatches = passwordEncoder.matches(
                    data.currentPassword(), managedUser.getPassword()
            );

            if (!currentPasswordMatches) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Senha atual incorreta"));
            }
        }

        managedUser.update(data, passwordEncoder);

        return ResponseEntity.ok(Map.of(
                "username", managedUser.getUsername(),
                "nickname", managedUser.getNickname(),
                "email",    managedUser.getEmail(),
                "profilePic", managedUser.getProfilePic() != null ? managedUser.getProfilePic() : ""
        ));
    }
}