package io.github.ivansanchez16.apiresponses;

import lombok.*;

/**
 * ApiResponseDTO
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApiBodyDTO<T> {

    private Meta meta;
    private T data;

    public ApiBodyDTO(Meta meta) {
        this.meta = meta;
    }
}
