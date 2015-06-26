/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.meta;

import javax.lang.model.element.Modifier;
import java.util.EnumSet;

/**
 * @author nathan.taylor
 * @since 2014-11-11.
 */
public class Modifiers {

    public static final EnumSet<Modifier> FINAL = EnumSet.of(Modifier.FINAL);
    public static final EnumSet<Modifier> NONE = EnumSet.noneOf(Modifier.class);
    public static final EnumSet<Modifier> PRIVATE = EnumSet.of(Modifier.PRIVATE);
    public static final EnumSet<Modifier> PUBLIC = EnumSet.of(Modifier.PUBLIC);
    public static final EnumSet<Modifier> PUBLIC_CONSTANT = EnumSet.of(Modifier.PUBLIC, Modifier.STATIC,
                                                                       Modifier.FINAL);
    public static final EnumSet<Modifier> PRIVATE_CONSTANT = EnumSet.of(Modifier.PRIVATE, Modifier.STATIC,
                                                                        Modifier.FINAL);
}
