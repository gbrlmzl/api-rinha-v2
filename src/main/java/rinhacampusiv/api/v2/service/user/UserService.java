package rinhacampusiv.api.v2.service.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rinhacampusiv.api.v2.domain.user.User;
import rinhacampusiv.api.v2.domain.user.UserDataUpdateDTO;
import rinhacampusiv.api.v2.domain.user.UserEssentialsDetails;
import rinhacampusiv.api.v2.domain.user.UserRepository;
import rinhacampusiv.api.v2.infra.exception.UserNotAuthenticatedException;
import rinhacampusiv.api.v2.infra.exception.user.InvalidCurrentPasswordException;
import rinhacampusiv.api.v2.infra.external.ImgurClient;
import rinhacampusiv.api.v2.infra.security.TokenService;

import java.util.Arrays;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ImgurClient imgurClient;


    public UserEssentialsDetails getAuthenticatedUser(HttpServletRequest request) {

        if (request.getCookies() == null) {
            throw new UserNotAuthenticatedException("no_token"); //padrão de mensagem necessário para o frontend implementar o silent refresh
        }

        String accessToken = Arrays.stream(request.getCookies())
                .filter(c -> "JWT".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (accessToken == null) {
            throw new UserNotAuthenticatedException("no_token"); //padrão de mensagem necessário para o frontend implementar o silent refresh
        }


        String subject = tokenService.getSubject(accessToken);
        Optional<User> userOpt = repository.findById(Long.parseLong(subject));


        if (userOpt.isPresent()) {

            return new UserEssentialsDetails(userOpt.get());

        } else {

            throw new UsernameNotFoundException("user_not_found");
        }

    }

    public UserEssentialsDetails updateUser(UserDataUpdateDTO data, MultipartFile profilePic, Authentication authentication){
        User user = (User) authentication.getPrincipal();

        // Recarrega do banco para garantir dados frescos
        User managedUser = repository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        // Se enviou nova senha, valida a senha atual antes de alterar
        handleNickNameChange(data, managedUser);
        handlePasswordChange(data, managedUser);
        handleProfilePicChange(profilePic, managedUser);

        repository.save(managedUser);

        return new UserEssentialsDetails(managedUser);

    }

    private void handleNickNameChange(UserDataUpdateDTO data, User managedUser){
        if(data.nickname() != null){
            managedUser.updateNickname(data);
        }
    }

    private void handlePasswordChange(UserDataUpdateDTO data, User managedUser) {
        if (data.newPassword() != null) {
            if (data.currentPassword() == null) {
                throw new InvalidCurrentPasswordException("Senha atual não pode estar vazia!");
            }

            boolean currentPasswordMatches = passwordEncoder.matches(
                    data.currentPassword(), managedUser.getPassword()
            );

            if (!currentPasswordMatches) {
                throw new InvalidCurrentPasswordException("Senha atual incorreta");
            }

            managedUser.update(data, passwordEncoder);
        }
    }

    private void handleProfilePicChange(MultipartFile profilePic, User managedUser){
        if(profilePic != null && !profilePic.isEmpty()){
            imgurClient.validateImage(profilePic);

            String profilePicUrl = imgurClient.uploadProfilePicImage(profilePic, managedUser.getId().toString());
            managedUser.updateProfilePic(profilePicUrl);
        }
    }
}
