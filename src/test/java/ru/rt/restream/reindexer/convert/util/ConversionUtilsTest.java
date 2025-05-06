/*
 * Copyright 2020 Restream
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.rt.restream.reindexer.convert.util;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import ru.rt.restream.reindexer.convert.FieldConverter;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link ConversionUtils}
 */
public class ConversionUtilsTest {

    @Test
    void resolveConverterTargetTypeWhenClassImplementsFieldConverterThenResolves() {
        ResolvableType resolvableType = ConversionUtils.resolveConverterTargetType(ClassImplementsFieldConverter.class);
        assertThat(resolvableType, notNullValue());
        assertThat(resolvableType.getType(), is(String.class));
        assertThat(resolvableType.getComponentType(), nullValue());
        assertThat(resolvableType.isCollectionLike(), is(false));
    }

    @Test
    void resolveConverterTargetTypeWhenClassImplementsMultipleInterfacesFieldConverterThenResolves() {
        ResolvableType resolvableType = ConversionUtils.resolveConverterTargetType(ClassImplementsMultipleInterfacesFieldConverter.class);
        assertThat(resolvableType, notNullValue());
        assertThat(resolvableType.getType(), is(String.class));
        assertThat(resolvableType.getComponentType(), nullValue());
        assertThat(resolvableType.isCollectionLike(), is(false));
    }

    @Test
    void resolveConverterTargetTypeWhenClassExtendsClassImplementingFieldConverterThenResolves() {
        ResolvableType resolvableType = ConversionUtils.resolveConverterTargetType(ClassExtendsClassImplementingFieldConverter.class);
        assertThat(resolvableType, notNullValue());
        assertThat(resolvableType.getType(), is(String.class));
        assertThat(resolvableType.getComponentType(), nullValue());
        assertThat(resolvableType.isCollectionLike(), is(false));
    }

    @Test
    void resolveConverterTargetTypeWhenClassExtendsClassImplementingMultipleInterfacesFieldConverterThenResolves() {
        ResolvableType resolvableType = ConversionUtils.resolveConverterTargetType(ClassExtendsClassImplementingMultipleInterfacesFieldConverter.class);
        assertThat(resolvableType, notNullValue());
        assertThat(resolvableType.getType(), is(String.class));
        assertThat(resolvableType.getComponentType(), nullValue());
        assertThat(resolvableType.isCollectionLike(), is(false));
    }

    @Test
    void resolveConverterTargetTypeWhenClassExtendsGenericConverterThenResolves() {
        ResolvableType resolvableType = ConversionUtils.resolveConverterTargetType(ClassExtendsGenericFieldConverter.class);
        assertThat(resolvableType, notNullValue());
        assertThat(resolvableType.getType(), is(String.class));
        assertThat(resolvableType.getComponentType(), nullValue());
        assertThat(resolvableType.isCollectionLike(), is(false));
    }

    @Test
    void resolveConverterTargetTypeWhenClassExtendsClassExtendingGenericFieldConverterThenResolves() {
        ResolvableType resolvableType = ConversionUtils.resolveConverterTargetType(ClassExtendsClassExtendingGenericFieldConverter.class);
        assertThat(resolvableType, notNullValue());
        assertThat(resolvableType.getType(), is(String.class));
        assertThat(resolvableType.getComponentType(), nullValue());
        assertThat(resolvableType.isCollectionLike(), is(false));
    }

    @Test
    void resolveConverterTargetTypeWhenContainerTypeListFieldConverterThenResolves() {
        ResolvableType resolvableType = ConversionUtils.resolveConverterTargetType(ContainerTypeListFieldConverter.class);
        assertThat(resolvableType, notNullValue());
        assertThat(resolvableType.getType(), is(List.class));
        assertThat(resolvableType.getComponentType(), is(String.class));
        assertThat(resolvableType.isCollectionLike(), is(true));
    }

