/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.meta;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

/**
 * @author nathan.taylor
 * @since 2014-10-16.
 */
public final class MetaTypeNames {

    private MetaTypeNames() {}

    public static String constructTypeName(TypeElement type) {
        return constructTypeName(type, "");
    }

    public static String constructTypeName(TypeElement baseType, String suffix) {
        StringBuilder result = new StringBuilder();
        String packageName = getPackage(baseType).getQualifiedName().toString();
        String qualifiedName = baseType.getQualifiedName().toString();
        if (packageName.isEmpty()) {
            result.append(qualifiedName.replace('.', Constants.INNER_CLASS_SEPARATOR));
        } else {
            result.append(packageName);
            result.append('.');
            result.append(qualifiedName.substring(packageName.length() + 1).replace('.', Constants.INNER_CLASS_SEPARATOR));
        }
        result.append(suffix);
        return result.toString();
    }

    private static PackageElement getPackage(Element type) {
        while (type.getKind() != ElementKind.PACKAGE) {
            type = type.getEnclosingElement();
        }
        return (PackageElement) type;
    }
}
