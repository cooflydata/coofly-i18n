package com.github.cooflydata.i18n;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 东来
 * @email 80871901@qq.com
 */
public class LangFileLoader {

    @SuppressWarnings("unchecked")
    private static Map<String, Map<String, Object>> transformHashMap(Map<String, Object> map, String parentKey) {
        Map<String, Map<String, Object>> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                Map<String, Object> nestedMap = (Map<String, Object>) value;
                String newParentKey = parentKey.isEmpty() ? key : parentKey + "." + key;
                result.putAll(transformHashMap(nestedMap, newParentKey));
            } else {
                if (!parentKey.isEmpty()) {
                    Map<String, Object> innerMap = null;
                    if (result.containsKey(parentKey)) {
                        innerMap = result.get(parentKey);
                        innerMap.put(key, value);
                        result.put(parentKey, innerMap);
                    } else {
                        innerMap = new HashMap<>();
                    }
                    innerMap.put(key, value);
                    result.put(parentKey, innerMap);
                }
            }
        }
        return result;
    }

    public Map<String, Object> loadYaml(String fileName) {
        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        return yaml.load(inputStream);
    }

    public Map<String, Map<String, Object>> loadMap(String fileName) {
        return transformHashMap(loadYaml(fileName), "");
    }

}
