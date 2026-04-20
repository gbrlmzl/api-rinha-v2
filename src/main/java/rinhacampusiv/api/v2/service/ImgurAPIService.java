package rinhacampusiv.api.v2.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import rinhacampusiv.api.v2.infra.exception.ImgurUploadException;

@Service
public class ImgurAPIService {

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

}