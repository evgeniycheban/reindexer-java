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
import ru.rt.restream.reindexer.util.Pair;

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
    void resolveConverterTypePairWhenClassImplementsFieldConverterThenResolves() {
        Pair<ResolvableType, ResolvableType> typePair = ConversionUtils
                .resolveConverterTypePair(ClassImplementsFieldConverter.class);
        ResolvableType sourceType = typePair.getFirst();
        assertThat(sourceType, notNullValue());
        assertThat(sourceType.getType(), is(Integer.class));
        assertThat(sourceType.getComponentType(), nullValue());
        assertThat(sourceType.isCollectionLike(), is(false));
        ResolvableType targetType = typePair.getSecond();
        assertThat(targetType, notNullValue());
        assertThat(targetType.getType(), is(String.class));
        assertThat(targetType.getComponentType(), nullValue());
        assertThat(targetType.isCollectionLike(), is(false));
    }

    @Test
    void resolveConverterTypePairWhenClassImplementsMultipleInterfacesFieldConverterThenResolves() {
        Pair<ResolvableType, ResolvableType> typePair = ConversionUtils.resolveConverterTypePair(ClassImplementsMultipleInterfacesFieldConverter.class);
        ResolvableType sourceType = typePair.getFirst();
        assertThat(sourceType, notNullValue());
        assertThat(sourceType.getType(), is(Integer.class));
        assertThat(sourceType.getComponentType(), nullValue());
        assertThat(sourceType.isCollectionLike(), is(false));
        ResolvableType targetType = typePair.getSecond();
        assertThat(targetType, notNullValue());
        assertThat(targetType.getType(), is(String.class));
        assertThat(targetType.getComponentType(), nullValue());
        assertThat(targetType.isCollectionLike(), is(false));
    }

    @Test
    void resolveConverterTypePairWhenClassExtendsClassImplementingFieldConverterThenResolves() {
        Pair<ResolvableType, ResolvableType> typePair = ConversionUtils.resolveConverterTypePair(ClassExtendsClassImplementingFieldConverter.class);
        ResolvableType sourceType = typePair.getFirst();
        assertThat(sourceType, notNullValue());
        assertThat(sourceType.getType(), is(Integer.class));
        assertThat(sourceType.getComponentType(), nullValue());
        assertThat(sourceType.isCollectionLike(), is(false));
        ResolvableType targetType = typePair.getSecond();
        assertThat(targetType, notNullValue());
        assertThat(targetType.getType(), is(String.class));
        assertThat(targetType.getComponentType(), nullValue());
        assertThat(targetType.isCollectionLike(), is(false));
    }

    @Test
    void resolveConverterTypePairWhenClassExtendsClassImplementingMultipleInterfacesFieldConverterThenResolves() {
        Pair<ResolvableType, ResolvableType> typePair = ConversionUtils.resolveConverterTypePair(ClassExtendsClassImplementingMultipleInterfacesFieldConverter.class);
        ResolvableType sourceType = typePair.getFirst();
        assertThat(sourceType, notNullValue());
        assertThat(sourceType.getType(), is(Integer.class));
        assertThat(sourceType.getComponentType(), nullValue());
        assertThat(sourceType.isCollectionLike(), is(false));
        ResolvableType targetType = typePair.getSecond();
        assertThat(targetType, notNullValue());
        assertThat(targetType.getType(), is(String.class));
        assertThat(targetType.getComponentType(), nullValue());
        assertThat(targetType.isCollectionLike(), is(false));
    }

    @Test
    void resolveConverterTypePairWhenClassExtendsGenericConverterThenResolves() {
        Pair<ResolvableType, ResolvableType> typePair = ConversionUtils.resolveConverterTypePair(ClassExtendsGenericFieldConverter.class);
        ResolvableType sourceType = typePair.getFirst();
        assertThat(sourceType, notNullValue());
        assertThat(sourceType.getType(), is(Integer.class));
        assertThat(sourceType.getComponentType(), nullValue());
        assertThat(sourceType.isCollectionLike(), is(false));
        ResolvableType targetType = typePair.getSecond();
        assertThat(targetType, notNullValue());
        assertThat(targetType.getType(), is(String.class));
        assertThat(targetType.getComponentType(), nullValue());
        assertThat(targetType.isCollectionLike(), is(false));
    }

    @Test
    void resolveConverterTypePairWhenClassExtendsClassExtendingGenericFieldConverterThenResolves() {
        Pair<ResolvableType, ResolvableType> typePair = ConversionUtils.resolveConverterTypePair(ClassExtendsClassExtendingGenericFieldConverter.class);
        ResolvableType sourceType = typePair.getFirst();
        assertThat(sourceType, notNullValue());
        assertThat(sourceType.getType(), is(Integer.class));
        assertThat(sourceType.getComponentType(), nullValue());
        assertThat(sourceType.isCollectionLike(), is(false));
        ResolvableType targetType = typePair.getSecond();
        assertThat(targetType, notNullValue());
        assertThat(targetType.getType(), is(String.class));
        assertThat(targetType.getComponentType(), nullValue());
        assertThat(targetType.isCollectionLike(), is(false));
    }

    @Test
    void resolveConverterTypePairWhenContainerTypeListFieldConverterThenResolves() {
        Pair<ResolvableType, ResolvableType> typePair = ConversionUtils.resolveConverterTypePair(ContainerTypeListFieldConverter.class);
        ResolvableType sourceType = typePair.getFirst();
        assertThat(sourceType, notNullValue());
        assertThat(sourceType.getType(), is(List.class));
        assertThat(sourceType.getComponentType(), is(Integer.class));
        assertThat(sourceType.isCollectionLike(), is(true));
        ResolvableType targetType = typePair.getSecond();
        assertThat(targetType, notNullValue());
        assertThat(targetType.getType(), is(List.class));
        assertThat(targetType.getComponentType(), is(String.class));
        assertThat(targetType.isCollectionLike(), is(true));
    }

    @Test
    void resolveConverterTypePairWhenContainerTypeSetFieldConverterThenResolves() {
        Pair<ResolvableType, ResolvableType> typePair = ConversionUtils.resolveConverterTypePair(ContainerTypeSetFieldConverter.class);
        ResolvableType sourceType = typePair.getFirst();
        assertThat(sourceType, notNullValue());
        assertThat(sourceType.getType(), is(List.class));
        assertThat(sourceType.getComponentType(), is(Integer.class));
        assertThat(sourceType.isCollectionLike(), is(true));
        ResolvableType targetType = typePair.getSecond();
        assertThat(targetType, notNullValue());
        assertThat(targetType.getType(), is(Set.class));
        assertThat(targetType.getComponentType(), is(String.class));
        assertThat(targetType.isCollectionLike(), is(true));
    }

    @Test
    void resolveConverterTypePairWhenContainerTypeCollectionFieldConverterThenResolves() {
        Pair<ResolvableType, ResolvableType> typePair = ConversionUtils.resolveConverterTypePair(ContainerTypeCollectionFieldConverter.class);
        ResolvableType sourceType = typePair.getFirst();
        assertThat(sourceType, notNullValue());
        assertThat(sourceType.getType(), is(Collection.class));
        assertThat(sourceType.getComponentType(), is(Integer.class));
        assertThat(sourceType.isCollectionLike(), is(true));
        ResolvableType targetType = typePair.getSecond();
        assertThat(targetType, notNullValue());
        assertThat(targetType.getType(), is(Collection.class));
        assertThat(targetType.getComponentType(), is(String.class));
        assertThat(targetType.isCollectionLike(), is(true));
    }

    @Test
    void resolveConverterTypePairWhenArrayTypeFieldConverterThenResolves() {
        Pair<ResolvableType, ResolvableType> typePair = ConversionUtils.resolveConverterTypePair(ArrayTypeFieldConverter.class);
        ResolvableType sourceType = typePair.getFirst();
        assertThat(sourceType, notNullValue());
        assertThat(sourceType.getType(), is(Integer[].class));
        assertThat(sourceType.getComponentType(), is(Integer.class));
        assertThat(sourceType.isCollectionLike(), is(true));
        ResolvableType targetType = typePair.getSecond();
        assertThat(targetType, notNullValue());
        assertThat(targetType.getType(), is(String[].class));
        assertThat(targetType.getComponentType(), is(String.class));
        assertThat(targetType.isCollectionLike(), is(true));
    }

    @Test
    void resolveConverterTypePairWhenContainerSourceTypeOptionalFieldConverterThenException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ConversionUtils.resolveConverterTypePair(ContainerSourceTypeOptionalFieldConverter.class));
        String expectedMessage = "Cannot resolve FieldConverter: " + ContainerSourceTypeOptionalFieldConverter.class.getName()
                                 + " source type: java.util.Optional<java.lang.String>";
        assertThat(exception.getMessage(), is(expectedMessage));
    }

    @Test
    void resolveConverterTypePairWhenContainerSourceTypeRawCollectionFieldConverterThenException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ConversionUtils.resolveConverterTypePair(ContainerSourceTypeRawCollectionFieldConverter.class));
        String expectedMessage = "Cannot resolve FieldConverter: " + ContainerSourceTypeRawCollectionFieldConverter.class.getName()
                                 + " source type: interface java.util.Collection";
        assertThat(exception.getMessage(), is(expectedMessage));
    }

    @Test
    void resolveConverterTypePairWhenContainerSourceTypeGenericCollectionFieldConverterThenException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ConversionUtils.resolveConverterTypePair(ContainerSourceTypeGenericCollectionFieldConverter.class));
        String expectedMessage = "Cannot resolve FieldConverter: " + ContainerSourceTypeGenericCollectionFieldConverter.class.getName()
                                 + " source type: java.util.Collection<T>";
        assertThat(exception.getMessage(), is(expectedMessage));
    }

    @Test
    void resolveConverterTypePairWhenContainerSourceTypeWildcardCollectionFieldConverterThenException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ConversionUtils.resolveConverterTypePair(ContainerSourceTypeWildcardCollectionFieldConverter.class));
        String expectedMessage = "Cannot resolve FieldConverter: " + ContainerSourceTypeWildcardCollectionFieldConverter.class.getName()
                                 + " source type: java.util.Collection<?>";
        assertThat(exception.getMessage(), is(expectedMessage));
    }

    @Test
    void resolveConverterTypePairWhenContainerSourceTypeGenericArrayFieldConverterThenException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ConversionUtils.resolveConverterTypePair(ContainerSourceTypeGenericArrayFieldConverter.class));
        String expectedMessage = "Cannot resolve FieldConverter: " + ContainerSourceTypeGenericArrayFieldConverter.class.getName()
                                 + " source type: T[]";
        assertThat(exception.getMessage(), is(expectedMessage));
    }

    @Test
    void resolveConverterTypePairWhenContainerSourceTypeGenericFieldConverterThenException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ConversionUtils.resolveConverterTypePair(ContainerSourceTypeGenericFieldConverter.class));
        String expectedMessage = "Cannot resolve FieldConverter: " + ContainerSourceTypeGenericFieldConverter.class.getName()
                                 + " source type: T";
        assertThat(exception.getMessage(), is(expectedMessage));
    }

    @Test
    void resolveConverterTypePairWhenContainerTargetTypeOptionalFieldConverterThenException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ConversionUtils.resolveConverterTypePair(ContainerTypeOptionalFieldConverter.class));
        String expectedMessage = "Cannot resolve FieldConverter: " + ContainerTypeOptionalFieldConverter.class.getName()
                                 + " target type: java.util.Optional<java.lang.String>";
        assertThat(exception.getMessage(), is(expectedMessage));
    }

    @Test
    void resolveConverterTypePairWhenContainerTargetTypeRawCollectionFieldConverterThenException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ConversionUtils.resolveConverterTypePair(ContainerTargetTypeRawCollectionFieldConverter.class));
        String expectedMessage = "Cannot resolve FieldConverter: " + ContainerTargetTypeRawCollectionFieldConverter.class.getName()
                                 + " target type: interface java.util.Collection";
        assertThat(exception.getMessage(), is(expectedMessage));
    }

    @Test
    void resolveConverterTypePairWhenContainerTargetTypeGenericCollectionFieldConverterThenException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ConversionUtils.resolveConverterTypePair(ContainerTargetTypeGenericCollectionFieldConverter.class));
        String expectedMessage = "Cannot resolve FieldConverter: " + ContainerTargetTypeGenericCollectionFieldConverter.class.getName()
                                 + " target type: java.util.Collection<T>";
        assertThat(exception.getMessage(), is(expectedMessage));
    }

    @Test
    void resolveConverterTypePairWhenContainerTargetTypeWildcardCollectionFieldConverterThenException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ConversionUtils.resolveConverterTypePair(ContainerTargetTypeWildcardCollectionFieldConverter.class));
        String expectedMessage = "Cannot resolve FieldConverter: " + ContainerTargetTypeWildcardCollectionFieldConverter.class.getName()
                                 + " target type: java.util.Collection<?>";
        assertThat(exception.getMessage(), is(expectedMessage));
    }

    @Test
    void resolveConverterTypePairWhenContainerTargetTypeGenericArrayFieldConverterThenException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ConversionUtils.resolveConverterTypePair(ContainerTargetTypeGenericArrayFieldConverter.class));
        String expectedMessage = "Cannot resolve FieldConverter: " + ContainerTargetTypeGenericArrayFieldConverter.class.getName()
                                 + " target type: T[]";
        assertThat(exception.getMessage(), is(expectedMessage));
    }

    @Test
    void resolveConverterTypePairWhenContainerTargetTypeGenericFieldConverterThenException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ConversionUtils.resolveConverterTypePair(ContainerTargetTypeGenericFieldConverter.class));
        String expectedMessage = "Cannot resolve FieldConverter: " + ContainerTargetTypeGenericFieldConverter.class.getName()
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

    static class ContainerTypeCollectionFieldConverter extends GenericFieldConverter<Collection<Integer>, Collection<String>> {
    }

    static class ArrayTypeFieldConverter extends GenericFieldConverter<Integer[], String[]> {
    }

    static class ContainerSourceTypeOptionalFieldConverter extends GenericFieldConverter<Optional<String>, List<Integer>> {
    }

    static class ContainerSourceTypeRawCollectionFieldConverter extends GenericFieldConverter<Collection, List<Integer>> {
    }

    static class ContainerSourceTypeGenericCollectionFieldConverter<T> extends GenericFieldConverter<Collection<T>, List<Integer>> {
    }

    static class ContainerSourceTypeWildcardCollectionFieldConverter extends GenericFieldConverter<Collection<?>, List<Integer>> {
    }

    static class ContainerSourceTypeGenericArrayFieldConverter<T> extends GenericFieldConverter<T[], List<Integer>> {
    }

    static class ContainerSourceTypeGenericFieldConverter<T> extends GenericFieldConverter<T, List<Integer>> {
    }

    static class ContainerTypeOptionalFieldConverter extends GenericFieldConverter<List<Integer>, Optional<String>> {
    }

    static class ContainerTargetTypeRawCollectionFieldConverter extends GenericFieldConverter<List<Integer>, Collection> {
    }

    static class ContainerTargetTypeGenericCollectionFieldConverter<T> extends GenericFieldConverter<List<Integer>, Collection<T>> {
    }

    static class ContainerTargetTypeWildcardCollectionFieldConverter extends GenericFieldConverter<List<Integer>, Collection<?>> {
    }

    static class ContainerTargetTypeGenericArrayFieldConverter<T> extends GenericFieldConverter<List<Integer>, T[]> {
    }

    static class ContainerTargetTypeGenericFieldConverter<T> extends GenericFieldConverter<List<Integer>, T> {
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
