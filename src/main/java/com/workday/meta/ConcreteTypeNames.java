/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.meta;

import java.util.LinkedList;

/**
 * @author nathan.taylor
 * @since 2014-10-16.
 */
public class ConcreteTypeNames {

    private ConcreteTypeNames() {}

    public static String constructClassName(Class<?> baseClazz, String suffix) {
        LinkedList<Class<?>> classTree = new LinkedList<>();
        while (baseClazz != null) {
            classTree.push(baseClazz);
            baseClazz = baseClazz.getEnclosingClass();
        }

        StringBuilder result = new StringBuilder(classTree.pop().getCanonicalName());
        while (!classTree.isEmpty()) {
            result.append(Constants.INNER_CLASS_SEPARATOR).append(classTree.pop().getSimpleName());
        }
        result.append(suffix);
        return result.toString();
    }
}
