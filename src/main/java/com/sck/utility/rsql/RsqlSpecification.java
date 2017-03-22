package com.sck.utility.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.Attribute;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by TEKKINCERS on 8/31/2015.
 */
public class RsqlSpecification<T> implements Specification<T> {

    // Initial vars
    private String property;
    private final ComparisonOperator operator;
    private final List<String> arguments;

    // Calculated vars
    private boolean entityCaseCorrected = false;
    private Field propertyField = null;
    private List<Object> convertedArguments = null;
    private Path path;

    public RsqlSpecification(String property, ComparisonOperator operator, List<String> arguments) {
        super();
        this.property = property;
        this.operator = operator;
        this.arguments = arguments;
    }

    @Override // NOTE - this will get called an additional time when using pageable
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        // If we run into any query issues a TurtleShellLampException (runtime) will be thrown ending the process

        // Check for nested entities and correct case where necessary
        if(!entityCaseCorrected) {
            correctEntityCase(root.getJavaType());
            entityCaseCorrected = true;
        }

        // Get the entity for the search
        buildPath(root);

        // Get info for property searching on
        if(propertyField == null) {
            findAndSetPropertyField();
        }

        // Attempt to convert the arguments
        if(convertedArguments == null) {
            convertArguments();
        }

        // If we've gotten this far build the query
        return buildPredicate(builder);
    }

    private void buildPath(Root<T> root) {
        if (property.contains(".")) {
            String[] propertyArray = property.split("\\.");
            // TODO - this needs some researching, pretty sloppy right now
            switch (propertyArray.length) {
                case 2:
                    // one level nested
                    if (root.getModel().getAttribute(propertyArray[0]).getPersistentAttributeType().equals(Attribute.PersistentAttributeType.ONE_TO_MANY) || root.getModel().getAttribute(propertyArray[0]).getPersistentAttributeType().equals(Attribute.PersistentAttributeType.MANY_TO_MANY)) {
                        path = root.join(propertyArray[0]);
                    } else {
                        path = root.get(propertyArray[0]);
                    }

                    break;
                case 3:
                    // two level nested
                    if (root.getModel().getAttribute(propertyArray[0]).getPersistentAttributeType().equals(Attribute.PersistentAttributeType.ONE_TO_MANY) || root.getModel().getAttribute(propertyArray[0]).getPersistentAttributeType().equals(Attribute.PersistentAttributeType.MANY_TO_MANY)) {
                        if (root.getModel().getAttribute(propertyArray[0]).getPersistentAttributeType().equals(Attribute.PersistentAttributeType.ONE_TO_MANY) || root.getModel().getAttribute(propertyArray[0]).getPersistentAttributeType().equals(Attribute.PersistentAttributeType.MANY_TO_MANY)) {
                            path = root.join(propertyArray[0]).join(propertyArray[1]);
                        } else {
                            path = root.join(propertyArray[0]).get(propertyArray[1]);
                        }
                    } else {
                        if (root.getModel().getAttribute(propertyArray[0]).getPersistentAttributeType().equals(Attribute.PersistentAttributeType.ONE_TO_MANY) || root.getModel().getAttribute(propertyArray[0]).getPersistentAttributeType().equals(Attribute.PersistentAttributeType.MANY_TO_MANY)) {
                            throw new TurtleShellLampException("We don't support that type of join currently.");
                        } else {
                            path = root.get(propertyArray[0]).get(propertyArray[1]);
                        }
                    }

                    break;
                default:
                    throw new TurtleShellLampException("We don't support a nested search more than two levels currently.");
            }

        } else {
            // Set the path (just the root since nothing is nested)
            path = root;
        }
    }

    private void correctEntityCase(Class rootType) {
        if (property.contains(".")) {
            String newProperty = "";
            String[] entityNames = this.property.split("\\.");
            Class currentType = rootType;
            for(int i=0; i<entityNames.length-1;) {
                boolean matchedField = false;
                for (Field field : currentType.getDeclaredFields()) {
                    if (entityNames[i].equalsIgnoreCase(field.getName())) {
                        newProperty += field.getName()+".";
                        if(Collection.class.isAssignableFrom(field.getType())) {
                            currentType = (Class)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
                        }else {
                            currentType = field.getType();
                        }
                        matchedField = true;
                        break;
                    }
                }

                if(!matchedField) {
                    throw new TurtleShellLampException("Field "+entityNames[i]+" did not match any field on "+currentType.getName());
                }
                i++;
            }
            newProperty += entityNames[entityNames.length-1];
            property = newProperty;
        }
    }

    private void findAndSetPropertyField() {
        if (property.contains(".")) {
            String property = this.property.split("\\.")[this.property.split("\\.").length-1];
            for (Field field : path.getJavaType().getDeclaredFields()) {
                if (property.equalsIgnoreCase(field.getName())) {
                    propertyField = field;
                    return;
                }
            }
        }else {
            for (Field field : path.getJavaType().getDeclaredFields()) {
                if (this.property.equalsIgnoreCase(field.getName())) {
                    propertyField = field;
                    return;
                }
            }
        }

        throw new TurtleShellLampException("Field "+property+" did not match any field on "+path.getJavaType().getName());
    }

    private void convertArguments() {
        convertedArguments = new ArrayList<>();
        // Case arguments based on property type
        for (String argument : arguments) {

            if (propertyField.getType().equals(String.class)) {
                convertedArguments.add(String.valueOf(argument));
            } else if (propertyField.getType().equals(Integer.class)) {
                convertedArguments.add(Integer.parseInt(argument));
            } else if (propertyField.getType().equals(Long.class)) {
                convertedArguments.add(Long.parseLong(argument));
            } else if (propertyField.getType().equals(Boolean.class)) {
                convertedArguments.add(Boolean.parseBoolean(argument));
            } else if (propertyField.getType().equals(Date.class)) {
                // User passed in milliseconds?
                try {
                    convertedArguments.add(new Date(Long.parseLong(argument)));

                } catch (NumberFormatException e) {
                    // User passed date string?
                    // What format are we looking for?
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        convertedArguments.add(dateFormat.parse(argument));
                    } catch (ParseException x) {
                        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            convertedArguments.add(dateFormat.parse(argument));
                        } catch (ParseException x1) {
                            dateFormat = new SimpleDateFormat("HH:mm:ss");
                            try {
                                convertedArguments.add(dateFormat.parse(argument));
                            } catch (ParseException x2) {
                                convertedArguments.add(null);
                            }
                        }
                    }
                }

            } else {
                // Haven't accounted for this argument type yet...
                throw new TurtleShellLampException(propertyField.getType().getName() + ", " + propertyField.getType() + " on " + propertyField.getType().getDeclaringClass());
            }
        }
    }

    private Predicate buildPredicate(CriteriaBuilder builder) {

        switch (RsqlSearchOperation.getSimpleOperator(operator)) {

            case EQUAL: {
                if (convertedArguments.get(0) == null) {
                    return builder.isNull(path.get(propertyField.getName()));

                } else if (convertedArguments.get(0) instanceof String) {
                    if (((String) convertedArguments.get(0)).contains("*")) {
                        return builder.like(path.<String>get(propertyField.getName()), convertedArguments.get(0).toString().replace('*', '%'));

                    } else if (((String) convertedArguments.get(0)).isEmpty()) {
                        return builder.isNull(path.get(propertyField.getName()));

                    } else {
                        return builder.equal(path.get(propertyField.getName()), convertedArguments.get(0));

                    }

                } else {
                    return builder.equal(path.get(propertyField.getName()), convertedArguments.get(0));

                }
            }

            case NOT_EQUAL: {
                if (convertedArguments.get(0) == null) {
                    return builder.isNotNull(path.get(propertyField.getName()));

                } else if (convertedArguments.get(0) instanceof String) {
                    if (((String) convertedArguments.get(0)).contains("*")) {
                        return builder.notLike(path.<String>get(propertyField.getName()), convertedArguments.get(0).toString().replace('*', '%'));

                    } else if (((String) convertedArguments.get(0)).isEmpty()) {
                        return builder.isNotNull(path.get(propertyField.getName()));

                    } else {
                        return builder.notEqual(path.get(propertyField.getName()), convertedArguments.get(0));

                    }
                } else {
                    return builder.notEqual(path.get(propertyField.getName()), convertedArguments.get(0));

                }
            }

            // These require comparisons and therefore will need some type checks
            case GREATER_THAN: {
                if (convertedArguments.get(0) instanceof String) {
                    return builder.greaterThan(path.<String>get(propertyField.getName()), (String) convertedArguments.get(0));

                } else if (convertedArguments.get(0) instanceof Date) {
                    return builder.greaterThan(path.<Date>get(propertyField.getName()), (Date) convertedArguments.get(0));

                } else if (convertedArguments.get(0) instanceof Long) {
                    return builder.greaterThan(path.<Long>get(propertyField.getName()), (Long) convertedArguments.get(0));

                } else if (convertedArguments.get(0) instanceof Integer) {
                    return builder.greaterThan(path.<Integer>get(propertyField.getName()), (Integer) convertedArguments.get(0));

                } else if (convertedArguments.get(0) instanceof Boolean) {
                    return builder.greaterThan(path.<Boolean>get(propertyField.getName()), (Boolean) convertedArguments.get(0));

                }
            }

            case GREATER_THAN_OR_EQUAL: {
                if (convertedArguments.get(0) instanceof String) {
                    return builder.greaterThanOrEqualTo(path.<String>get(propertyField.getName()), (String) convertedArguments.get(0));

                } else if (convertedArguments.get(0) instanceof Date) {
                    return builder.greaterThanOrEqualTo(path.<Date>get(propertyField.getName()), (Date) convertedArguments.get(0));

                } else if (convertedArguments.get(0) instanceof Long) {
                    return builder.greaterThanOrEqualTo(path.<Long>get(propertyField.getName()), (Long) convertedArguments.get(0));

                } else if (convertedArguments.get(0) instanceof Integer) {
                    return builder.greaterThanOrEqualTo(path.<Integer>get(propertyField.getName()), (Integer) convertedArguments.get(0));

                } else if (convertedArguments.get(0) instanceof Boolean) {
                    return builder.greaterThanOrEqualTo(path.<Boolean>get(propertyField.getName()), (Boolean) convertedArguments.get(0));

                }
            }

            case LESS_THAN: {
                if (convertedArguments.get(0) instanceof String) {
                    return builder.lessThan(path.<String>get(propertyField.getName()), (String) convertedArguments.get(0));

                } else if (convertedArguments.get(0) instanceof Date) {
                    return builder.lessThan(path.<Date>get(propertyField.getName()), (Date) convertedArguments.get(0));

                } else if (convertedArguments.get(0) instanceof Long) {
                    return builder.lessThan(path.<Long>get(propertyField.getName()), (Long) convertedArguments.get(0));

                } else if (convertedArguments.get(0) instanceof Integer) {
                    return builder.lessThan(path.<Integer>get(propertyField.getName()), (Integer) convertedArguments.get(0));

                } else if (convertedArguments.get(0) instanceof Boolean) {
                    return builder.lessThan(path.<Boolean>get(propertyField.getName()), (Boolean) convertedArguments.get(0));

                }
            }

            case LESS_THAN_OR_EQUAL: {
                if (convertedArguments.get(0) instanceof String) {
                    return builder.lessThanOrEqualTo(path.<String>get(propertyField.getName()), (String) convertedArguments.get(0));

                } else if (convertedArguments.get(0) instanceof Date) {
                    return builder.lessThanOrEqualTo(path.<Date>get(propertyField.getName()), (Date) convertedArguments.get(0));

                } else if (convertedArguments.get(0) instanceof Long) {
                    return builder.lessThanOrEqualTo(path.<Long>get(propertyField.getName()), (Long) convertedArguments.get(0));

                } else if (convertedArguments.get(0) instanceof Integer) {
                    return builder.lessThanOrEqualTo(path.<Integer>get(propertyField.getName()), (Integer) convertedArguments.get(0));

                } else if (convertedArguments.get(0) instanceof Boolean) {
                    return builder.lessThanOrEqualTo(path.<Boolean>get(propertyField.getName()), (Boolean) convertedArguments.get(0));

                }
            }

            case IN:
                return path.get(propertyField.getName()).in(convertedArguments);

            case NOT_IN:
                return builder.not(path.get(propertyField.getName()).in(convertedArguments));
        }

        return null;
    }

}