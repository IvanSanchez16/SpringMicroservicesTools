package io.github.ivansanchez16.apiresponses;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

/**
 * ApiResponseDTO
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({ "meta", "data" })
public class ApiBodyDTO<T> {

    private Meta meta;
    private T data;

    public ApiBodyDTO(Meta meta) {
        this.meta = meta;
    }
}
