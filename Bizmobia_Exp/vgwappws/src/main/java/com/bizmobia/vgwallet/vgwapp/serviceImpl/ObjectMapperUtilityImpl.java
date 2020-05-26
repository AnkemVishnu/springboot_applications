package com.bizmobia.vgwallet.vgwapp.serviceImpl;

import com.bizmobia.vgwallet.vgwapp.service.ObjectMapperUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import java.util.List;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Vaibhav
 */
@Service
public class ObjectMapperUtilityImpl implements ObjectMapperUtility {

    private static final Logger logger = LoggerFactory.getLogger(ObjectMapperUtilityImpl.class);

    public static ObjectMapper mapper = new ObjectMapper();

    @Override
    public <T> String objectToJson(T obj) {
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(obj);
            logger.info("ObjectMapperUtilityImpl.class", "objectToJson()", "Success");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("ObjectMapperUtilityImpl.class", "objectToJson()", "Plese try after Sometime " + e.getMessage());
        }
        return jsonString;
    }

    @Override
    public <T> T jsonToObject(String jsonString, Class<T> clazz) {
        T obj = null;
        try {
            obj = mapper.readValue(jsonString, clazz);
            logger.info("ObjectMapperUtilityImpl.class", "jsonToObject()", "Success");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("ObjectMapperUtilityImpl.class", "jsonToObject()", "Plese try after Sometime " + e.getMessage());
        }
        return obj;
    }

    @Override
    public <T> List<T> jsonArrayToObjectList(List<?> resplist, Class<T> reqclass) {
        List<T> objlist = null;
        try {
            objlist = new ObjectMapper().readValue(new Gson().toJson(resplist),
                    TypeFactory.defaultInstance().constructCollectionType(List.class, reqclass));
            logger.info("ObjectMapperUtilityImpl.class", "jsonArrayToObjectList()", "Success");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("ObjectMapperUtilityImpl.class", "jsonArrayToObjectList()", "Plese try after Sometime " + e.getMessage());
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
            logger.info("ObjectMapperUtilityImpl.class", "nullAwareBeanCopy()", "Success");
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("ObjectMapperUtilityImpl.class", "nullAwareBeanCopy()", "Plese try after Sometime " + e.getMessage());
        }
    }

}
