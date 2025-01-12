package com.github.cooflydata.i18n;

import java.util.Map;

/**
 * @author 东来
 * @email 80871901@qq.com
 */
public class Translator {

    private final Map<String, Map<String, Object>> langMap = new LangFileLoader().loadMap("zh_CN.yaml");

    public String translate(String className, String text) {
        Map<String, Object> classNameMap = langMap.get(className);
        Map<String, Object> anyMatchMap = langMap.get("*");
        Map<String, Object> otherwiseMap = langMap.get("otherwise");
        if (classNameMap != null) {
            if (classNameMap.containsKey(text)) {
                return classNameMap.get(text).toString();
            } else {
                return otherwiseMap.getOrDefault(text, text).toString();
            }
        } else {
            if (anyMatchMap.containsKey(text)) {
                return anyMatchMap.get(text).toString();
            } else {
                return otherwiseMap.getOrDefault(text, text).toString();
            }
        }
    }

}
