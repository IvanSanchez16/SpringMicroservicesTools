package io.github.ivansanchez16.jpautils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
class WhereParam {

    private String path;
    private Field field;
    private Object value;
    private Operator operator;
}
