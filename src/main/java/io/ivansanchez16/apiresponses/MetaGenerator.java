package io.ivansanchez16.apiresponses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ivansanchez16.logger.LogMethods;
import io.ivansanchez16.logger.classes.ClientInfo;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Level;
import org.springframework.http.HttpStatusCode;

import java.util.UUID;

/**
 * MetaGenerator
 * Contiene metodos para implementar la meta correcta y devolver el campo devMessage solamente cuando no sea ambiente
 * productivo
 */
@RequiredArgsConstructor
public class MetaGenerator {

    private final EnvironmentConfig environmentConfig;
    private final LogMethods logMethods;

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
        if (logMethods.request != null) {
            ClientInfo clientInfo = (ClientInfo) logMethods.request.getAttribute("ORIGIN-INFO");
            if (clientInfo != null) {
                meta.setTransactionID(clientInfo.transactionUUID().toString());
            }
        }

        try {
            final String metaString = objectMapper.writeValueAsString(meta);

            Environment env = Environment.getByValue( environmentConfig.getEnvironment() );

            if ( env != null && env.equals(Environment.PRODUCTION) ){
                return objectMapper.readerWithView(MetaView.External.class)
                        .forType(Meta.class)
                        .readValue(metaString);
            }

            return objectMapper.readerWithView(MetaView.Internal.class)
                    .forType(Meta.class)
                    .readValue(metaString);

        } catch (JsonProcessingException e) {
            logMethods.logException(Level.ERROR, e);
            return meta;
        }
    }
}
