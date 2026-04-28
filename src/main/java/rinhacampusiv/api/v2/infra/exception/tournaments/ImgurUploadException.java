package rinhacampusiv.api.v2.infra.exception.tournaments;

public  class ImgurUploadException extends RuntimeException {
    public ImgurUploadException(String message) {
        super(message);
    }
    public ImgurUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