    @Test
    void resolveConverterTargetTypeWhenContainerTypeSetFieldConverterThenResolves() {
        ResolvableType resolvableType = ConversionUtils.resolveConverterTargetType(ContainerTypeSetFieldConverter.class);
        assertThat(resolvableType, notNullValue());
        assertThat(resolvableType.getType(), is(Set.class));
        assertThat(resolvableType.getComponentType(), is(String.class));
        assertThat(resolvableType.isCollectionLike(), is(true));
    }

    @Test
    void resolveConverterTargetTypeWhenContainerTypeCollectionFieldConverterThenResolves() {
        ResolvableType resolvableType = ConversionUtils.resolveConverterTargetType(ContainerTypeCollectionFieldConverter.class);
        assertThat(resolvableType, notNullValue());
        assertThat(resolvableType.getType(), is(Collection.class));
        assertThat(resolvableType.getComponentType(), is(String.class));
        assertThat(resolvableType.isCollectionLike(), is(true));
    }

    @Test
    void resolveConverterTargetTypeWhenArrayTypeFieldConverterThenResolves() {
        ResolvableType resolvableType = ConversionUtils.resolveConverterTargetType(ArrayTypeFieldConverter.class);
        assertThat(resolvableType, notNullValue());
        assertThat(resolvableType.getType(), is(String[].class));
        assertThat(resolvableType.getComponentType(), is(String.class));
        assertThat(resolvableType.isCollectionLike(), is(true));
    }

