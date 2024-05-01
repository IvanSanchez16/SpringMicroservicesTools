package io.ivansanchez16.generalutilery;

import io.ivansanchez16.apiresponses.Meta;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

@UtilityClass
public class LogFile {

    private final Logger LOGGER = LogManager.getLogger(LogFile.class.getName());

    private final String SEPARADOR = "--------------------------------------------------";

    private static String projectGroup = "";

    /**
     * Método para loggear el detalle de una excepción
     *
     * @param exception La excepción a loggear
     */
    public void logException(Exception exception){
        String[] classSplit = exception.getClass().getName().split("\\.");
        String clase = classSplit[classSplit.length-1];
        String mensajeLog;

        LOGGER.error(SEPARADOR);
        mensajeLog = String.format("%s: %s", clase,exception.getMessage());
        LOGGER.error(mensajeLog);
        // Obtener origen del error
        StackTraceElement[] st =
                Arrays.stream(exception.getStackTrace())
                        .filter(stackTraceElement -> stackTraceElement.getClassName().startsWith( projectGroup ))
                        .toArray(StackTraceElement[]::new);

        StackTraceElement ste;
        int stackTraceCount = Math.min(st.length, 3);
        LOGGER.error("Stack trace exception origin");
        for (int i = 0; i < stackTraceCount; i++) {
            ste = st[i];
            mensajeLog = String.format("%d: [Archivo: %s | Metodo: %s | Linea: %d]", i+1, ste.getFileName(), ste.getMethodName(), ste.getLineNumber());
            LOGGER.error(mensajeLog);
        }
        LOGGER.error(SEPARADOR);
    }

    /*
     * Método para grabar en log algún error sin necesidad de una excepción
     */
    public void logError(String error){
        LOGGER.error(SEPARADOR);
        LOGGER.error(error);
        LOGGER.error(SEPARADOR);
    }

    public void logWarn(String warn) {
       LOGGER.warn(warn);
    }

    /*
     * Método para grabar en log los datos de un objeto META
     */
    public void logMeta(Meta meta){
        String mensajeLog;

        LOGGER.info(SEPARADOR);

        LOGGER.info("Peticion");
        mensajeLog = String.format("ID de la peticion: %s", meta.getTransactionID());
        LOGGER.info(mensajeLog);
        mensajeLog = String.format("El servicio respondio con un status: %d | %s", meta.getStatusCode(), meta.getStatus());
        LOGGER.info(mensajeLog);
        mensajeLog = String.format("Fecha y hora de peticion: %s", meta.getTimestamp());
        LOGGER.info(mensajeLog);

        if (meta.getMessage() != null && !meta.getMessage().equals("")){
            mensajeLog = String.format("Mensaje: %s", meta.getMessage());
            LOGGER.info(mensajeLog);
        }
        if (meta.getDevMessage() != null && !meta.getDevMessage().equals("")){
            mensajeLog = String.format("Detalle: %s", meta.getDevMessage());
            LOGGER.info(mensajeLog);
        }

        LOGGER.info(SEPARADOR);
    }

    /*
     * Método para grabar en log cuando se manda a llamar el servicio de manera incorrecta
     */
    public void logBadRequest(String clase, String metodo, String identificador, String mensaje) {
        String mensajeLog;
        LOGGER.warn(SEPARADOR);

        LOGGER.warn("Peticion no exitosa.");
        LOGGER.warn("Razon:");
        mensajeLog = String.format("Clase: %s | Metodo: %s | Id: %s", clase, metodo, identificador);
        LOGGER.warn(mensajeLog);
        mensajeLog = String.format("Detalle: %s", mensaje);
        LOGGER.warn(mensajeLog);

        LOGGER.warn(SEPARADOR);
    }

    public static String getProjectGroup() {
        return projectGroup;
    }

    public static void setProjectGroup(String projectGroup) {
        LogFile.projectGroup = projectGroup;
    }
}
