package rinhacampusiv.api.v2.infra.loggers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class UploadImageLogger {

    private static final Logger logger = LoggerFactory.getLogger(UploadImageLogger.class);


    // ─── Upload de escudo de equipe ────────────────────────────────────────────

    public void requestingShieldUploadLog(MultipartFile file, String teamName) {
        logger.info("[IMGUR] Iniciando upload de escudo | equipe={} | arquivo={} | formato={} | tamanho={}KB",
                teamName,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize() / 1024);
    }

    public void shieldUploadSuccessLog(String teamName, String imageUrl, long elapsedMs) {
        logger.info("[IMGUR] Upload de escudo concluído com sucesso | equipe={} | url={} | tempo={}ms",
                teamName, imageUrl, elapsedMs);
    }

    public void shieldUploadErrorLog(String teamName, Exception e, long elapsedMs) {
        logger.error("[IMGUR] Falha ao fazer upload do escudo | equipe={} | erro={} | tempo={}ms",
                teamName, e.getMessage(), elapsedMs);
    }


    // ─── Upload de imagem de torneio ────────────────────────────────────────────

    public void requestingTournamentImageUploadLog(MultipartFile file, String tournamentName) {
        logger.info("[IMGUR] Iniciando upload de imagem do torneio | torneio={} | arquivo={} | formato={} | tamanho={}KB",
                tournamentName,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize() / 1024);
    }

    public void tournamentImageUploadSuccessLog(String tournamentName, String imageUrl, long elapsedMs) {
        logger.info("[IMGUR] Upload de imagem do torneio concluído com sucesso | torneio={} | url={} | tempo={}ms",
                tournamentName, imageUrl, elapsedMs);
    }

    public void tournamentImageUploadErrorLog(String tournamentName, Exception e, long elapsedMs) {
        logger.error("[IMGUR] Falha ao fazer upload da imagem do torneio | torneio={} | erro={} | tempo={}ms",
                tournamentName, e.getMessage(), elapsedMs);
    }


    // ─── Upload de foto de perfil ──────────────────────────────────────────────

    public void requestingProfilePicUploadLog(MultipartFile file, String userId) {
        logger.info("[IMGUR] Iniciando upload de foto de perfil | userId={} | arquivo={} | formato={} | tamanho={}KB",
                userId,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize() / 1024);
    }

    public void profilePicUploadSuccessLog(String userId, String imageUrl, long elapsedMs) {
        logger.info("[IMGUR] Upload de foto de perfil concluído com sucesso | userId={} | url={} | tempo={}ms",
                userId, imageUrl, elapsedMs);
    }

    public void profilePicUploadErrorLog(String userId, Exception e, long elapsedMs) {
        logger.error("[IMGUR] Falha ao fazer upload da foto de perfil | userId={} | erro={} | tempo={}ms",
                userId, e.getMessage(), elapsedMs);
    }


    // ─── Validação ─────────────────────────────────────────────────────────────

    public void imageValidationFailedLog(String reason, String contentType, long sizeBytes) {
        logger.warn("[IMGUR] Imagem rejeitada na validação | motivo={} | formato={} | tamanho={}KB",
                reason, contentType != null ? contentType : "desconhecido", sizeBytes / 1024);
    }
}
