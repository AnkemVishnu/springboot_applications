package com.vishnu.ObjectMapping;

import java.util.List;

/**
 *
 * @author Vishnu
 */
public interface ObjectMapperUtility {

    public static final String encGenKey = "bizmobiabizmobia";

    public <T> String objectToJson(T obj);

    public <T> T jsonToObject(String jsonString, Class<T> clazz);

    public <T> List<T> jsonArrayToObjectList(List<?> resplist, Class<T> reqclass);
    
    public void nullAwareBeanCopy(Object dest, Object source);
}
