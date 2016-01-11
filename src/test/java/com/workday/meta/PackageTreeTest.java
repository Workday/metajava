/*
 * Copyright 2015 Workday, Inc.
 *
 * This software is available under the MIT license.
 * Please see the LICENSE.txt file in this project.
 */

package com.workday.meta;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.util.Elements;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

/**
 * @author nathan.taylor
 * @since 2015-02-27
 */
@RunWith(MockitoJUnitRunner.class)
public class PackageTreeTest {

    @Mock
    private Name orgName;
    @Mock
    private Name comName;
    @Mock
    private PackageElement orgPackageElement;
    @Mock
    private PackageElement comPackageElement;
    @Mock
    private Element orgElement;
    @Mock
    private Element comElement;
    @Mock
    private Element packagelessElement;
    @Mock
    private Elements elementUtils;

    @Before
    public void setUp() throws Exception {
        when(orgName.toString()).thenReturn("org");
        when(comName.toString()).thenReturn("com");
        when(orgPackageElement.getQualifiedName()).thenReturn(orgName);
        when(comPackageElement.getQualifiedName()).thenReturn(comName);
        when(elementUtils.getPackageOf(orgElement)).thenReturn(orgPackageElement);
        when(elementUtils.getPackageOf(comElement)).thenReturn(comPackageElement);
        when(elementUtils.getPackageElement("org")).thenReturn(orgPackageElement);
        when(elementUtils.getPackageElement("com")).thenReturn(comPackageElement);

    }

    @Test
    public void testSimpleTree() {
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
        // "org.child1.grandchild.greatgrandchild.Element
        // "org.child2.Element"
        // "com.Element"
        // "Element" (packageless)

        //Declare Canonical Name Mocks
        Name child1CanonicalName = Mockito.mock(Name.class);
        Name grandchildCanonicalName = Mockito.mock(Name.class);
        Name greatGrandchildCanonicalName = Mockito.mock(Name.class);
        Name child2CanonicalName = Mockito.mock(Name.class);

        // Declare Package Mocks
        PackageElement child1PackageElement = Mockito.mock(PackageElement.class);
        PackageElement child2PackageElement = Mockito.mock(PackageElement.class);
        PackageElement grandchildPackageElement = Mockito.mock(PackageElement.class);
        PackageElement greatGrandchildPackageElement = Mockito.mock(PackageElement.class);

        // Declare Element Mocks
        Element orgChild1Element = Mockito.mock(Element.class);
        Element orgChild1GrandchildElement = Mockito.mock(Element.class);
        Element orgChild2Element = Mockito.mock(Element.class);
        Element packagelessElement = Mockito.mock(Element.class);
        Element greatGrandChildElement = Mockito.mock(Element.class);

        // Set up canonical names
        when(child1CanonicalName.toString()).thenReturn("org.child1");
        when(grandchildCanonicalName.toString()).thenReturn("org.child1.grandchild");
        when(greatGrandchildCanonicalName.toString()).thenReturn("org.child1.grandchild.greatgrandchild");
        when(child2CanonicalName.toString()).thenReturn("org.child2");

        // Link packages to canonical names
        when(child1PackageElement.getQualifiedName()).thenReturn(child1CanonicalName);
        when(grandchildPackageElement.getQualifiedName()).thenReturn(grandchildCanonicalName);
        when(greatGrandchildPackageElement.getQualifiedName()).thenReturn(greatGrandchildCanonicalName);
        when(child2PackageElement.getQualifiedName()).thenReturn(child2CanonicalName);

        // Link elements to packages
        when(elementUtils.getPackageOf(orgChild1Element)).thenReturn(child1PackageElement);
        when(elementUtils.getPackageOf(orgChild1GrandchildElement)).thenReturn(grandchildPackageElement);
        when(elementUtils.getPackageOf(orgChild2Element)).thenReturn(child2PackageElement);
        when(elementUtils.getPackageOf(greatGrandChildElement)).thenReturn(greatGrandchildPackageElement);

        // Link strings to packages
        when(elementUtils.getPackageElement("org.child1")).thenReturn(child1PackageElement);
        when(elementUtils.getPackageElement("org.child1.grandchild")).thenReturn(grandchildPackageElement);
        when(elementUtils.getPackageElement("org.child1.grandchild.greatgrandchild")).thenReturn(
                greatGrandchildPackageElement);
        when(elementUtils.getPackageElement("org.child2")).thenReturn(child2PackageElement);

        // Create tree
        Set<PackageElement> packageElements = new HashSet<>();
        Collections.addAll(packageElements, orgPackageElement, child1PackageElement, child2PackageElement,
                           comPackageElement);
        PackageTree tree = new PackageTree(elementUtils, packageElements);

        // Assertions
        assertNull(tree.getMatchingPackage(packagelessElement));
        assertPackageEquals(comPackageElement, tree.getMatchingPackage(comElement));
        assertPackageEquals(orgPackageElement, tree.getMatchingPackage(orgElement));
        assertPackageEquals(child1PackageElement, tree.getMatchingPackage(orgChild1Element));
        assertPackageEquals(child1PackageElement, tree.getMatchingPackage(orgChild1GrandchildElement));
        assertPackageEquals(child2PackageElement, tree.getMatchingPackage(orgChild2Element));
        assertPackageEquals(child1PackageElement, tree.getMatchingPackage(greatGrandChildElement));
    }

    private static void assertPackageEquals(PackageElement expected, PackageElement actual) {
        assertNotNull("Expected package should not be null", expected);
        assertNotNull(String.format(Locale.US, "Expect: '%s' but found null", expected.getQualifiedName().toString()),
                      actual);
        assertEquals(String.format(Locale.US, "Expected '%s' but found '%s'", expected.getQualifiedName().toString(),
                                   actual.getQualifiedName().toString()), expected, actual);
    }
}
