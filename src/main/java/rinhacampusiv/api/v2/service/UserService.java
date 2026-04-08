package rinhacampusiv.api.v2.service;

import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rinhacampusiv.api.v2.domain.user.User;
import rinhacampusiv.api.v2.domain.user.UserEssentialsDetails;
import rinhacampusiv.api.v2.domain.user.UserRepository;
import rinhacampusiv.api.v2.infra.exception.UserNotAuthenticatedException;
import rinhacampusiv.api.v2.infra.security.TokenService;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository repository;


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
}
