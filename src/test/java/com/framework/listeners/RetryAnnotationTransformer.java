package com.framework.listeners;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Binds {@link TransientFailureRetryAnalyzer} to every test method at the
 * TestNG infra layer, since Cucumber's TestNG bridge generates test methods
 * dynamically and there is no single {@code @Test}-annotated method to
 * annotate directly.
 */
public class RetryAnnotationTransformer implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(TransientFailureRetryAnalyzer.class);
    }
}
