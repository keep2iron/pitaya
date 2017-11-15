/*
 * Create bt Keep2iron on 17-7-6 上午10:19
 */

package io.github.keep2iron.pitaya.compiler.utils;

/**
 * Created by keep2iron on ${Date}.
 * write the powerful code ！
 * website : keep2iron.github.io
 */
public class Const {
    public static final String PREFIX_OF_LOGGER = "compiler===========>:";

    public static final String PITAYA_CLASS = "io.github.keep2iron.pitaya.annntation.Pitaya";

    // Options of processor
    public static final String KEY_MODULE_NAME = "moduleName";
    public static final String KEY_MAIN_MODULE_NAME = "mainModuleName";


    public static final String PARCELABLE = "android.os.Parcelable";

    // Java type
    private static final String LANG = "java.lang";
    public static final String BYTE = LANG + ".Byte";
    public static final String SHORT = LANG + ".Short";
    public static final String INTEGER = LANG + ".Integer";
    public static final String LONG = LANG + ".Long";
    public static final String FLOAT = LANG + ".Float";
    public static final String DOUBEL = LANG + ".Double";
    public static final String BOOLEAN = LANG + ".Boolean";
    public static final String STRING = LANG + ".String";

    /**
     * 类名的正则表达式
     */
    public static final String NAME_PATTERN = "[A-Z][a-z0-9_]*";
    public static final String GENERATE_PACKAGE = "io.github.keep2iron.route";
}
