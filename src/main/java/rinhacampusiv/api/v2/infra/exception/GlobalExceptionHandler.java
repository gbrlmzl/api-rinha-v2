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
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import rinhacampusiv.api.v2.infra.exception.auth.*;
import rinhacampusiv.api.v2.infra.exception.payments.MercadoPagoPaymentException;
import rinhacampusiv.api.v2.infra.exception.payments.PaymentNotFoundException;
import rinhacampusiv.api.v2.infra.exception.payments.TeamWithoutPaymentException;
import rinhacampusiv.api.v2.infra.exception.tournaments.*;
import rinhacampusiv.api.v2.infra.exception.users.AccountNotActivatedException;
import rinhacampusiv.api.v2.infra.exception.users.InvalidCurrentPasswordException;
import rinhacampusiv.api.v2.infra.exception.users.ProfilePicUploadException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {




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

    @ExceptionHandler(TeamNotFoundException.class)
    public ResponseEntity<?> tratarEquipeInexistente(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));

    }

    @ExceptionHandler(ImgurUploadException.class)
    public ResponseEntity<?> tratarErroUploadImgur(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", ex.getMessage()));
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

    @ExceptionHandler(TournamentNotFoundException.class)
    public ResponseEntity<?> tratarTorneioNaoEncontrado(Exception ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(TeamWithoutPaymentException.class)
    public ResponseEntity<?> tratarErroSemPagamento(TeamWithoutPaymentException ex){
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(TournamentFullException.class)
    public ResponseEntity<?> handleTournamentFull(TournamentFullException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> tratarErroIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }


    @ExceptionHandler(InvalidCurrentPasswordException.class)
    public ResponseEntity<?> tratarErroInvalidCurrentPassword(InvalidCurrentPasswordException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ProfilePicUploadException.class)
    public ResponseEntity<?> tratarErroUploadProfilePic(ProfilePicUploadException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", ex.getMessage()));
    }

//    @ExceptionHandler(InvalidProfilePicException.class)
//    public ResponseEntity<?> tratarErroInvalidProfilePic(InvalidProfilePicException ex) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(Map.of("error", ex.getMessage()));
//    }

    @ExceptionHandler(InvalidImageException.class)
    public ResponseEntity<?> tratarErroInvalidImage(InvalidImageException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> tratarErroUploadMuitoGrande(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(Map.of("error", "O arquivo enviado é muito grande. O tamanho máximo permitido é 5MB."));
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
