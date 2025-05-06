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

import org.apache.commons.lang3.reflect.TypeUtils;
import ru.rt.restream.reindexer.convert.FieldConverter;
import ru.rt.restream.reindexer.util.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * For internal use only, as this contract is likely to change.
 */
public final class ConversionUtils {

    private static final Map<Class<?>, ResolvableType> CONVERTER_TARGET_TYPE_CACHE = new ConcurrentHashMap<>();

    private static final Map<Pair<Class<?>, String>, ResolvableType> FIELD_TYPE_CACHE = new ConcurrentHashMap<>();

    /**
     * Resolves target type based on {@code Y} parameter from {@link FieldConverter} interface implementation.
     * If {@code FieldConverter} returns an array, the target type is determined by {@code Class#getComponentType},
     * in case of a generic return type, only {@code java.util.Collection} assignable types are considered,
     * nested generic types (i.e. {@code List<List<String>>}) are not supported, such types will not be recognized
     * and IllegalArgumentException will be thrown.
     * @param converterClass the {@link FieldConverter} interface implementation class to use
     * @return the {@link ResolvableType} to use
     */
    public static ResolvableType resolveConverterTargetType(Class<?> converterClass) {
        Objects.requireNonNull(converterClass, "converterClass must not be null");
        // https://bugs.openjdk.java.net/browse/JDK-8161372
        ResolvableType resolvableType = CONVERTER_TARGET_TYPE_CACHE.get(converterClass);
        if (resolvableType != null) {
            return resolvableType;
        }
        return CONVERTER_TARGET_TYPE_CACHE.computeIfAbsent(converterClass, k -> {
            Type type = TypeUtils.getTypeArguments(k, FieldConverter.class)
                    .get(FieldConverter.class.getTypeParameters()[1]);
            ResolvableType result = resolveType(type);
            if (result == null) {
                throw new IllegalArgumentException(
                        String.format("Cannot resolve FieldConverter: %s target type: %s", converterClass.getName(), type));
            }
            return result;
        });
    }

    /**
     * Resolves a field type. If the field type is an array, the target type is determined
     * by {@code Class#getComponentType}, in case of a generic return type, only {@code java.util.Collection}
     * assignable types are considered, nested generic types (i.e. {@code List<List<String>>})
     * are not supported, such types will not be recognized and IllegalArgumentException will be thrown.
     * @param field the field to use
     * @return the {@link ResolvableType} to use
     */
    public static ResolvableType resolveFieldType(Field field) {
        Objects.requireNonNull(field, "field must not be null");
        // https://bugs.openjdk.java.net/browse/JDK-8161372
        Pair<Class<?>, String> key = new Pair<>(field.getDeclaringClass(), field.getName());
        ResolvableType resolvableType = FIELD_TYPE_CACHE.get(key);
        if (resolvableType != null) {
            return resolvableType;
        }
        return FIELD_TYPE_CACHE.computeIfAbsent(key, k -> {
            ResolvableType result = resolveType(field.getGenericType());
            if (result == null) {
                throw new IllegalArgumentException(String.format("Cannot resolve Field: %s.%s target type: %s",
                        field.getDeclaringClass().getName(), field.getName(), field.getGenericType()));
            }
            return result;
        });
    }

    private static ResolvableType resolveType(Type type) {
        if (TypeUtils.isAssignable(type, Collection.class)) {
            Type typeArgument = TypeUtils.getTypeArguments(type, Collection.class)
                    .get(Collection.class.getTypeParameters()[0]);
            if (typeArgument instanceof Class<?>) {
                Class<?> containerType = TypeUtils.getRawType(type, Collection.class);
                return new ResolvableType(containerType, (Class<?>) typeArgument, true);
            }
            return null;
        }
        if (type instanceof Class<?>) {
            Class<?> targetType = (Class<?>) type;
            return new ResolvableType(targetType, targetType.getComponentType(), targetType.isArray());
        }
        return null;
    }

    private ConversionUtils() {
        // utils
    }
}
