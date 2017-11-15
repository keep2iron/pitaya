package io.github.keep2iron.pitaya.compiler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.keep2iron.pitaya.compiler.utils.Const;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/07/08 15:14
 *
 * @Aurowired(name = 'mark') String mark;
 *
 * name = 'EXTRA_STRING_MARK' value = 'mark' type = 'String';
 */
public class ExtraUtils {

    public static String convertFieldName(String fieldType, String fieldName){

        StringBuilder builder = new StringBuilder().append("EXTRA_")
                .append(fieldType.toUpperCase())
                .append("_");

        char firstChar = fieldName.charAt(0);
        if(firstChar >= 'a' && firstChar <= 'z'){
            fieldName = (char)(firstChar - 32) + fieldName.substring(1);
        }else{
            return builder.append(fieldName.toUpperCase()).toString();
        }

        Pattern pattern = Pattern.compile(Const.NAME_PATTERN);
        Matcher matcher = pattern.matcher(fieldName);
        while (matcher.find()) {
            builder.append(matcher.group(0).toUpperCase()).append("_");
        }

        return builder.deleteCharAt(builder.length() - 1).toString();
    }
}