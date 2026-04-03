package rinhacampusiv.api.v2.domain.mercadoPago;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.time.OffsetDateTime;

public record WebHookNotificationData(
        String id,

        String type,

        @JsonAlias("date_created")
        OffsetDateTime dateCreated,

        @JsonAlias("action")
        String action,

        Data data
) {

    public record Data(String id){

    }
}
