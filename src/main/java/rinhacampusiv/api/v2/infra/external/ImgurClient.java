package rinhacampusiv.api.v2.infra.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import rinhacampusiv.api.v2.infra.exception.ImgurUploadException;
import rinhacampusiv.api.v2.infra.exception.ProfilePicUploadException;
import rinhacampusiv.api.v2.infra.exception.user.InvalidProfilePicException;

import java.util.List;

@Service
public class ImgurClient {

    private static final String IMGUR_UPLOAD_URL = "https://api.imgur.com/3/image";


    @Value("${imgur.access.token}")
    private String accessToken;


    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper  = new ObjectMapper();

    /**
     * Faz upload de um arquivo de imagem para o Imgur.
     *
     * @param file      arquivo recebido via multipart
     * @param teamName  nome da equipe — usado como título da imagem no Imgur
     * @return          URL pública da imagem
     *
     */
    public String uploadShield(MultipartFile file, String teamName) {
        try {
            // Monta o multipart para a API do Imgur
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setBearerAuth(accessToken); // "Bearer {token}"

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", file.getResource()); // Spring converte MultipartFile → Resource
            body.add("type", "file");
            body.add("title", teamName != null ? teamName : "team_shield");
            body.add("album", "ENwHp34");

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    IMGUR_UPLOAD_URL,
                    request,
                    String.class
            );

            // Parseia a resposta do Imgur
            JsonNode root = objectMapper.readTree(response.getBody());

            boolean success = root.path("success").asBoolean(false);
            if (!success) {
                String errorMsg = root.path("data").path("error").asText("Erro desconhecido");
                //throw new ImgurUploadException("Imgur recusou o upload: " + errorMsg);
                //Salvar logo dizendo que deu errado para auditoria
                //return null

            }

            return root.path("data").path("link").asText();

        } catch (ImgurUploadException e) {
            //Regra de negócio: O fluxo de inscrição não deve ser interrompido por erro ao fazer o upload do escudo.
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public String uploadTournamentImage(MultipartFile file, String tournamentName) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setBearerAuth(accessToken);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", file.getResource());
            body.add("type", "file");
            body.add("title", tournamentName != null ? tournamentName : "tournament_image");
            body.add("album", "sOOAAOb");

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(IMGUR_UPLOAD_URL, request, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());

            boolean success = root.path("success").asBoolean(false);
            if (!success) {
                String errorMsg = root.path("data").path("error").asText("Erro desconhecido");
                throw new ImgurUploadException("Imgur recusou o upload da imagem do torneio: " + errorMsg);
            }

            return root.path("data").path("link").asText();

        } catch (ImgurUploadException e) {
            throw e;
        } catch (Exception e) {
            throw new ImgurUploadException("Erro ao fazer upload da imagem do torneio: " + e.getMessage());
        }
    }

    public String uploadProfilePicImage(MultipartFile file, String userId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setBearerAuth(accessToken);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", file.getResource());
            body.add("type", "file");
            body.add("title", userId);
            body.add("album", "s8LvvIS");

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(IMGUR_UPLOAD_URL, request, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());

            return root.path("data").path("link").asText();

        } catch (Exception e) {
            throw new ProfilePicUploadException("Erro ao fazer upload da foto do perfil, por favor tente novamente mais tarde.");
        }
    }

    public void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidProfilePicException("Arquivo de imagem vazio");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidProfilePicException("Arquivo não é uma imagem válida");
        }

        // Formatos aceitos pelo Imgur (mais comuns)
        List<String> allowedFormats = List.of("image/jpeg", "image/png", "image/gif");
        if (!allowedFormats.contains(contentType.toLowerCase())) {
            throw new InvalidProfilePicException("Formato de imagem não suportado. Use JPEG, PNG ou GIF.");
        }

        // Limite de tamanho (exemplo: 5 MB)
        long maxSizeBytes = 5 * 1024 * 1024; // 5 MB
        if (file.getSize() > maxSizeBytes) {
            throw new InvalidProfilePicException("Arquivo de imagem muito grande. Máximo permitido: 5MB");
        }
    }


}