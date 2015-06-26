/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.meta;

import javax.lang.model.type.DeclaredType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author nathan.taylor
 * @since 2014-10-09
 */
public final class Initializers {

    private final MetaTypes metaTypes;

    public Initializers(MetaTypes metaTypes) {
        this.metaTypes = metaTypes;
    }

    public String findCollectionInitializer(DeclaredType type) throws InvalidTypeException {
        String initializer;

        // Lists
        if (metaTypes.isSameTypeErasure(type, Collection.class) || metaTypes.isSameTypeErasure(type, List.class)
                || metaTypes.isSameTypeErasure(type, ArrayList.class)) {
            initializer = "new java.util.ArrayList<>()";
        } else if (metaTypes.isSameTypeErasure(type, LinkedList.class)) {
            initializer = "new java.util.LinkedList<>()";
        }

        // Sets
        else if (metaTypes.isSameTypeErasure(type, Set.class) || metaTypes.isSameTypeErasure(type, HashSet.class)) {
            initializer = "new java.util.HashSet<>()";
        } else if (metaTypes.isSameTypeErasure(type, LinkedHashSet.class)) {
            initializer = "new java.util.LinkedHashSet<>()";
        } else if (metaTypes.isSameTypeErasure(type, TreeSet.class)) {
            initializer = "new java.util.TreeSet<>()";
        }

        // Unknown
        else {
            throw new InvalidTypeException(
                    String.format("AutoParse does not know how to instantiate Collection of type %s", type.toString()));
        }
        return initializer;
    }

    public String findMapInitializer(DeclaredType type) throws InvalidTypeException {
        String initializer;
        if (metaTypes.isSameTypeErasure(type, Map.class) || metaTypes.isSameTypeErasure(type, HashMap.class)) {
            initializer = "new java.util.HashMap<>()";
        } else if (metaTypes.isSameTypeErasure(type, LinkedHashMap.class)) {
            initializer = "new java.util.LinkedHashMap<>()";
        } else {
            throw new InvalidTypeException(
                    String.format("AutoParse does not know how to instantiate Map of type %s", type.toString()));
        }
        return initializer;
    }
}
