package rinhacampusiv.api.v2.infra.exception;

import com.auth0.jwt.exceptions.TokenExpiredException;
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
import rinhacampusiv.api.v2.infra.exception.payments.PaymentNotFoundException;
import rinhacampusiv.api.v2.infra.exception.payments.TeamWithoutPaymentException;

import java.util.Map;

@RestControllerAdvice
public class TratadorDeErros {




    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> tratarErroDadosInvalidos(MethodArgumentNotValidException ex) {
        var erros = ex.getFieldErrors();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)  // 400 — campos inválidos
                .body(erros.stream().map(DadosErroValidacao::new).toList());
    }

    // JSON malformado ou body ausente
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> tratarErroBodyInvalido() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
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

    @ExceptionHandler(ValidatorException.class)
    public ResponseEntity<?> tratarErroDeValidacao(Exception ex){
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(Map.of("error", ex.getMessage()));
    }



    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> tratarUsernameInexistente(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", ex.getMessage()));

    }

    @ExceptionHandler(ImgurUploadException.class)
    public void tratarErroUploadImgur(Exception ex){
        //Não deve parar o fluxo de cadastro, apenas avisar que houve um erro no upload do escudo.
    }

    @ExceptionHandler(AccountNotActivatedException.class)
    public ResponseEntity<?> tratarContaNaoAtivada(Exception ex){
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", ex.getMessage()));

    }

    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ResponseEntity<?> tratarUsuarioNaoAutenticado(Exception ex){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<?> tratarTokenExpirado(){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "token_expired"));

    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> tratarTokenInvalido(Exception ex){
        String message = !ex.getMessage().isBlank() ? ex.getMessage()  : "invalid_token";
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", message));
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<?> tratarPagamentoNaoEncontrado(Exception ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(SendEmailException.class)
    public void tratarErroNoEnvioDoEmail(){
            //O fluxo não deve ser interrompido.
        System.out.println("Houve um erro ao enviar o email.");
        //Debug
        }


    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public ResponseEntity<?> tratarRefreshTokenNaoEncontrado(Exception ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MercadoPagoPaymentException.class)
    public ResponseEntity<?> tratarErroMercadoPago(MercadoPagoPaymentException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(TournamentNotExistsException.class)
    public ResponseEntity<?> tratarTorneioNaoEncontrado(Exception ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(TeamWithoutPaymentException.class)
    public ResponseEntity<?> tratarErroSemPagamento(TeamWithoutPaymentException ex){
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(Map.of("error", ex.getMessage()));
    }


    //==================================================================================================================
    // 404 genérico (JPA)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> tratarErro404(Exception ex) {
        String message = !ex.getMessage().isBlank() ? ex.getMessage() : "";

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", message));
    }

    // Fallback — qualquer erro não tratado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> tratarErroGenerico(Exception ex) {
        String message = "Erro interno do servidor: ";
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", message));
    }


    private record DadosErroValidacao(String campo, String mensagem) {
        public DadosErroValidacao(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }

}





