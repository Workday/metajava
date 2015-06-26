/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.meta;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.util.Elements;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

/**
 * @author nathan.taylor
 * @since 2015-02-27
 */
@RunWith(JUnit4.class)
public class PackageTreeTest {

    @Test
    public void testSimpleTree() {
        Name orgName = Mockito.mock(Name.class);
        Name comName = Mockito.mock(Name.class);
        PackageElement orgPackageElement = Mockito.mock(PackageElement.class);
        PackageElement comPackageElement = Mockito.mock(PackageElement.class);
        Element orgElement = Mockito.mock(Element.class);
        Element comElement = Mockito.mock(Element.class);
        Element packagelessElement = Mockito.mock(Element.class);
        Elements elementUtils = Mockito.mock(Elements.class);

        when(orgName.toString()).thenReturn("org");
        when(comName.toString()).thenReturn("com");
        when(orgPackageElement.getSimpleName()).thenReturn(orgName);
        when(comPackageElement.getSimpleName()).thenReturn(comName);
        when(elementUtils.getPackageOf(orgElement)).thenReturn(orgPackageElement);
        when(elementUtils.getPackageOf(comElement)).thenReturn(comPackageElement);

        Set<PackageElement> packageElements = new HashSet<>();
        packageElements.add(orgPackageElement);
        PackageTree tree = new PackageTree(elementUtils, packageElements);
        assertPackageEquals(orgPackageElement, tree.getMatchingPackage(orgElement));
        assertNull(tree.getMatchingPackage(comElement));
        assertNull(tree.getMatchingPackage(packagelessElement));
    }

    @Test
    public void testNormalTree() {

        // Packages:
        // '"org"
        // "org.child1"
        // "org.child1.grandchild.greatgrandchild"
        // "org.child2"
        // "com"

        // Elements:
        // "org.Element"
        // "org.child1.Element"
        // "org.child1.grandchild.Element
        // "org.child2.Element"
        // "com.Element"
        // "Element" (packageless)

        // Declare Name Mocks
        Name orgName = Mockito.mock(Name.class);
        Name child1Name = Mockito.mock(Name.class);
        Name grandchildName = Mockito.mock(Name.class);
        Name greatGrandchildName = Mockito.mock(Name.class);
        Name child2Name = Mockito.mock(Name.class);
        Name comName = Mockito.mock(Name.class);

        // Declare Package Mocks
        PackageElement orgPackageElement = Mockito.mock(PackageElement.class);
        PackageElement child1PackageElement = Mockito.mock(PackageElement.class);
        PackageElement child2PackageElement = Mockito.mock(PackageElement.class);
        PackageElement grandchildPackageElement = Mockito.mock(PackageElement.class);
        PackageElement greatGrandchildPackageElement = Mockito.mock(PackageElement.class);
        PackageElement comPackageElement = Mockito.mock(PackageElement.class);

        // Declare Element Mocks
        Element orgElement = Mockito.mock(Element.class);
        Element orgChild1Element = Mockito.mock(Element.class);
        Element orgChild1GrandchildElement = Mockito.mock(Element.class);
        Element orgChild2Element = Mockito.mock(Element.class);
        Element comElement = Mockito.mock(Element.class);
        Element packagelessElement = Mockito.mock(Element.class);

        // Element Utils mock
        Elements elementUtils = Mockito.mock(Elements.class);

        // Set up names
        when(orgName.toString()).thenReturn("org");
        when(child1Name.toString()).thenReturn("child1");
        when(grandchildName.toString()).thenReturn("grandchild");
        when(greatGrandchildName.toString()).thenReturn("greatgrandchild");
        when(child2Name.toString()).thenReturn("child2");
        when(comName.toString()).thenReturn("com");

        // Link packages to names
        when(orgPackageElement.getSimpleName()).thenReturn(orgName);
        when(child1PackageElement.getSimpleName()).thenReturn(child1Name);
        when(grandchildPackageElement.getSimpleName()).thenReturn(grandchildName);
        when(greatGrandchildPackageElement.getSimpleName()).thenReturn(greatGrandchildName);
        when(child2PackageElement.getSimpleName()).thenReturn(child2Name);
        when(comPackageElement.getSimpleName()).thenReturn(comName);

        // Link packages to parent packages
        when(child1PackageElement.getEnclosingElement()).thenReturn(orgPackageElement);
        when(grandchildPackageElement.getEnclosingElement()).thenReturn(child1PackageElement);
        when(greatGrandchildPackageElement.getEnclosingElement()).thenReturn(grandchildPackageElement);
        when(child2PackageElement.getEnclosingElement()).thenReturn(orgPackageElement);

        // Link elements to packages
        when(elementUtils.getPackageOf(orgElement)).thenReturn(orgPackageElement);
        when(elementUtils.getPackageOf(orgChild1Element)).thenReturn(child1PackageElement);
        when(elementUtils.getPackageOf(orgChild1GrandchildElement)).thenReturn(grandchildPackageElement);
        when(elementUtils.getPackageOf(orgChild2Element)).thenReturn(child2PackageElement);
        when(elementUtils.getPackageOf(comElement)).thenReturn(comPackageElement);

        // Create tree
        Set<PackageElement> packageElements = new HashSet<>();
        Collections.addAll(packageElements, orgPackageElement, child1PackageElement, greatGrandchildPackageElement,
                           child2PackageElement, comPackageElement);
        PackageTree tree = new PackageTree(elementUtils, packageElements);

        // Assertions
        assertNull(tree.getMatchingPackage(packagelessElement));
        assertPackageEquals(comPackageElement, tree.getMatchingPackage(comElement));
        assertPackageEquals(orgPackageElement, tree.getMatchingPackage(orgElement));
        assertPackageEquals(child1PackageElement, tree.getMatchingPackage(orgChild1Element));
        assertPackageEquals(child1PackageElement, tree.getMatchingPackage(orgChild1GrandchildElement));
        assertPackageEquals(child2PackageElement, tree.getMatchingPackage(orgChild2Element));
    }

    private static void assertPackageEquals(PackageElement expected, PackageElement actual) {
        assertEquals(String.format(Locale.US, "Expected '%s' but found '%s'", expected.getSimpleName().toString(),
                                   actual.getSimpleName().toString()), expected, actual);
    }
}
