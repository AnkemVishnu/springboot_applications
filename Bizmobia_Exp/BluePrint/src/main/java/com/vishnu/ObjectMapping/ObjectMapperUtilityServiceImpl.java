package com.vishnu.ObjectMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import java.util.List;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.beanutils.BeanUtilsBean;

/**
 *
 * @author Vishnu
 */
@Service
public class ObjectMapperUtilityServiceImpl implements ObjectMapperUtility {

    public static ObjectMapper mapper = new ObjectMapper();

    @Override
    public <T> String objectToJson(T obj) {
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    @Override
    public <T> T jsonToObject(String jsonString, Class<T> clazz) {
        T obj = null;
        try {
            obj = mapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public <T> List<T> jsonArrayToObjectList(List<?> resplist, Class<T> reqclass) {
        List<T> objlist = null;
        try {
            objlist = new ObjectMapper().readValue(new Gson().toJson(resplist),
                    TypeFactory.defaultInstance().constructCollectionType(List.class, reqclass));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return objlist;
    }

    @Override
    public void nullAwareBeanCopy(Object dest, Object source) {
        try {
            new BeanUtilsBean() {
                @Override
                public void copyProperty(Object dest, String name, Object value)
                        throws IllegalAccessException, InvocationTargetException {
                    if (value != null) {
                        super.copyProperty(dest, name, value);
                    }
                }
            }.copyProperties(dest, source);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            Logger.getLogger(ObjectMapperUtilityServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
