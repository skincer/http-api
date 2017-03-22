package com.sck.utility.web;

import com.sck.domain.RestInitializable;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by TEKKINCERS on 8/7/2015.
 */
public class ControllerUtility {

    public static boolean foreignKeyToEntityTranslation(RestInitializable postedObject) {

        if(postedObject.getForeignKeys() == null) {
            return false;
        }

        Iterator it = postedObject.getForeignKeys().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            //System.out.println(pair.getKey() + " = " + pair.getValue());

            // Class name of foreign key
            String foreignClass = pair.getValue().toString();

            // Format string to match declared method
            String foreignKey = pair.getKey().toString();
            foreignKey = foreignKey.substring(0, 1).toUpperCase() + foreignKey.substring(1);
            foreignKey = foreignKey.substring(0, foreignKey.length() - 1) + foreignKey.substring(foreignKey.length()-1).toLowerCase();

            // Try to retrieve the proper get method
            Method method = null;
            try {
                //System.out.println(postedObject.getClass().getMethod("get"+foreignKey).getName());
                method = postedObject.getClass().getMethod("get"+foreignKey);
            }catch (NoSuchMethodException e) {
                //System.out.println("get"+foreignKey+" does not exist!");
                continue;
            }

            // Use the get method to retrieve passed value
            Long returnedId = null;
            if(method != null) {
                try {
                    if(method.invoke(postedObject) == null) {
                        //System.out.println("get"+foreignKey+" is NULL");
                    }else {
                        //System.out.println(method.invoke(postedObject));
                        returnedId = (long)method.invoke(postedObject);
                    }
                }catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
                    //System.out.println("get"+foreignKey+" failed!");
                    continue;
                }
            }else {
                continue;
            }

            // If a value was passed create object
            RestInitializable relatedEntity = null;
            if(returnedId != null) {
                try {
                    relatedEntity = (RestInitializable)Class.forName(foreignClass).newInstance();
                }catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                    //System.out.println("Class "+foreignClass+" failed!");
                    continue;
                }

            }else {
                continue;
            }

            relatedEntity.setId(returnedId);

            // If created object, find setter for it
            method = null;
            try {
                if(postedObject.getClass().getMethod("set"+relatedEntity.getClass().getSimpleName(),relatedEntity.getClass()) == null) {
                    //System.out.println("set"+relatedEntity.getClass().getSimpleName()+" is null?");
                }else {
                    //System.out.println(postedObject.getClass().getMethod("set"+relatedEntity.getClass().getSimpleName(),relatedEntity.getClass()).getName());
                    method = postedObject.getClass().getMethod("set"+relatedEntity.getClass().getSimpleName(),relatedEntity.getClass());
                }
            }catch (NoSuchMethodException e) {
                //System.out.println("set"+relatedEntity.getClass().getSimpleName()+" does not exist!");
                continue;
            }

            // Setter found invoke it
            if(method != null) {
                try {
                    method.invoke(postedObject, relatedEntity);
                }catch (IllegalAccessException | InvocationTargetException e) {
                    //System.out.println(method.getName()+" failed!");
                    continue;
                }
            }

            it.remove(); // avoids a ConcurrentModificationException
        }

        return true;
    }


    public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());

            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }else if(srcValue.getClass().getSimpleName().equals("Integer")) {
                if(srcValue.equals(0)) {
                    src.setPropertyValue(pd.getName(), null);
                }
            }else if(srcValue.getClass().getSimpleName().equals("Long")) {
                if(srcValue.equals(0L)) {
                    src.setPropertyValue(pd.getName(), null);
                }
            }else if(srcValue.getClass().getSimpleName().equals("String")) {
                if(srcValue.equals("0")) {
                    src.setPropertyValue(pd.getName(), null);
                }
            }else if(srcValue.getClass().getSimpleName().equals("Date")) {
                if(((Date)srcValue).getTime() == -62170138800000L) {
                    src.setPropertyValue(pd.getName(), null);
                }
            }
        }

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    // then use Spring BeanUtils to copy and ignore null
    public static void copyProperties(Object src, Object target, boolean ignoreNull) {
        if(ignoreNull) {
            BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
        }else {
            BeanUtils.copyProperties(src, target);
        }
    }
}
