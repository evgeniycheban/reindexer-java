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
package ru.rt.restream.reindexer.convert;

import lombok.Getter;
import ru.rt.restream.reindexer.convert.util.ResolvableType;
import ru.rt.restream.reindexer.util.Pair;

import java.util.Objects;

/**
 * A generic {@link FieldConverter} with resolved source and target types accessible via
 * {@code GenericFieldConverter#getConvertiblePair}.
 * @param <X> the field type used within the POJO
 * @param <Y> the Reindexer stored database type
 */
public final class GenericFieldConverter<X, Y> implements FieldConverter<X, Y> {

    private final FieldConverter<X, Y> delegate;

    @Getter
    private final Pair<ResolvableType, ResolvableType> convertiblePair;

    /**
     * Creates an instance.
     *
     * @param delegate the {@link FieldConverter} to use
     * @param convertiblePair the {@link Pair} of source and target {@link ResolvableType}s to use
     */
    public GenericFieldConverter(FieldConverter<X, Y> delegate, Pair<ResolvableType, ResolvableType> convertiblePair) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
        this.convertiblePair = Objects.requireNonNull(convertiblePair, "convertiblePair must not be null");
    }

    @Override
    public X convertToFieldType(Y dbData) {
        return this.delegate.convertToFieldType(dbData);
    }

    @Override
    public Y convertToDatabaseType(X field) {
        return this.delegate.convertToDatabaseType(field);
    }

    Class<?> getConverterClass() {
        if (delegate instanceof GenericFieldConverter) {
            return ((GenericFieldConverter<X, Y>) delegate).getConverterClass();
        }
        return delegate.getClass();
    }

    /**
     * For testing purposes only.
     */
    FieldConverter<X, Y> getDelegate() {
        return delegate;
    }
}
