package com.dexmohq.spring.eureka.util;

import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.dexmohq.spring.eureka.util.AnnotationUtils.validateOnlyOneOfAnnotationsPresent;

/**
 * @author Henrik Drefs
 */
public class AnnotationUtilsTest {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface A {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface B {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface C {
    }

    private static class WithNone {

    }

    @A
    private static class WithA {

    }

    @A
    @B
    private static class WithAB {

    }

    @A
    @C
    private static class WithAC {

    }

    @A
    @B
    @C
    private static class WithABC {

    }

    @Test
    public void testEmpty() {
        validateOnlyOneOfAnnotationsPresent(WithNone.class);
        validateOnlyOneOfAnnotationsPresent(WithA.class);
        validateOnlyOneOfAnnotationsPresent(WithAB.class);
    }

    @Test
    public void testSingle() {
        validateOnlyOneOfAnnotationsPresent(WithNone.class, A.class);
        validateOnlyOneOfAnnotationsPresent(WithA.class, A.class);
        validateOnlyOneOfAnnotationsPresent(WithAB.class, A.class);
    }

    @Test
    public void testTwoValid() {
        validateOnlyOneOfAnnotationsPresent(WithNone.class, A.class, B.class);
        validateOnlyOneOfAnnotationsPresent(WithA.class, A.class, B.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testTwoInvalid_BothPresent() {
        validateOnlyOneOfAnnotationsPresent(WithAB.class, A.class, B.class);
    }

    @Test
    public void testThreeValid() {
        validateOnlyOneOfAnnotationsPresent(WithNone.class, A.class, B.class, C.class);
        validateOnlyOneOfAnnotationsPresent(WithA.class, A.class, B.class, C.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testThreeInvalid_ABPresent() {
        validateOnlyOneOfAnnotationsPresent(WithAB.class, A.class, B.class, C.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testThreeInvalid_ACPresent() {
        validateOnlyOneOfAnnotationsPresent(WithAC.class, A.class, B.class, C.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testThreeInvalid_ABCPresent() {
        validateOnlyOneOfAnnotationsPresent(WithABC.class, A.class, B.class, C.class);
    }
}
