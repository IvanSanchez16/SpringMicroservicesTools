package io.ivansanchez16.jpautils;

import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.Map;

public interface DynamicRepository<T, K> {

    /**
     * Method for generate a CriteriaQuery to use on queryByAttributes
     *
     * @return A CriteriaQuery object created with entityManager
     */
    CriteriaQuery<Tuple> generateQuery();

    /**
     * Generate a query based on a Map. Takes the Map keys to add WHERE, ORDER BY and Pageable options to the query
     *
     * @param params A Map object to get the WHERE params, ORDER BY attributes and Pagination configuration
     * @param clazz The class object of entity (T)
     * @param cQuery The CriteriaQuery object previous generated
     * @param root The Root object with JOINS
     * @param keyClass The class object of entity key (K)
     *
     * @throws InvalidValueException When the values for attributes provided to construct the query cannot be cast
     * to their value
     * @return A PageQuery object with the query result
     */
    PageQuery<T> queryByAttributes(Map<String, Object> params, CriteriaQuery<Tuple> cQuery, Root<T> root, Class<T> clazz, Class<K> keyClass);

    /**
     * Generate a query based on a Map. Takes the Map keys to add WHERE, ORDER BY and Pageable options to the query
     *
     * @param params A Map object to get the WHERE params, ORDER BY attributes and Pagination configuration
     * @param clazz The class object of entity (T)
     * @param keyClass The class object of entity key (K)
     *
     * @throws InvalidValueException When the values for attributes provided to construct the query cannot be cast
     * to their value
     * @return A PageQuery object with the query result
     */
    PageQuery<T> queryByAttributes(Map<String, Object> params, Class<T> clazz, Class<K> keyClass);

    /**
     * Use entityManager persist method to persist an entity without select query
     *
     * @param object The entity object to persist
     * @return The same object
     */
    T grabaWithoutQuery(T object);
}
