package io.ivansanchez16.jpautils;

import jakarta.persistence.Tuple;
import lombok.experimental.UtilityClass;

import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.*;

// TODO - Definir todos los nombres de los atributos
@UtilityClass
class DynamicQueryUtil {

    private final String DELETED_AT = "deletedAt";

    <T> void addSortedValues(CriteriaBuilder cb, CriteriaQuery<Tuple> criteriaQuery, Root<T> root, Map<String, Object> params) {
        if (params.containsKey("sort_by")) {
            String[] att;

            final String sortBy = params.get("sort_by").toString();
            final List<String> listSortBy = List.of(sortBy.split(","));
            final List<Order> orders = new ArrayList<>(listSortBy.size());

            for (String attSortBy: listSortBy) {
                att = attSortBy.split("\\.");
                if (att.length == 0)
                    continue;

                if (att.length == 1 || att[1].equals("asc")) {
                    orders.add( cb.asc( root.get(att[0]) ) );
                } else if (att[1].equals("desc")){
                    orders.add( cb.desc( root.get(att[0]) ) );
                }
            }

            if (!orders.isEmpty()) {
                criteriaQuery.orderBy( orders );
            }
        }
    }

    void addPageableParams(Query query, Map<String, Object> params) throws NumberFormatException{
        final int pageSize;
        final int pageNo;

        if (params.containsKey("page_size") && params.containsKey("page")) {
            pageSize = Integer.parseInt( params.get("page_size").toString() );
            pageNo = Integer.parseInt( params.get("page").toString() );
        } else {
            pageNo = 1;
            pageSize = 10;
        }

        query.setFirstResult( (pageNo - 1) * pageSize );
        query.setMaxResults( pageSize );
    }

    Map<String, WhereParams> prepareWhereParams(Map<String, Object> original, Field[] classFields) {
        Map<String, WhereParams> preparedMap = new HashMap<>();

        original.forEach((attribute, value) -> {
            boolean flagNullable = false;
            boolean isNull = false;

            String newAttribute = attribute;

            // Para campos de fecha se quiere saber si son NULL
            if (attribute.contains("is-")) {
                newAttribute = attribute.replace("is-", "") + "At";
                flagNullable = true;
                isNull = Boolean.parseBoolean(value.toString());
            }

            if (newAttribute.contains("_") || newAttribute.contains("-")) {
                newAttribute = cleanAttribute(newAttribute);
            }

            final Field field = findByName(classFields, newAttribute);
            if (field == null) return;

            if (flagNullable){
                // Se manda null cuando se valida si es null o no el valor
                preparedMap.put(newAttribute, new WhereParams(field, isNull));
            } else if (value.toString().contains(",")) {
                List<Object> values = List.of(value.toString().split(","));
                values = listCastTo(field.getType(), values);

                preparedMap.put(newAttribute, new WhereParams(field, values));
            } else {
                preparedMap.put(newAttribute, new WhereParams(field, castTo(field.getType(), value)));
            }
        });

        // Si la tabla maneja SoftDelete y no se especificó una búsqueda, agrega al filtrado que no estén eliminados
        final Field field = findByName(classFields, DELETED_AT);
        if (field != null) {
            preparedMap.computeIfAbsent(DELETED_AT, key -> new WhereParams(field, false));
        }

        return preparedMap;
    }

    Predicate groupPredicateList(CriteriaBuilder cb, List<Predicate> predicateList) {
        final Predicate predicate = predicateList.remove(0);
        if (predicateList.isEmpty()) {
            return predicate;
        }

        return cb.and(predicate, groupPredicateList(cb, predicateList));
    }

    <T> Path<T> groupPathList(Path<T> root, List<String> pathList) {
        final String attribute = pathList.remove(0);
        if (pathList.isEmpty()) {
            return root.get(attribute);
        }

        return groupPathList(root.get(attribute), pathList);
    }

    private Field findByName(Field[] fields, String name) {
        String[] attributeList = name.split("\\.");
        String attributeName = attributeList[0];

        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase(attributeName)) {
                if (attributeList.length > 1) {
                    final String restOfString = name.substring( name.indexOf(attributeName)+attributeName.length()+1 );
                    return findByName(field.getType().getDeclaredFields(), restOfString);
                } else {
                    return field;
                }
            }
        }

        return null;
    }

    private String cleanAttribute(String oldAttribute) {
        final StringBuilder sb = new StringBuilder();
        boolean flag = false;

        for (int i = 0; i < oldAttribute.length(); i++) {
            if (oldAttribute.charAt(i) == '_' || oldAttribute.charAt(i) == '-') {
                flag = true;
                continue;
            }

            if (flag) {
                sb.append( (oldAttribute.charAt(i)+"").toUpperCase(Locale.ROOT) );
                flag = false;
            } else {
                sb.append( oldAttribute.charAt(i) );
            }
        }

        return sb.toString();
    }

    private <T> Object castTo(Class<T> clase, Object value) {
        final String[] nameClass = clase.getTypeName().split("\\.");
        final String sValue = value.toString();

        return switch (nameClass[nameClass.length-1]) {
            case "Boolean" -> Boolean.valueOf(sValue);
            case "Integer" -> Integer.parseInt(sValue);
            case "Short" -> Short.parseShort(sValue);
            case "Long" -> Long.parseLong(sValue);
            case "UUID" -> UUID.fromString(sValue);
            default -> sValue;
        };
    }

    private <T> List<Object> listCastTo(Class<T> clase, List<Object> values) {
        List<Object> newList = new ArrayList<>(values.size());

        for (final Object value : values) {
            newList.add(castTo(clase, value));
        }

        return newList;
    }
}
