package com.vishnu.utilities;

/**
 *
 * @author Vishnu
 */
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperUtil {

    public static String encGenKey = "bizmobiabizmobia";
    public static ObjectMapper mapper = new ObjectMapper();

    public static <T> String objectToJson(T obj) {
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    public static <T> T jsonToObject(String jsonString, Class<T> clazz) {
        T obj = null;
        try {
            obj = mapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

//    public static void main(String[] args) {
//        String employeeString = "{\"countryId\":\"1\",\"countryName\":\"Johnson\"}";
//        Country employeeFromString = jsonToObject(employeeString, Country.class);
//        System.out.println("Country from Json:");
//        System.out.println(employeeFromString);
//
//    }
}
