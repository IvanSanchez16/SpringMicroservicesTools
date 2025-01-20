package io.github.ivansanchez16.logger;

import io.github.ivansanchez16.logger.classes.ClientInfo;
import io.github.ivansanchez16.logger.classes.Event;
import io.github.ivansanchez16.logger.enums.SecurityEventType;
import io.github.ivansanchez16.logger.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class LogMethods {

    private final Logger logger = LogManager.getLogger(LogMethods.class.getName());

    private final String SEPARADOR = "--------------------------------------------------";

    private final LogConfig logConfig;

    private final RequestInfoHelper requestInfoHelper;

    /**
     * Método para loggear el detalle de una excepción
     *
     * @param level El nivel de severidad de la excepción
     * @param exception La excepción a loggear
     */
    public void logException(Level level, Exception exception){
        String[] classSplit = exception.getClass().getName().split("\\.");
        String clazz = classSplit[classSplit.length-1];
        String messageLog;

        logger.log(level, SEPARADOR);

        logOriginAndTransaction(level);
        logUserDetail(level);

        messageLog = String.format("%s: %s", clazz, exception.getMessage());
        logger.log(level, messageLog);

        // Get the error origin
        StackTraceElement[] st =
                Arrays.stream(exception.getStackTrace())
                        .filter(stackTraceElement -> stackTraceElement.getClassName().startsWith( logConfig.getProjectGroup() ))
                        .toArray(StackTraceElement[]::new);

        StackTraceElement ste;
        int stackTraceCount = Math.min(st.length, 3);
        logger.log(level, "Stack trace exception origin");

        for (int i = 0; i < stackTraceCount; i++) {
            ste = st[i];
            messageLog = String.format("%d: [File: %s | Method: %s | Line: %d]", i+1, ste.getFileName(), ste.getMethodName(), ste.getLineNumber());
            logger.log(level, messageLog);
        }
        logger.log(level, SEPARADOR);
    }

    /**
     * Método para loggear un evento dentro del componente
     *
     * @param event El evento a loggearse
     */
    public void logEvent(Event event) {
        String messageLog;

        logger.info(SEPARADOR);
        logger.info("EVENTO");

        logOriginAndTransaction(Level.INFO);

        messageLog = String.format("Encabezado: %s", event.header());
        logger.info( messageLog );

        event.rows().forEach(logger::info);

        logger.info(SEPARADOR);
    }

    /**
     * Método para loggear una transacción. Obtiene la información del origen de la petición
     * así como la sesión con la que se registra la transacción
     *
     * @param transactionType El tipo de transacción a loggear
     * @param event Los datos adicionales del evento transaccional
     */
    public void logTransaction(TransactionType transactionType, Event event) {
        String messageLog;

        logger.info(SEPARADOR);

        logger.info("TRANSACCIÓN");

        logOriginAndTransaction(Level.INFO);
        logUserDetail(Level.INFO);

        messageLog = String.format("Tipo: %s - Encabezado: %s", transactionType.getValue(), event.header());
        logger.info( messageLog );

        event.rows().forEach(logger::info);

        logger.info(SEPARADOR);
    }

    public void logSecurityEvent(SecurityEventType securityEventType, Event event) {
        String messageLog;

        logger.info(SEPARADOR);

        logger.info("EVENTO DE SEGURIDAD");

        logOriginAndTransaction(Level.INFO);
        logUserDetail(Level.INFO);

        messageLog = String.format("Tipo: %s - Encabezado: %s", securityEventType.getValue(), event.header());
        logger.info( messageLog );

        event.rows().forEach(logger::info);

        logger.info(SEPARADOR);
    }

    public RequestInfoHelper getRequestInfoHelper() {
        return requestInfoHelper;
    }

    public LogConfig getLogConfig() {
        return logConfig;
    }

    private void logUserDetail(Level level) {
        JSONObject sessionInfo = requestInfoHelper.getSessionInfoFromRequest();
        if (sessionInfo == null) {
            logger.log(level, "Info del usuario: No se encontró ninguna sesión");
            return;
        }

        StringBuilder messageLog = new StringBuilder();
        logger.log(level, "Info del usuario:");

        Iterator<String> keys = sessionInfo.keys();
        while (keys.hasNext()) {
            String header = keys.next();
            messageLog.append( String.format("%s: [%s] | ", header, sessionInfo.get(header)) );
        }

        logger.log(level, messageLog.substring(0, messageLog.length() - 3));
    }

    private void logOriginAndTransaction(Level level) {
        ClientInfo originInfo = requestInfoHelper.getClientInfoFromRequest();
        String messageLog;

        messageLog = String.format("Transaction UUID: %s", originInfo.transactionUUID().toString());
        logger.log(level, messageLog);

        messageLog = String.format("Origen de la petición: Address: [%s] | Host: [%s]",
                originInfo.originAddress(), originInfo.originHost());
        logger.log(level, messageLog);
    }
}
