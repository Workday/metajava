/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.meta;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.EnumSet;

/**
 * @author nathan.taylor
 * @since 2014-10-09
 */
public final class CodeAnalysisUtils {

    private CodeAnalysisUtils() {}

    public static boolean isPrivate(Element element) {
        return element.getModifiers().contains(Modifier.PRIVATE);
    }

    public static boolean isFinal(Element element) {
        return element.getModifiers().contains(Modifier.FINAL);
    }

    public static boolean isConstant(Element element) {
        return element.getModifiers().containsAll(EnumSet.of(Modifier.STATIC, Modifier.FINAL));
    }

    public static boolean isStatic(Element element) {
        return element.getModifiers().contains(Modifier.STATIC);
    }

}
