package io.github.ivansanchez16.jpautils;

import io.github.ivansanchez16.jpautils.annotations.ExtractAttributes;
import io.github.ivansanchez16.jpautils.annotations.UseSoftDelete;
import jakarta.persistence.Embedded;
import jakarta.persistence.Tuple;
import lombok.experimental.UtilityClass;

import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;


@UtilityClass
class DynamicQueryUtil {

    private final String COMPARATOR_REGEX = "^[a-zA-z_\\-]+-(gt|gte|eq|lt|lte)$";
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

    List<ClassField> getFields(String prefix, Class<?> clazz) {
        final Field[] baseFields = clazz.getDeclaredFields();
        final List<ClassField> finalFields = new ArrayList<>(baseFields.length + 10);

        for (Field field : baseFields) {
            if (field.isAnnotationPresent(Embedded.class) || field.isAnnotationPresent(ExtractAttributes.class)) {
                String newPrefix = String.format("%s%s.", prefix, field.getName());
                finalFields.addAll( getFields( newPrefix, field.getType() ) );
                continue;
            }

            finalFields.add(new ClassField(String.format("%s%s", prefix, field.getName()), field));
        }

        return finalFields;
    }

    Map<String, WhereParam> prepareWhereParams(Map<String, Object> original, Class<?> clazz) {
        final List<ClassField> classFields = getFields("", clazz);
        Map<String, WhereParam> preparedMap = new HashMap<>();

        original.forEach((attribute, value) -> {
            Operator operator = null;
            boolean flagNullable = false;
            boolean isNull = false;

            String newAttribute = attribute;

            if (Pattern.matches(COMPARATOR_REGEX, newAttribute)) {
                String[] tokens = newAttribute.split("-");
                String operatorToken = tokens[tokens.length-1];

                operator = Operator.getByPostfix( operatorToken );
                newAttribute = newAttribute.substring(0, newAttribute.length() - operatorToken.length() - 1);
            }

            if (operator == null) {
                operator = Operator.EQUAL;
            }

            // If you ask with 'is-' to a date attribute, validate if it's null or not
            if (attribute.contains("is-")) {
                newAttribute = attribute.replace("is-", "") + "At";
                flagNullable = true;
                isNull = Boolean.parseBoolean(value.toString());
            }

            if (newAttribute.contains("_") || newAttribute.contains("-")) {
                newAttribute = cleanAttribute(newAttribute);
            }

            final ClassField field = findByName(classFields, newAttribute);
            if (field == null) return;

            if (flagNullable){
                // Validation for if date is null
                preparedMap.put(newAttribute, new WhereParam(field.getPath(), field.getField(), isNull, operator));
            } else if (value.toString().contains(",")) {
                // Validation for multiple values separates with commas
                try {
                    List<Object> values = List.of(value.toString().split(","));
                    values = listCastTo(field.getField().getType(), values);

                    preparedMap.put(newAttribute, new WhereParam(field.getPath(), field.getField(), values, operator));
                } catch (Exception e) {
                    throw new InvalidValueException(
                            String.format("One of the values for %s cannot be converted to the attribute type", attribute),
                            field.getField().getName(),
                            value.toString(),
                            attribute);
                }
            } else {
                // Standard validation
                try {
                    preparedMap.put(newAttribute, new WhereParam(field.getPath(), field.getField(), castTo(field.getField().getType(), value), operator));
                } catch (Exception e) {
                    throw new InvalidValueException(
                            String.format("The provided value for %s cannot be converted to the attribute type", attribute),
                            field.getField().getName(),
                            value.toString(),
                            attribute);
                }
            }
        });

        // If the table contains UseSoftDelete annotation includes in where params this attribute needs to be NULL
        if (clazz.isAnnotationPresent(UseSoftDelete.class)) {
            UseSoftDelete annotation = clazz.getAnnotation(UseSoftDelete.class);
            final ClassField field = findByName(classFields, annotation.attribute());
            if (field != null) {
                preparedMap.computeIfAbsent(DELETED_AT, key -> new WhereParam(field.getPath(), field.getField(), false, Operator.EQUAL));
            }
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

    Path<?> groupPathList(Path<?> root, List<String> pathList) {
        final String attribute = pathList.remove(0);
        if (pathList.isEmpty()) {
            return root.get(attribute);
        }

        return groupPathList(root.get(attribute), pathList);
    }

    private ClassField findByName(List<ClassField> fields, String name) {
        String[] attributeList = name.split("\\.");
        String attributeName = attributeList[0];

        for (ClassField field : fields) {
            if (field.getField().getName().equalsIgnoreCase(attributeName)) {
                if (attributeList.length > 1) {
                    final String restOfString = name.substring( name.indexOf(attributeName)+attributeName.length()+1 );
                    return findByName( getFields(attributeName + ".", field.getField().getType()) , restOfString);
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
