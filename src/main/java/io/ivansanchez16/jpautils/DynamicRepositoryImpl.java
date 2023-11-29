package io.ivansanchez16.jpautils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.metamodel.SingularAttribute;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;


public class DynamicRepositoryImpl<T, K> implements DynamicRepository<T, K> {

    private final CriteriaBuilder cb;

    private final EntityManager entityManager;

    public DynamicRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.cb = entityManager.getCriteriaBuilder();
    }

    @Override
    public T grabaWithoutQuery(T object) {
        entityManager.persist(object);
        return object;
    }

    @Override
    public CriteriaQuery<Tuple> generateQuery() {
        return cb.createTupleQuery();
    }

    @Override
    public PageQuery<T> queryByAttributes(Map<String, Object> params, CriteriaQuery<Tuple> cQuery, Root<T> root,
                                          Class<T> clazz, Class<K> keyClass)
    {
        Map<String, WhereParams> whereParams = DynamicQueryUtil.prepareWhereParams(params, clazz.getDeclaredFields());
        final List<K> firstElementsKeys = findFirstElements(whereParams, params, clazz, keyClass);
        if (firstElementsKeys.isEmpty()) {
            return new PageQuery<>(0L, new ArrayList<>());
        }

        cQuery.select( cb.tuple(root) );
        DynamicQueryUtil.addSortedValues(cb, cQuery, root, params);

        final List<T> finalRows = findElementsWithJoin(firstElementsKeys, clazz, cQuery, root);

        return new PageQuery<>(countElements(clazz, whereParams), finalRows.stream().distinct().toList());
    }

    @Override
    public PageQuery<T> queryByAttributes(Map<String, Object> params, Class<T> clazz, Class<K> keyClass) {
        CriteriaQuery<Tuple> cQuery = cb.createTupleQuery();
        Root<T> root = cQuery.from(clazz);

        return queryByAttributes(params, cQuery, root, clazz, keyClass);
    }

    @SuppressWarnings("unchecked")
    private List<K> findFirstElements(Map<String, WhereParams> whereParams, Map<String, Object> params, Class<T> clase, Class<K> keyClass) {
        CriteriaQuery<Tuple> cQuery = cb.createTupleQuery();
        Root<T> root = cQuery.from(clase);
        cQuery.select( cb.tuple( root.get(getIdProperty(clase)) ) );

        DynamicQueryUtil.addSortedValues(cb, cQuery, root, params);

        if (whereParams.isEmpty()) {
            Query query = entityManager.createQuery(cQuery);

            DynamicQueryUtil.addPageableParams(query, params);

            List<Tuple> resultTuples = query.getResultList();
            return resultTuples.stream()
                    .map(tuple -> tuple.get(0, keyClass))
                    .toList();
        }

        List<Predicate> predicates = getPredicates(root, whereParams);

        cQuery.where( DynamicQueryUtil.groupPredicateList(cb, predicates) );

        Query query = entityManager.createQuery(cQuery);

        DynamicQueryUtil.addPageableParams(query, params);

        List<Tuple> resultTuples = query.getResultList();
        return resultTuples.stream()
                .map(tuple -> tuple.get(0, keyClass))
                .toList();
    }

    private Long countElements(Class<T> clase, Map<String, WhereParams> whereParams) {
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<T> root = cQuery.from(clase);
        cQuery.select( cb.count(root) );

        List<Predicate> predicates = getPredicates(root, whereParams);
        if (!predicates.isEmpty()) {
            cQuery.where( DynamicQueryUtil.groupPredicateList(cb, predicates) );
        }

        return entityManager.createQuery(cQuery).getSingleResult();
    }

    @SuppressWarnings("unchecked")
    private List<Predicate> getPredicates(Path<T> root, Map<String, WhereParams> whereParams) {
        List<Predicate> predicates = new ArrayList<>();

        whereParams.forEach((attribute, whereParam) -> {
            String[] paths = attribute.split("\\.");
            Path<T> path = DynamicQueryUtil.groupPathList( root, new ArrayList<>(List.of(paths)) );

            if (whereParam.getField().getType().equals(LocalDateTime.class)) {
                Path<LocalDateTime> dateTimePath = (Path<LocalDateTime>) path;

                // If the attribute is for date there is two ways
                if (whereParam.getValue() instanceof Boolean) {
                    // Validate for null date columns
                    if (Boolean.TRUE.equals( whereParam.getValue() )) {
                        predicates.add( cb.isNotNull(path) );
                    } else {
                        predicates.add( cb.isNull(path) );
                    }
                    return;
                } else {
                    // Validation for date range
                    final String strBeginOfDay = String.format("%sT00:00:00", whereParam.getValue());
                    final String strEndOfDay = String.format("%sT23:59:59", whereParam.getValue());

                    final LocalDateTime beginOfDay = LocalDateTime.parse(strBeginOfDay);
                    final LocalDateTime endOfDay = LocalDateTime.parse(strEndOfDay);

                    predicates.add( cb.between(dateTimePath, beginOfDay, endOfDay) );
                    return;
                }
            }

            if (whereParam.getValue() instanceof List) {
                CriteriaBuilder.In<Object> corePredicateIn = cb.in( path );
                ((List<Object>) whereParam.getValue()).forEach(corePredicateIn::value);

                predicates.add(corePredicateIn);
            } else {
                predicates.add( cb.equal(path , whereParam.getValue()) );
            }
        });

        return predicates;
    }

    @SuppressWarnings("unchecked")
    private List<T> findElementsWithJoin(List<K> listOfKey, Class<T> clase, CriteriaQuery<Tuple> cQuery, Root<T> root) {
        CriteriaBuilder.In<Object> corePredicateIn = cb.in( root.get(getIdProperty(clase)) );
        listOfKey.forEach(corePredicateIn::value);
        cQuery.where(corePredicateIn);

        Query query = entityManager.createQuery(cQuery);

        List<Tuple> tupleList = query.getResultList();
        return tupleList.stream()
                .map(tuple -> tuple.get(0, clase))
                .toList();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private String getIdProperty(Class entityClass) {
        String idProperty = null;
        Metamodel metamodel = entityManager.getMetamodel();
        EntityType entity = metamodel.entity(entityClass);

        Set<SingularAttribute> singularAttributes = entity.getSingularAttributes();
        for (SingularAttribute singularAttribute : singularAttributes) {
            if (singularAttribute.isId()){
                idProperty=singularAttribute.getName();
                break;
            }
        }

        if(idProperty==null)
            throw new NullPointerException("No se encontró el Id de la entidad");

        return idProperty;
    }
}