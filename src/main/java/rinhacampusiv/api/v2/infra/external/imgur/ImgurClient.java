package rinhacampusiv.api.v2.infra.external.imgur;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import rinhacampusiv.api.v2.infra.exception.tournaments.ImgurUploadException;
import rinhacampusiv.api.v2.infra.exception.tournaments.InvalidImageException;
import rinhacampusiv.api.v2.infra.exception.users.ProfilePicUploadException;
import rinhacampusiv.api.v2.infra.loggers.UploadImageLogger;

import java.util.List;

@Service
public class ImgurClient {

    private static final String IMGUR_UPLOAD_URL = "https://api.imgur.com/3/image";

    @Value("${imgur.access.token}")
    private String accessToken;

    @Autowired
    private UploadImageLogger uploadLogger;

    private final RestTemplate restTemplate  = new RestTemplate();
    private final ObjectMapper objectMapper  = new ObjectMapper();

    /**
     * Faz upload de um arquivo de imagem para o Imgur.
     *
     * @param file      arquivo recebido via multipart
     * @param teamName  nome da equipe — usado como título da imagem no Imgur
     * @return          URL pública da imagem
     */

    public String uploadShield(MultipartFile file, String teamName) {
        uploadLogger.requestingShieldUploadLog(file, teamName);
        long start = System.currentTimeMillis();
        String title = teamName != null ? teamName : "team_shield";

        try {
            String link = executeUpload(file, title, "ENwHp34");
            uploadLogger.shieldUploadSuccessLog(teamName, link, System.currentTimeMillis() - start);
            return link;
        } catch (ImgurUploadException e) {
            uploadLogger.shieldUploadErrorLog(teamName, e, System.currentTimeMillis() - start);
            return null;
        } catch (Exception e) {
            uploadLogger.shieldUploadErrorLog(teamName, e, System.currentTimeMillis() - start);
            return null;
        }
    }

    public String uploadTournamentImage(MultipartFile file, String tournamentName) {
        uploadLogger.requestingTournamentImageUploadLog(file, tournamentName);
        long start = System.currentTimeMillis();
        String title = tournamentName != null ? tournamentName : "tournament_image";

        try {
            String link = executeUpload(file, title, "sOOAAOb");
            uploadLogger.tournamentImageUploadSuccessLog(tournamentName, link, System.currentTimeMillis() - start);
            return link;
        } catch (ImgurUploadException e) {
            ImgurUploadException wrapped = new ImgurUploadException("Imgur recusou o upload da imagem do torneio: " + e.getMessage());
            uploadLogger.tournamentImageUploadErrorLog(tournamentName, wrapped, System.currentTimeMillis() - start);
            throw wrapped;
        } catch (Exception e) {
            ImgurUploadException wrapped = new ImgurUploadException("Erro ao fazer upload da imagem do torneio: " + e.getMessage());
            uploadLogger.tournamentImageUploadErrorLog(tournamentName, wrapped, System.currentTimeMillis() - start);
            throw wrapped;
        }
    }

    public String uploadProfilePicImage(MultipartFile file, String userId) {
        uploadLogger.requestingProfilePicUploadLog(file, userId);
        long start = System.currentTimeMillis();

        try {
            String link = executeUpload(file, userId, "s8LvvIS");
            uploadLogger.profilePicUploadSuccessLog(userId, link, System.currentTimeMillis() - start);
            return link;
        } catch (Exception e) {
            uploadLogger.profilePicUploadErrorLog(userId, e, System.currentTimeMillis() - start);
            throw new ProfilePicUploadException("Erro ao fazer upload da foto do perfil, por favor tente novamente mais tarde.");
        }
    }

    private String executeUpload(MultipartFile file, String title, String album) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(accessToken);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", file.getResource());
        body.add("type", "file");
        body.add("title", title);
        body.add("album", album);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(IMGUR_UPLOAD_URL, request, String.class);

        JsonNode root = objectMapper.readTree(response.getBody());

        boolean success = root.path("success").asBoolean(false);
        if (!success) {
            String errorMsg = root.path("data").path("error").asText("Erro desconhecido");
            throw new ImgurUploadException(errorMsg);
        }

        return root.path("data").path("link").asText();
    }

    public void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            uploadLogger.imageValidationFailedLog("Arquivo vazio ou nulo", null, 0);
            throw new InvalidImageException("Arquivo de imagem vazio");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            uploadLogger.imageValidationFailedLog("Não é uma imagem válida", contentType, file.getSize());
            throw new InvalidImageException("Arquivo não é uma imagem válida");
        }

        List<String> allowedFormats = List.of("image/jpeg", "image/png");
        if (!allowedFormats.contains(contentType.toLowerCase())) {
            uploadLogger.imageValidationFailedLog("Formato não suportado", contentType, file.getSize());
            throw new InvalidImageException("Formato de imagem não suportado. Use JPEG ou PNG.");
        }

        long maxSizeBytes = 5L * 1024 * 1024;
        if (file.getSize() > maxSizeBytes) {
            uploadLogger.imageValidationFailedLog("Arquivo excede 5MB", contentType, file.getSize());
            throw new InvalidImageException("Arquivo de imagem muito grande. Máximo permitido: 5MB");
        }
    }
}
