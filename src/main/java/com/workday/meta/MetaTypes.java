/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.meta;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @author nathan.taylor
 * @since 2013-9-27-15:49
 */
public class MetaTypes {

    private final Types typeUtils;
    private final Elements elementUtils;
    private DeclaredType arrayListType;
    private DeclaredType stringType;

    private DeclaredType booleanType;
    private DeclaredType byteType;
    private DeclaredType characterType;
    private DeclaredType charSequenceType;
    private DeclaredType doubleType;
    private DeclaredType floatType;
    private DeclaredType integerType;
    private DeclaredType longType;
    private DeclaredType shortType;
    private Set<DeclaredType> boxedTypes;

    public MetaTypes(ProcessingEnvironment processingEnv) {
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        initializeKnownTypes();
    }

    private void initializeKnownTypes() {
        Element parameterizedArrayList = elementUtils.getTypeElement(ArrayList.class.getCanonicalName());
        arrayListType = (DeclaredType) typeUtils.erasure(parameterizedArrayList.asType());

        stringType = getDeclaredType(String.class);

        booleanType = getDeclaredType(Boolean.class);
        byteType = getDeclaredType(Byte.class);
        characterType = getDeclaredType(Character.class);
        charSequenceType = getDeclaredType(CharSequence.class);
        doubleType = getDeclaredType(Double.class);
        floatType = getDeclaredType(Float.class);
        integerType = getDeclaredType(Integer.class);
        longType = getDeclaredType(Long.class);
        shortType = getDeclaredType(Short.class);

        boxedTypes = Sets.newHashSet(booleanType, byteType, characterType, doubleType, floatType, integerType, longType,
                                     shortType);
    }

    private DeclaredType getDeclaredType(Class<?> clazz) {
        return (DeclaredType) elementUtils.getTypeElement(clazz.getCanonicalName()).asType();
    }

    public boolean isString(TypeMirror type) {
        return typeUtils.isSameType(type, stringType);
    }

    public boolean isPrimitive(TypeMirror type) {
        return type.getKind().isPrimitive();
    }

    public boolean isArrayList(TypeMirror type) {
        return typeUtils.isSubtype(type, arrayListType);
    }

    public boolean isBoxed(TypeMirror type) {
        return boxedTypes.contains(type);
    }

    public boolean isArray(TypeMirror type) {
        return type.getKind() == TypeKind.ARRAY;
    }

    public boolean isBoxable(TypeMirror type) {
        return isPrimitive(type) || isBoxed(type);
    }

    public TypeMirror asPrimitive(TypeMirror type) {
        if (isPrimitive(type)) {
            return type;
        } else if (isBoxed(type)) {
            return typeUtils.unboxedType(type);
        } else {
            throw new IllegalArgumentException(String.format("%s is neither primitive nor boxed", type));
        }
    }

    public TypeMirror asBoxed(TypeMirror type) {
        if (isPrimitive(type)) {
            return typeUtils.boxedClass((PrimitiveType) type).asType();
        } else if (isBoxed(type)) {
            return type;
        } else {
            throw new IllegalArgumentException(String.format("%s is neither primitive nor boxed", type));
        }
    }

    public TypeMirror getTypeArg(DeclaredType element) {
        List<? extends TypeMirror> typeArguments = element.getTypeArguments();
        Preconditions.checkArgument(typeArguments.size() == 1, "%s must have exactly one type argument, but found %d",
                                    element, typeArguments.size());
        return typeArguments.get(0);
    }

    public boolean isSubtype(TypeMirror type, Class<?> clazz) {
        return isSubtype(type, clazz.getCanonicalName());
    }

    public boolean isSubtype(TypeMirror type, String canonicalName) {
        TypeMirror otherType = elementUtils.getTypeElement(canonicalName).asType();
        return typeUtils.isSubtype(type, typeUtils.erasure(otherType));
    }

    public boolean isSubtypeErasure(TypeMirror type, Class<?> clazz) {
        return isSubtypeErasure(type, clazz.getCanonicalName());
    }

    public boolean isSubtypeErasure(TypeMirror type, String canonicalName) {
        TypeMirror otherType = elementUtils.getTypeElement(canonicalName).asType();
        return typeUtils.isSubtype(typeUtils.erasure(type), typeUtils.erasure(otherType));
    }

    public boolean isSameType(TypeMirror type, Class<?> clazz) {
        TypeMirror otherType = elementUtils.getTypeElement(clazz.getCanonicalName()).asType();
        return typeUtils.isSameType(type, typeUtils.erasure(otherType));
    }

    public boolean isSameTypeErasure(TypeMirror type, Class<?> clazz) {
        return isSameType(typeUtils.erasure(type), clazz);
    }

    public boolean isAssignable(TypeMirror type, Class<?> clazz) {
        TypeMirror otherType = elementUtils.getTypeElement(clazz.getCanonicalName()).asType();
        return typeUtils.isAssignable(type, typeUtils.erasure(otherType));
    }

    public boolean isAssignable(Class<?> clazz, TypeMirror type) {
        TypeMirror otherType = elementUtils.getTypeElement(clazz.getCanonicalName()).asType();
        return typeUtils.isAssignable(typeUtils.erasure(otherType), type);
    }

    public boolean isBoolean(TypeMirror type) {
        return type.getKind() == TypeKind.BOOLEAN || typeUtils.isSameType(type, booleanType);
    }

    public boolean isByte(TypeMirror type) {
        return type.getKind() == TypeKind.BYTE || typeUtils.isSameType(type, byteType);
    }

    public boolean isChar(TypeMirror type) {
        return type.getKind() == TypeKind.CHAR || typeUtils.isSameType(type, characterType);
    }

    public boolean isCharSequecne(TypeMirror type) {
        return typeUtils.isSameType(type, charSequenceType);
    }

    public boolean isDouble(TypeMirror type) {
        return type.getKind() == TypeKind.DOUBLE || typeUtils.isSameType(type, doubleType);
    }

    public boolean isFloat(TypeMirror type) {
        return type.getKind() == TypeKind.FLOAT || typeUtils.isSameType(type, floatType);
    }

    public boolean isInt(TypeMirror type) {
        return type.getKind() == TypeKind.INT || typeUtils.isSameType(type, integerType);
    }

    public boolean isLong(TypeMirror type) {
        return type.getKind() == TypeKind.LONG || typeUtils.isSameType(type, longType);
    }

    public boolean isShort(TypeMirror type) {
        return type.getKind() == TypeKind.SHORT || typeUtils.isSameType(type, shortType);
    }

    public DeclaredType getFirstParameterType(DeclaredType collectionType) throws InvalidTypeException {
        return getParameterType(collectionType, 0);
    }

    public DeclaredType getParameterType(DeclaredType parameterizedType, int parameter) throws InvalidTypeException {
        DeclaredType declaredParameterType;
        List<? extends TypeMirror> typeArguments = parameterizedType.getTypeArguments();
        if (typeArguments.size() <= parameter) {
            throw new IllegalArgumentException(
                    String.format(Locale.US, "Parameter %d was requested but the number of parameters is %d.",
                                  parameter, typeArguments.size()));
        }
        TypeMirror parameterType = typeArguments.get(parameter);
        if (!(parameterType instanceof DeclaredType)) {
            throw new InvalidTypeException("Cannot handle objects parametrised with non-declared types.");
        } else {
            declaredParameterType = (DeclaredType) parameterType;
        }
        return declaredParameterType;
    }
}
