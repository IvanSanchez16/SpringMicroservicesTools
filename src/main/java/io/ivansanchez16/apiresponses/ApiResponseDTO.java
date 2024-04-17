package io.ivansanchez16.apiresponses;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class ApiResponseDTO<T> extends ResponseEntity<ApiBodyDTO<T>> {

    public ApiResponseDTO(Meta meta, T data) {
        super(new ApiBodyDTO<>(meta, data), HttpStatusCode.valueOf( meta.getStatusCode() ));
    }

    public ApiResponseDTO(Meta meta) {
        super(new ApiBodyDTO<>(meta, null), HttpStatusCode.valueOf( meta.getStatusCode() ));
    }
}
