package io.ivansanchez16.apiresponses.webclient;

import io.ivansanchez16.apiresponses.ApiBodyDTO;
import io.ivansanchez16.jpautils.PageQuery;
import org.springframework.http.client.MultipartBodyBuilder;

import java.util.List;

public interface Request {

    Request addHeader(String headerName, String headerValue);

    Request addBody(Object requestBody);

    Request multipartBody(MultipartBodyBuilder builder);

    <T> ApiBodyDTO<T> objectResponse(Class<T> clazz);

    <T> ApiBodyDTO<List<T>> listResponse(Class<T> clazz);

    <T> ApiBodyDTO<PageQuery<T>> pageableResponse(Class<T> clazz);

    ApiBodyDTO<Void> noDataResponse();

    String rawResponse();

    void ignoreResponse();
}
