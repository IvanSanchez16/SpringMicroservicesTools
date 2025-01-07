package io.ivansanchez16.logger;

import io.ivansanchez16.logger.classes.ClientInfo;
import io.ivansanchez16.logger.classes.Event;
import io.ivansanchez16.logger.enums.TransactionType;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
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

    @Resource
    public HttpServletRequest request;

    /**
     * Método para loggear el detalle de una excepción
     *
     * @param level El nivel de severidad de la excepción
     * @param exception La excepción a loggear
     */
    public void logException(Level level, Exception exception){
        ClientInfo originInfo = (ClientInfo) request.getAttribute("ORIGIN-INFO");
        JSONObject sessionInfo = (JSONObject) request.getAttribute("SESSION-INFO");

        String[] classSplit = exception.getClass().getName().split("\\.");
        String clazz = classSplit[classSplit.length-1];
        String messageLog;

        logger.log(level, SEPARADOR);

        messageLog = String.format("Transaction UUID: %s", originInfo.transactionUUID().toString());
        logger.log(level, messageLog);

        messageLog = String.format("Origen de la petición: Address: [%s] | Host: [%s]",
                originInfo.originAddress(), originInfo.originHost());
        logger.log(level, messageLog);

        if (sessionInfo != null) {
            logger.log(level, "Info del usuario:");

            Iterator<String> keys = sessionInfo.keys();
            while (keys.hasNext()) {
                String header = keys.next();
                messageLog = String.format("%s: [%s]", header, sessionInfo.get(header));
                logger.log(level, messageLog);
            }
        } else {
            logger.log(level, "Info del usuario: No se encontró ninguna sesión");
        }

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
        ClientInfo originInfo = (ClientInfo) request.getAttribute("ORIGIN-INFO");

        logger.info(SEPARADOR);
        logger.info("EVENTO");

        messageLog = String.format("Transaction UUID: %s", originInfo.transactionUUID().toString());
        logger.info( messageLog );

        messageLog = String.format("Origen de la petición: Address: [%s] | Host: [%s]",
                originInfo.originAddress(), originInfo.originHost());
        logger.info(messageLog);

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
        ClientInfo originInfo = (ClientInfo) request.getAttribute("ORIGIN-INFO");
        JSONObject sessionInfo = (JSONObject) request.getAttribute("SESSION-INFO");

        logger.info(SEPARADOR);

        logger.info("TRANSACCIÓN");
        messageLog = String.format("Transaction UUID: %s", originInfo.transactionUUID().toString());
        logger.info( messageLog );

        messageLog = String.format("Origen de la petición: Address: [%s] | Host: [%s]",
                originInfo.originAddress(), originInfo.originHost());
        logger.info(messageLog);

        if (sessionInfo != null) {
            logger.info("Info del usuario:");

            Iterator<String> keys = sessionInfo.keys();
            while (keys.hasNext()) {
                String header = keys.next();
                messageLog = String.format("%s: [%s]", header, sessionInfo.get(header));
                logger.info(messageLog);
            }
        } else {
            logger.info("Info del usuario: No se encontró ninguna sesión");
        }

        messageLog = String.format("Tipo: %s - Encabezado: %s", transactionType.getValue(), event.header());
        logger.info( messageLog );

        event.rows().forEach(logger::info);

        logger.info(SEPARADOR);
    }

    public LogConfig getLogConfig() {
        return logConfig;
    }
}
