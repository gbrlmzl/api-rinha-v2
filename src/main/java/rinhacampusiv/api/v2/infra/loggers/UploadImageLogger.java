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

    public void shieldUploadSuccessLog(String teamName, String imageUrl) {
        logger.info("[IMGUR] Upload de escudo concluído com sucesso | equipe={} | url={}",
                teamName, imageUrl);
    }

    public void shieldUploadErrorLog(String teamName, Exception e) {
        logger.error("[IMGUR] Falha ao fazer upload do escudo | equipe={} | erro={}",
                teamName, e.getMessage());
    }


    // ─── Upload de imagem de torneio ────────────────────────────────────────────

    public void requestingTournamentImageUploadLog(MultipartFile file, String tournamentName) {
        logger.info("[IMGUR] Iniciando upload de imagem do torneio | torneio={} | arquivo={} | formato={} | tamanho={}KB",
                tournamentName,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize() / 1024);
    }

    public void tournamentImageUploadSuccessLog(String tournamentName, String imageUrl) {
        logger.info("[IMGUR] Upload de imagem do torneio concluído com sucesso | torneio={} | url={}",
                tournamentName, imageUrl);
    }

    public void tournamentImageUploadErrorLog(String tournamentName, Exception e) {
        logger.error("[IMGUR] Falha ao fazer upload da imagem do torneio | torneio={} | erro={}",
                tournamentName, e.getMessage());
    }


    // ─── Upload de foto de perfil ──────────────────────────────────────────────

    public void requestingProfilePicUploadLog(MultipartFile file, String userId) {
        logger.info("[IMGUR] Iniciando upload de foto de perfil | userId={} | arquivo={} | formato={} | tamanho={}KB",
                userId,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize() / 1024);
    }

    public void profilePicUploadSuccessLog(String userId, String imageUrl) {
        logger.info("[IMGUR] Upload de foto de perfil concluído com sucesso | userId={} | url={}",
                userId, imageUrl);
    }

    public void profilePicUploadErrorLog(String userId, Exception e) {
        logger.error("[IMGUR] Falha ao fazer upload da foto de perfil | userId={} | erro={}",
                userId, e.getMessage());
    }
}
