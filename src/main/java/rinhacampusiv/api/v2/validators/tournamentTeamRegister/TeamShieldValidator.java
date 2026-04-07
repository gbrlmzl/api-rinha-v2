package rinhacampusiv.api.v2.validators.tournamentTeamRegister;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public class TeamShieldValidator implements ConstraintValidator<ValidTeamShield, MultipartFile> {

    private static final long MAX_SIZE = 10 * 1024 * 1024; // 10MB
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("png", "jpg", "jpeg");

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true; // escudo é opcional
        }

        // Verifica tamanho
        if (file.getSize() > MAX_SIZE) {
            return false;
        }

        // Verifica extensão pelo nome original do arquivo
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            return false;
        }

        String extension = originalName
                .substring(originalName.lastIndexOf('.') + 1)
                .toLowerCase();

        return ALLOWED_EXTENSIONS.contains(extension);
    }
}