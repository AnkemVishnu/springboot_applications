package com.vishnu.utilities;

public interface ObjectMapping {

    public <T> String objectToJson(T obj);

    public <T> T jsonToObject(String jsonString, Class<T> clazz);

}
