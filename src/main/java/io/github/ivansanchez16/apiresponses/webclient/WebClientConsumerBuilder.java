package io.github.ivansanchez16.apiresponses.webclient;

import io.github.ivansanchez16.logger.LogMethods;
import org.springframework.web.reactive.function.client.WebClient;

public interface WebClientConsumerBuilder {

    /**
     * Agrega el objeto WebClient al builder.
     *
     * Este paso es obligatorio para generar una instancia de WebClientConsumer.
     *
     * @param webClient el objeto configurado para realizar las peticiones
     * @return El mismo objeto builder
     */
    WebClientConsumerBuilder webClient(WebClient webClient);

    /**
     * Método para agregar algún header al WebClientConsumer.
     * Util cuando se desea agregar un header a todas las peticiones que se realicen
     * a través de la instancia de WebClientConsumer a generar.
     *
     * @param headerName El nombre del header a agregar
     * @param headerValue El valor del header a agregar
     * @return El mismo objeto builder
     */
    WebClientConsumerBuilder addDefaultHeader(String headerName, String headerValue);

    /**
     * Método para configurar el manejo de las excepciones cuando las peticiones
     * realizadas a través del objeto a generar.
     * <p>
     * Si se activa entonces la excepción {@link org.springframework.web.reactive.function.client.WebClientResponseException}
     * será arrojada y será responsabilidad de quién utilice la librería para manejar
     * la excepción.
     * <p>
     * La excepción {@link org.springframework.web.reactive.function.client.WebClientRequestException} siempre
     * es retornada dentro de esta librería.
     * <p>
     * El valor default es true.
     *
     * @param flag El valor a asignar a esta opción
     * @return el mismo objeto builder
     */
    WebClientConsumerBuilder throwWebClientExceptions(boolean flag);

    /**
     * Método para configurar el log de los errores que ocurran por el consumo
     * de servicios a través del objeto a generar.
     * <p>
     * Si se activa se loggeará información acerca de la petición en caso de ser fallida,
     * es decir la respuesta de un http status mayor a 400.
     * <p>
     * @param logMethods El componente para el logging de los errores
     * @return el mismo objeto builder
     */
    WebClientConsumerBuilder logErrors(LogMethods logMethods);

    /**
     * Método para generar el objeto WebClientConsumer.
     *
     * @return El objeto de WebClientConsumer generado
     */
    WebClientConsumer build();
}
