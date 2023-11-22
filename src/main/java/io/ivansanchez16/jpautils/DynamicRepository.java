package io.ivansanchez16.jpautils;

import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.Map;

public interface DynamicRepository<T, K> {

    /**
     * Método que regresa la CriteriaQuery para generar una consulta con joins
     *
     * @return Un objeto de CriteriaQuery de tipo Tuple
     */
    CriteriaQuery<Tuple> generateQuery();

    /**
     * Método que consulta de manera dinámica una tabla (entidad) por N atributos
     *
     * @param params Un map donde las key son los nombres de los atributos y los values los valores para armar WHERE
     * @param clase La clase de la entidad la cual se está realizando la consulta
     * @param cQuery El objeto de CriteriaQuery con los joins
     * @param root El objeto de Root con los joins
     * @param keyClass La clase de la llave primaria de la entidad
     * @return Una página de la consulta ha realizar
     */
    PageQuery<T> queryByAttributes(Map<String, Object> params, CriteriaQuery<Tuple> cQuery, Root<T> root, Class<T> clase, Class<K> keyClass);

    /**
     * Método que consulta de manera dinámica una tabla (entidad) por N atributos
     *
     * @param params Un map donde las key son los nombres de los atributos y los values los valores para armar WHERE
     * @param clase La clase de la entidad la cual se está realizando la consulta
     * @param keyClass la clase de la llave primaria de la entidad
     * @return Una página de la consulta ha realizar
     */
    PageQuery<T> queryByAttributes(Map<String, Object> params, Class<T> clase, Class<K> keyClass);

    /**
     * Método que usa el persist del entityManager para evitar el select pre query del save
     *
     * @param object El objeto de la entidad a persistir
     * @return El objeto persistido
     */
    T grabaWithoutQuery(T object);
}
