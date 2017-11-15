package io.github.keep2iron.pitaya.compiler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.keep2iron.pitaya.compiler.utils.Const;

public class RouteUtils {

    public static String convertClassName(String className){
        return convertSimpleName(null,className);
    }

    public static String convertClassName(String packageName,String className) {
        return convertSimpleName(packageName.split("\\."),className);
    }

    private static String convertSimpleName(String[] packageName,String simpleName){
        StringBuilder stringBuilder = new StringBuilder();

        if(packageName != null){
            for(String str : packageName){
                stringBuilder.append(str.toUpperCase()).append("_");
            }
        }

        Pattern pattern = Pattern.compile(Const.NAME_PATTERN);
        Matcher matcher = pattern.matcher(simpleName);
        while(matcher.find()){
            stringBuilder.append(matcher.group(0).toUpperCase()).append("_");
        }

        return stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
    }
}