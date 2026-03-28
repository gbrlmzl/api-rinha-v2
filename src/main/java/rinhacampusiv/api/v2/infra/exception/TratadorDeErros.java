package rinhacampusiv.api.v2.infra.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class TratadorDeErros {

    // 404 genérico (JPA)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> tratarErro404() {
        return ResponseEntity.notFound().build();
    }

    // Validação de campos (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> tratarErro400(MethodArgumentNotValidException ex) {
        var erros = ex.getFieldErrors();
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY) // 422 — dados recebidos mas inválidos
                .body(erros.stream().map(DadosErroValidacao::new).toList());
    }

    // JSON malformado ou body ausente
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> tratarErroBodyInvalido() {
        return ResponseEntity
                .badRequest()
                .body(Map.of("error", "Corpo da requisição inválido ou ausente"));
    }

    // Conflito de username no registro
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<?> tratarUsernameExistente(UsernameAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409 — recurso já existe
                .body(Map.of("error", ex.getMessage()));
    }

    // Conflito de email no registro
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<?> tratarEmailExistente(EmailAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409
                .body(Map.of("error", ex.getMessage()));
    }

    // Credenciais inválidas no login
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> tratarCredenciaisInvalidas() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Credenciais inválidas"));
    }

    // Conta bloqueada ou desativada
    @ExceptionHandler({LockedException.class, DisabledException.class})
    public ResponseEntity<?> tratarContaInativa(AuthenticationException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", ex.getMessage()));
    }

    // Fallback — qualquer erro não tratado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> tratarErroGenerico(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erro interno no servidor"));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> tratarUsernameInexistente() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Credenciais inválidas"));

    }

    private record DadosErroValidacao(String campo, String mensagem) {
        public DadosErroValidacao(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }
}


