package com.dexmohq.spring.eureka.util;

import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * @author Henrik Drefs
 */
@UtilityClass
public class AnnotationUtils {

    @SafeVarargs
    public static void validateOnlyOneOfAnnotationsPresent(AnnotatedElement e, Class<? extends Annotation>... annotationTypes) {
        Class<? extends Annotation> lastSeenAnnotation = null;
        for (Class<? extends Annotation> annotationType : annotationTypes) {
            if (e.isAnnotationPresent(annotationType)) {
                if (lastSeenAnnotation != null) {
                    throw new IllegalStateException("Cannot use both "
                            + lastSeenAnnotation.getSimpleName()
                            + " and "
                            + annotationType.getSimpleName()
                            + " annotations on " + e);
                }
                lastSeenAnnotation = annotationType;
            }
        }
    }
}
