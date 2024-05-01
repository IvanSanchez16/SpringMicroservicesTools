package io.ivansanchez16.apiresponses.webclient;

import io.ivansanchez16.apiresponses.ApiBodyDTO;
import io.ivansanchez16.jpautils.PageQuery;
import org.springframework.http.client.MultipartBodyBuilder;

import java.util.List;

public interface Request {

    /**
     * Método para agregar un header a la petición
     *
     * @param headerName El nombre del header a agregar
     * @param headerValue El valor del header a agregar
     * @return El mismo objeto de petición con los valores asignados
     */
    Request addHeader(String headerName, String headerValue);

    /**
     * Método para agregar body a la petición. Siempre será con un Media type application.json
     * A menos que se haya llamado el método multipartBody previamente
     *
     * @param requestBody El objeto a agregarse como body de la petición
     * @return El mismo objeto de petición con los valores asignados
     */
    Request addBody(Object requestBody);

    /**
     * Método para agregar body a la petición. A diferencia del método addBody
     * El Media type para estas peticiones será Multipart form data
     *
     * @param builder Objeto de MultipartBodyBuilder para agregarse al body de la petición
     * @return el mismo objeto de petición con los valores asignados
     */
    Request multipartBody(MultipartBodyBuilder builder);

    /**
     * Método para ejecutar la petición y recibir la respuesta del data como un objeto
     *
     * @param clazz Objeto class de la clase para recibir el response
     * @param <T> La clase a la cual se castea el valor dentro de data
     * @return Objeto ApiBody con la respuesta del servicio
     */
    <T> ApiBodyDTO<T> objectResponse(Class<T> clazz);

    /**
     * Método para ejecutar la petición y recibar la respuesta del data como una lista
     * del objeto indicado
     *
     * @param clazz Objeto class de la clase para recibir el response
     * @param <T> La clase a la cual se castea el valor de cada elemento de la lista dentro de data
     * @return Objeto ApiBody con la respuesta del servicio
     */
    <T> ApiBodyDTO<List<T>> listResponse(Class<T> clazz);

    /**
     * Método para ejecutar la petición y recibir la respuesta del data como un objeto paginado
     * del objeto indicado
     *
     * @param clazz Objeto class de la clase para recibir el response
     * @param <T> La clase a la cual se castea el valor de cada elemento de la lista dentro de data
     * @return Objeto ApiBody con la respuesta del servicio
     */
    <T> ApiBodyDTO<PageQuery<T>> pageableResponse(Class<T> clazz);

    /**
     * Método para ejecutar la petición y no espera nada dentro del atributo data
     *
     * @return Objeto ApiBody con la respuesta del servicio
     */
    ApiBodyDTO<Void> noDataResponse();

    /**
     * Método para ejecutar la petición y recibir la respuesta como string.
     * Util cuando la respuesta del servicio no sea con el esquema del objeto ApiBody
     *
     * @return String con la respuesta de la petición
     */
    String rawResponse();

    /**
     * Método para ejecutar la petición pero la respuesta de la misma no se requiere.
     * Util para peticiones de las cuales solamente se quiere conocer el http status.
     *
     * Si se tiene configurado que las excepciones no se arrojen no se conocerá el status
     * de la petición en caso de utilizar este método
     */
    void ignoreResponse();
}
