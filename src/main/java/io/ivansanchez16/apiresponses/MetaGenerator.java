package io.ivansanchez16.apiresponses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ivansanchez16.generalutilery.LogFile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;


/**
 * MetaGenerator
 * Contiene metodos para implementar la meta correcta y devolver el campo devMessage solamente cuando no sea ambiente
 * productivo
 */
@Component
@RequiredArgsConstructor
public class MetaGenerator {

    private final AppConfig appConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Meta crearMetaObject(HttpStatusCode httpStatus) {
        final Meta meta = new Meta(httpStatus.toString(), httpStatus.value());
        return crearMeta(meta);
    }

    public Meta crearMetaObject(HttpStatusCode httpStatus, String message) {
        final Meta meta = new Meta(httpStatus.toString(), httpStatus.value(), message);
        return crearMeta(meta);
    }

    public Meta crearMetaObject(HttpStatusCode httpStatus, String message, String devMessage) {
        final Meta meta = new Meta(httpStatus.toString(), httpStatus.value(), message, devMessage);
        return crearMeta(meta);
    }

    private Meta crearMeta(Meta meta) {
        try {
            final String metaString = objectMapper.writeValueAsString(meta);

            String env = appConfig.getEnvironment();

            if (env.equals("production")){
                return objectMapper.readerWithView(MetaView.External.class)
                        .forType(Meta.class)
                        .readValue(metaString);
            }

            return objectMapper.readerWithView(MetaView.Internal.class)
                    .forType(Meta.class)
                    .readValue(metaString);

        } catch (JsonProcessingException e) {
            LogFile.logExcepcion(e);
            return meta;
        }
    }
}
