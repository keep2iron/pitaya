/*
 * Create bt Keep2iron on 17-7-6 上午10:21
 */

/*
 * Create bt Keep2iron on 17-7-6 上午10:12
 */

/*
 * Create bt Keep2iron on 17-7-6 上午10:12
 */

/*
 * Create bt Keep2iron on 17-7-6 上午9:44
 */

package io.github.keep2iron.pitaya.compiler.processor;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import io.github.keep2iron.pitaya.compiler.BuildingSet;
import io.github.keep2iron.pitaya.compiler.utils.Const;
import io.github.keep2iron.pitaya.compiler.utils.Logger;
import io.github.keep2iron.pitaya.compiler.utils.TypeUtils;

/**
 * Created by keep2iron on ${Date}.
 * write the powerful code ！
 * website : keep2iron.github.io
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({
        "com.alibaba.android.arouter.facade.annotation.Route",
})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class PitayaProcessor extends AbstractProcessor {
    String moduleName;

    private Elements eleUtil;
    private Types mTypes;
    private TypeUtils mTypeUtils;
    private Logger logger;
    private Filer mFiler;

    String mainModuleName = null;
    private BuildingSet mBuildingSet;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnv.getFiler();
        eleUtil = processingEnv.getElementUtils();
        mTypes = processingEnv.getTypeUtils();
        logger = new Logger(processingEnv.getMessager());
        mTypeUtils = new TypeUtils(mTypes,eleUtil);

        Map<String, String> options = processingEnvironment.getOptions();
        if (MapUtils.isNotEmpty(options)) {
            moduleName = options.get(Const.KEY_MODULE_NAME);
            mainModuleName = options.get(Const.KEY_MAIN_MODULE_NAME);
        }

        if (StringUtils.isNoneEmpty(moduleName)) {
            moduleName = moduleName.replaceAll("-", "_");
            moduleName = moduleName.replaceAll("[^0-9a-zA-Z_]+", "");
            moduleName = moduleName.toUpperCase();

            logger.info(Const.PREFIX_OF_LOGGER + " moduleName is " + moduleName);
            logger.info(Const.PREFIX_OF_LOGGER + " mainModuleName is " + mainModuleName);
        } else {
            throw new IllegalArgumentException("moduleName is not empty please " +
                    "javaCompileOptions {" +
                    "annotationProcessorOptions {" +
                    "arguments = [moduleName: project.getName(),mainModuleName : '......']" +
                    "}" +
                    "}" +
                    "in your module.gradle and addRoute it in defaultConfig {}...");
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if(CollectionUtils.isEmpty(set)){
            return false;
        }

        if(StringUtils.isEmpty(mainModuleName)){
            return false;
        }

        mBuildingSet = new BuildingSet(moduleName);

        buildRoute(roundEnvironment);
        buildAutowired(roundEnvironment);
        generateFile();

        return false;
    }

    private void buildRoute(RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Route.class);
        if (elements != null && !elements.isEmpty()) {
            for (Element ele : elements) {
                mBuildingSet.addRoute(ele,eleUtil);
            }
        }
    }

    private void buildAutowired(RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Autowired.class);
        if (elements != null && !elements.isEmpty()) {
            for (Element ele : elements) {
                mBuildingSet.addAutowired(ele,mTypeUtils);
            }
        }
    }

    private void generateFile() {
        try {
            String dir = System.getProperty("user.dir") +
                    File.separator + mainModuleName +
                    File.separator + "src" +
                    File.separator + "main" +
                    File.separator + "java";
            File file = new File(dir);
            mBuildingSet.build().writeTo(file);
        } catch (IOException e) {
            logger.error(e);
            e.printStackTrace();
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportType = new HashSet<>();
        supportType.add("com.alibaba.android.arouter.facade.annotation.Route");

        return supportType;
    }
}