/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.meta;

/**
 * @author nathan.taylor
 * @since 2014-10-09
 */
public class InvalidTypeException extends Exception {

    private static final long serialVersionUID = 8255039346313124264L;

    public InvalidTypeException(String message) {
        super(message);
    }
}
