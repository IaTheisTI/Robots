package state;

import java.util.*;

/**
 * код для работы с подсловарями
 */
public class StateTransformer {
    /**
     * код формирования подсловаря по общему словарю
     */
    public static Map<String, String> getSubMap(Map<String, String> generalMap, String prefix){
        Map<String, String> subMap = new HashMap<>();
        generalMap.entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith(prefix))
                .forEach(entry -> subMap.put(entry.getKey().split("\\.")[1], entry.getValue()));
        if(!subMap.isEmpty()){
            return subMap;
        }
        else{
            throw new IllegalArgumentException("Wrong prefix");
        }
    }

    /**
     * код добавления префикса к подсловарю
     */
    private static Map<String, String> addPrefixToSubMap(Map<String, String> subMap, String prefix){
        Map<String, String> subMapWithPrefix = new HashMap<>();
        subMap.forEach((key, value) -> subMapWithPrefix.put(prefix + "." + key, value));
        return subMapWithPrefix;
    }

    /**
     * код добавления подсловаря в общий словарь по префиксу
     */
    public static void addSubMapToGeneralMapByPrefix(String prefix, Map<String, String> subMap, Map<String, String> generalMap){
        subMap = addPrefixToSubMap(subMap, prefix);
        generalMap.putAll(subMap);
    }
}