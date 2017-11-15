package io.github.keep2iron.pitaya.compiler;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import io.github.keep2iron.pitaya.compiler.utils.Const;
import io.github.keep2iron.pitaya.compiler.utils.TypeUtils;

/**
 * the data set in the building process
 * <p>
 * exp:
 * moduleName is app
 * className is @Route(path="/test/main")MainActivity
 * generate file is
 * APP.MAIN_ACTIVITY = "/test/main";
 *
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/07/07 11:04
 */
public class BuildingSet {
    TypeSpec.Builder mRouteBuilder;
    TypeSpec.Builder mExtraBuilder;

    private String mModuleName;

    /**
     * 以类名作为key,@Route的path作为值
     */
    Map<String, String> mRouteMap = new HashMap<>();

    /**
     * @Aurowired(name = 'mark') String mark; = >EXTRA_STRING_MARK
     * <p>
     * key : EXTRA_STRING_MARK
     * value : 'mark'
     */
    Map<String, String> mExtraMap = new HashMap<>();

    public BuildingSet(String moduleName) {
        this.mModuleName = moduleName;

        mRouteBuilder = TypeSpec.classBuilder("Route")
                .addModifiers(Modifier.PUBLIC);

        mExtraBuilder = TypeSpec.classBuilder("Extra")
                .addModifiers(Modifier.PUBLIC);
    }

    public void addRoute(Element ele, Elements util) {
        String className = ele.getSimpleName().toString();
        Route route = ele.getAnnotation(Route.class);

        if (mRouteMap.get(className) == null) {
            className = RouteUtils.convertClassName(className);
        } else {
            className = RouteUtils.convertClassName(util.getPackageOf(ele).getQualifiedName().toString(), className);
        }

        mRouteMap.put(className, route.path());

    }

    public void addAutowired(Element ele, TypeUtils util) {
        Autowired autowired = ele.getAnnotation(Autowired.class);

        String filedName = ele.getSimpleName().toString();
        String filedType = util.getType(ele);

        String typeName = ExtraUtils.convertFieldName(filedType, filedName);

        if (StringUtils.isEmpty(autowired.name())) {
            mExtraMap.put(typeName, filedName);
        } else {
            mExtraMap.put(typeName, autowired.name());
        }
    }

    public JavaFile build() {
        for (Map.Entry<String, String> extra : mExtraMap.entrySet()) {
            mExtraBuilder.addField(
                    FieldSpec.builder(ClassName.get(String.class), extra.getKey())
                            .addModifiers(Modifier.PUBLIC)
                            .addModifiers(Modifier.STATIC)
                            .addModifiers(Modifier.FINAL)
                            .initializer("$S", extra.getValue())
                            .build());
        }

        for (Map.Entry<String, String> route : mRouteMap.entrySet()) {
            mRouteBuilder.addField(
                    FieldSpec.builder(ClassName.get(String.class), route.getKey())
                            .addModifiers(Modifier.PUBLIC)
                            .addModifiers(Modifier.STATIC)
                            .addModifiers(Modifier.FINAL)
                            .initializer("$S", route.getValue())
                            .build());
        }

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(mModuleName)
                .addModifiers(Modifier.PUBLIC)
                .addType(mRouteBuilder.build())
                .addType(mExtraBuilder.build());
        return JavaFile.builder(Const.GENERATE_PACKAGE, classBuilder.build()).build();
    }
}