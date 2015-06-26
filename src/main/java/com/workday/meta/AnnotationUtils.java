/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.meta;

import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

/**
 * @author nathan.taylor
 * @since 2014-10-09
 */
public final class AnnotationUtils {

    private AnnotationUtils() {}

    /**
     * Retrieves a {@link Class} value of an annotation as a {@link TypeMirror}. The Class can normally not be retrieved
     * directly because java will throw a {@link MirroredTypeException} if you try to. This method handles that.
     *
     * @param getter A Getter than simply makes the call to directly retrieve the Class from the annotation (e.g. {@code
     * myAnnotation.getValue()}). Don't worry that Getter doesn't return the value. Making such a call with throw an
     * Exception from which we can retrieve the desired TypeMirror.
     *
     * @return The TypeMirror of the desired Class.
     */
    public static TypeMirror getClassTypeMirrorFromAnnotationValue(Getter getter) {

        try {
            getter.get();
        } catch (MirroredTypeException e) {
            return e.getTypeMirror();

        }
        // It should be impossible to get here.
        throw new RuntimeException("Expected MirroredTypeException to be thrown but found nothing.");
    }

    public interface Getter {

        void get();
    }
}