    @Test
    void resolveConverterTargetTypeWhenContainerTypeOptionalFieldConverterThenException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ConversionUtils.resolveConverterTargetType(ContainerTypeOptionalFieldConverter.class));
        String expectedMessage = "Cannot resolve FieldConverter: " + ContainerTypeOptionalFieldConverter.class.getName()
                                 + " target type: java.util.Optional<java.lang.String>";
        assertThat(exception.getMessage(), is(expectedMessage));
    }

    @Test
    void resolveConverterTargetTypeWhenContainerTypeRawCollectionFieldConverterThenException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ConversionUtils.resolveConverterTargetType(ContainerTypeRawCollectionFieldConverter.class));
        String expectedMessage = "Cannot resolve FieldConverter: " + ContainerTypeRawCollectionFieldConverter.class.getName()
                                 + " target type: interface java.util.Collection";
        assertThat(exception.getMessage(), is(expectedMessage));
    }

    @Test
    void resolveConverterTargetTypeWhenContainerTypeGenericCollectionFieldConverterThenException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ConversionUtils.resolveConverterTargetType(ContainerTypeGenericCollectionFieldConverter.class));
        String expectedMessage = "Cannot resolve FieldConverter: " + ContainerTypeGenericCollectionFieldConverter.class.getName()
                                 + " target type: java.util.Collection<T>";
        assertThat(exception.getMessage(), is(expectedMessage));
    }

    @Test
    void resolveConverterTargetTypeWhenContainerTypeWildcardCollectionFieldConverterThenException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ConversionUtils.resolveConverterTargetType(ContainerTypeWildcardCollectionFieldConverter.class));
        String expectedMessage = "Cannot resolve FieldConverter: " + ContainerTypeWildcardCollectionFieldConverter.class.getName()
                                 + " target type: java.util.Collection<?>";
        assertThat(exception.getMessage(), is(expectedMessage));
    }

    @Test
    void resolveConverterTargetTypeWhenContainerTypeGenericArrayFieldConverterThenException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ConversionUtils.resolveConverterTargetType(ContainerTypeGenericArrayFieldConverter.class));
        String expectedMessage = "Cannot resolve FieldConverter: " + ContainerTypeGenericArrayFieldConverter.class.getName()
                                 + " target type: T[]";
        assertThat(exception.getMessage(), is(expectedMessage));
    }

    @Test
    void resolveConverterTargetTypeWhenContainerTypeGenericFieldConverterThenException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ConversionUtils.resolveConverterTargetType(ContainerTypeGenericFieldConverter.class));
        String expectedMessage = "Cannot resolve FieldConverter: " + ContainerTypeGenericFieldConverter.class.getName()
                                 + " target type: T";
        assertThat(exception.getMessage(), is(expectedMessage));
    }

    @Test
    void resolveFieldTargetTypeWhenSimpleFieldThenResolves() {
        ResolvableType resolvableType = ConversionUtils.resolveFieldType(getField("simpleField"));
        assertThat(resolvableType, notNullValue());
        assertThat(resolvableType.getType(), is(String.class));
        assertThat(resolvableType.getComponentType(), nullValue());
        assertThat(resolvableType.isCollectionLike(), is(false));
    }

    @Test
    void resolveFieldTargetTypeWhenListFieldThenResolves() {
        ResolvableType resolvableType = ConversionUtils.resolveFieldType(getField("listField"));
        assertThat(resolvableType, notNullValue());
        assertThat(resolvableType.getType(), is(List.class));
        assertThat(resolvableType.getComponentType(), is(String.class));
        assertThat(resolvableType.isCollectionLike(), is(true));
    }

    @Test
    void resolveFieldTargetTypeWhenSetFieldThenResolves() {
        ResolvableType resolvableType = ConversionUtils.resolveFieldType(getField("setField"));
        assertThat(resolvableType, notNullValue());
        assertThat(resolvableType.getType(), is(Set.class));
        assertThat(resolvableType.getComponentType(), is(String.class));
        assertThat(resolvableType.isCollectionLike(), is(true));
    }

    @Test
    void resolveFieldTargetTypeWhenCollectionFieldThenResolves() {
        ResolvableType resolvableType = ConversionUtils.resolveFieldType(getField("collectionField"));
        assertThat(resolvableType, notNullValue());
        assertThat(resolvableType.getType(), is(Collection.class));
        assertThat(resolvableType.getComponentType(), is(String.class));
        assertThat(resolvableType.isCollectionLike(), is(true));
    }

    @Test
    void resolveFieldTargetTypeWhenArrayFieldThenResolves() {
        ResolvableType resolvableType = ConversionUtils.resolveFieldType(getField("arrayField"));
        assertThat(resolvableType, notNullValue());
        assertThat(resolvableType.getType(), is(String[].class));
        assertThat(resolvableType.getComponentType(), is(String.class));
        assertThat(resolvableType.isCollectionLike(), is(true));
    }

    @Test
    void resolveFieldTargetTypeWhenOptionalFieldThenException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ConversionUtils.resolveFieldType(getField("optionalField")));
        String expectedMessage = "Cannot resolve Field: " + TestPojo.class.getName() + ".optionalField"
                                 + " target type: java.util.Optional<java.lang.String>";
        assertThat(exception.getMessage(), is(expectedMessage));
    }

    @Test
    void resolveFieldTargetTypeWhenRawCollectionFieldThenException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ConversionUtils.resolveFieldType(getField("rawCollectionField")));
        String expectedMessage = "Cannot resolve Field: " + TestPojo.class.getName() + ".rawCollectionField"
                                 + " target type: interface java.util.Collection";
        assertThat(exception.getMessage(), is(expectedMessage));
    }

    @Test
    void resolveFieldTargetTypeWhenGenericCollectionFieldThenException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ConversionUtils.resolveFieldType(getField("genericCollectionField")));
        String expectedMessage = "Cannot resolve Field: " + TestPojo.class.getName() + ".genericCollectionField"
                                 + " target type: java.util.Collection<T>";
        assertThat(exception.getMessage(), is(expectedMessage));
    }

    @Test
    void resolveFieldTargetTypeWhenWildcardCollectionFieldThenException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ConversionUtils.resolveFieldType(getField("wildcardCollectionField")));
        String expectedMessage = "Cannot resolve Field: " + TestPojo.class.getName() + ".wildcardCollectionField"
                                 + " target type: java.util.Collection<?>";
        assertThat(exception.getMessage(), is(expectedMessage));
    }

    @Test
    void resolveFieldTargetTypeWhenGenericArrayFieldThenException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ConversionUtils.resolveFieldType(getField("genericArrayField")));
        String expectedMessage = "Cannot resolve Field: " + TestPojo.class.getName() + ".genericArrayField"
                                 + " target type: T[]";
        assertThat(exception.getMessage(), is(expectedMessage));
    }

    @Test
    void resolveFieldTargetTypeWhenGenericFieldThenException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ConversionUtils.resolveFieldType(getField("genericField")));
        String expectedMessage = "Cannot resolve Field: " + TestPojo.class.getName() + ".genericField"
                                 + " target type: T";
        assertThat(exception.getMessage(), is(expectedMessage));
    }

    private static Field getField(String name) {
        return FieldUtils.getDeclaredField(TestPojo.class, name, true);
    }

    static class ClassImplementsFieldConverter implements FieldConverter<Integer, String> {

        @Override
        public Integer convertToFieldType(String dbData) {
            return 0;
        }

        @Override
        public String convertToDatabaseType(Integer field) {
            return "";
        }
    }

    static class ClassImplementsMultipleInterfacesFieldConverter implements Supplier<String>, Callable<String>, FieldConverter<Integer, String> {

        @Override
        public Integer convertToFieldType(String dbData) {
            return 0;
        }

        @Override
        public String convertToDatabaseType(Integer field) {
            return "";
        }

        @Override
        public String get() {
            return "";
        }

        @Override
        public String call() {
            return "";
        }
    }

    static class ClassExtendsClassImplementingFieldConverter extends ClassImplementsFieldConverter {
    }

    static class ClassExtendsClassImplementingMultipleInterfacesFieldConverter extends ClassImplementsMultipleInterfacesFieldConverter {
    }

    static class GenericFieldConverter<X, Y> implements FieldConverter<X, Y> {

        @Override
        public X convertToFieldType(Y dbData) {
            return null;
        }

        @Override
        public Y convertToDatabaseType(X field) {
            return null;
        }
    }

    static class ClassExtendsGenericFieldConverter extends GenericFieldConverter<Integer, String> {
    }

    static class ClassExtendsClassExtendingGenericFieldConverter extends ClassExtendsGenericFieldConverter {
    }

    static class ContainerTypeListFieldConverter extends GenericFieldConverter<List<Integer>, List<String>> {
    }

    static class ContainerTypeSetFieldConverter extends GenericFieldConverter<List<Integer>, Set<String>> {
    }

    static class ContainerTypeCollectionFieldConverter extends GenericFieldConverter<List<Integer>, Collection<String>> {
    }

    static class ArrayTypeFieldConverter extends GenericFieldConverter<List<Integer>, String[]> {
    }

    static class ContainerTypeOptionalFieldConverter extends GenericFieldConverter<List<Integer>, Optional<String>> {
    }

    static class ContainerTypeRawCollectionFieldConverter extends GenericFieldConverter<List<Integer>, Collection> {
    }

    static class ContainerTypeGenericCollectionFieldConverter<T> extends GenericFieldConverter<List<Integer>, Collection<T>> {
    }

    static class ContainerTypeWildcardCollectionFieldConverter extends GenericFieldConverter<List<Integer>, Collection<?>> {
    }

    static class ContainerTypeGenericArrayFieldConverter<T> extends GenericFieldConverter<List<Integer>, T[]> {
    }

    static class ContainerTypeGenericFieldConverter<T> extends GenericFieldConverter<List<Integer>, T> {
    }

    static class TestPojo<T> {
        String simpleField;
        List<String> listField;
        Set<String> setField;
        Collection<String> collectionField;
        String[] arrayField;
        Optional<String> optionalField;
        Collection rawCollectionField;
        Collection<T> genericCollectionField;
        Collection<?> wildcardCollectionField;
        T[] genericArrayField;
        T genericField;
    }
}
