package io.ivansanchez16.logger;

import io.ivansanchez16.apiresponses.Meta;
import io.ivansanchez16.logger.classes.ClientInfo;
import io.ivansanchez16.logger.classes.Event;
import io.ivansanchez16.logger.enums.TransactionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class LogMethods {

    private final Logger LOGGER = LogManager.getLogger(LogMethods.class.getName());

    private final String SEPARADOR = "--------------------------------------------------";

    private String projectGroup = "";

    /**
     * Método para loggear el detalle de una excepción
     *
     * @param exception La excepción a loggear
     */
    public void logException(Exception exception){
        String[] classSplit = exception.getClass().getName().split("\\.");
        String clazz = classSplit[classSplit.length-1];
        String messageLog;

        LOGGER.error(SEPARADOR);
        messageLog = String.format("%s: %s", clazz,exception.getMessage());
        LOGGER.error(messageLog);

        // Get the error origin
        StackTraceElement[] st =
                Arrays.stream(exception.getStackTrace())
                        .filter(stackTraceElement -> stackTraceElement.getClassName().startsWith( projectGroup ))
                        .toArray(StackTraceElement[]::new);

        StackTraceElement ste;
        int stackTraceCount = Math.min(st.length, 3);
        LOGGER.error("Stack trace exception origin");

        for (int i = 0; i < stackTraceCount; i++) {
            ste = st[i];
            messageLog = String.format("%d: [Archivo: %s | Metodo: %s | Linea: %d]", i+1, ste.getFileName(), ste.getMethodName(), ste.getLineNumber());
            LOGGER.error(messageLog);
        }
        LOGGER.error(SEPARADOR);
    }

    public void logEvent(Event event, ClientInfo clientInfo) {
        String messageLog;

        LOGGER.info(SEPARADOR);

        LOGGER.info("EVENTO");
        messageLog = String.format("Transaction UUID: %s", clientInfo.transactionUUID().toString());
        LOGGER.info( messageLog );
        messageLog = String.format("Encabezado: %s", event.header());
        LOGGER.info( messageLog );

        event.rows().forEach(LOGGER::info);

        LOGGER.info(SEPARADOR);
    }

    public void logTransaction(TransactionType transactionType, Event event, ClientInfo clientInfo) {
        String messageLog;

        LOGGER.info(SEPARADOR);

        LOGGER.info("TRANSACCION");
        messageLog = String.format("Transaction UUID: %s", clientInfo.transactionUUID().toString());
        LOGGER.info( messageLog );
        messageLog = String.format("Tipo: %s - Encabezado: %s", transactionType.getValue(), event.header());
        LOGGER.info( messageLog );

        event.rows().forEach(LOGGER::info);

        LOGGER.info(SEPARADOR);
    }

    public String getProjectGroup() {
        return projectGroup;
    }

    public void setProjectGroup(String projectGroup) {
        this.projectGroup = projectGroup;
    }
}
