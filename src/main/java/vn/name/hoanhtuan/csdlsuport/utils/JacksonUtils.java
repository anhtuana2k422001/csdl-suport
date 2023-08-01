package vn.name.hoanhtuan.csdlsuport.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

@Slf4j
@NoArgsConstructor (access = AccessLevel.PRIVATE)
public class JacksonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private static final ObjectWriter writer = objectMapper.writer();
    private static final TypeReference<HashMap<String, String>> STRING_MAP_TYPE_REFERENCE = new TypeReference<HashMap<String, String>>() {};
    private static final TypeReference<HashMap<String, Object>> OBJECT_MAP_TYPE_REFERENCE = new TypeReference<HashMap<String, Object>>() {};

    public static String toJSonString(Object value){
        try{
            return writer.writeValueAsString(value);
        }catch (Exception e){
            LOGGER.error("Failed toJSonString - Error", e);
            return StringUtils.EMPTY;
        }
    }

    public static <T> T fromJSonString(String json, Class<T> clazz){
        if(StringUtils.isBlank(json)){
            return null;
        }
        try{
            return objectMapper.readValue(json, clazz);
        }catch (IOException e){
            LOGGER.error("Failed fromJSonString - Error", e);
            return null;
        }
    }

    public static Map<String, String> stringToMap(String json){
        if(StringUtils.isBlank(json)){
            return Collections.emptyMap();
        }

        try{
            return objectMapper.readValue(json, STRING_MAP_TYPE_REFERENCE);
        }catch(Exception e){
            LOGGER.error("Failed stringToMap - Error", e);
            return Collections.emptyMap();
        }
    }

    public static Map<String, Object> objectToMap(String json){
        if(StringUtils.isBlank(json)){
            return Collections.emptyMap();
        }

        try{
            return objectMapper.readValue(json, OBJECT_MAP_TYPE_REFERENCE);
        }catch (IOException e){
            LOGGER.error("Failed objectToMap - Error", e);
            return Collections.emptyMap();
        }
    }

    public static <T> List<T> stringToList(String json, Class<T> clazz){
        if(StringUtils.isBlank(json)){
            return Collections.emptyList();
        }

        try{
            CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
            return objectMapper.readValue(json, listType);
        }catch (Exception e){
            LOGGER.error("Failed stringToList - Error", e);
            return Collections.emptyList();
        }
    }
}
