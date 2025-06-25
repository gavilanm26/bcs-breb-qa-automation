package com.bcs.breb.utils.customrunner;

import com.bcs.breb.utils.readExcel.dataToFeature;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class CustomRunner extends Runner {

    private Class<CucumberWithSerenity> classValue;
    private CucumberWithSerenity cucumberWithSerenity;

    public CustomRunner(Class<CucumberWithSerenity> classValue) throws Exception {

        this.classValue = classValue;
        cucumberWithSerenity = new CucumberWithSerenity(classValue);
    }

    @Override
    public Description getDescription() {
        return cucumberWithSerenity.getDescription();
    }

    private void runAnnotatedMethods(Class<?> annotation) throws Exception {
        if (!annotation.isAnnotation()) {
            return;
        }
        Method[] methods = this.classValue.getMethods();
        for (Method method : methods) {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation item : annotations) {
                if (item.annotationType().equals(annotation)) {
                    method.invoke(null);
                    break;
                }
            }
        }
    }

    @Override
    public void run(RunNotifier notifier) {
        try {
            dataToFeature.overrideFeatureFiles(
                System.getProperty("user.dir") + "/src/test/resources/features"
            );
            cucumberWithSerenity = new CucumberWithSerenity(classValue);
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar los datos del Excel: " + e.getMessage(), e);
        }

        cucumberWithSerenity.run(notifier);

    }

}