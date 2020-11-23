package ru.rt.restream.reindexer;

import ru.rt.restream.reindexer.binding.Binding;
import ru.rt.restream.reindexer.binding.Consts;
import ru.rt.restream.reindexer.binding.QueryResult;
import ru.rt.restream.reindexer.binding.cproto.ByteBuffer;
import ru.rt.restream.reindexer.binding.cproto.CprotoIterator;

import java.util.Iterator;

import static ru.rt.restream.reindexer.binding.Consts.OP_AND;

public class Query<T> {

    public enum Condition {
        ANY(0),
        EQ(1),
        LT(2),
        LE(3),
        GT(4),
        GE(5),
        RANGE(6),
        SET(7),
        ALLSET(8),
        EMPTY(9);
        private final int code;

        Condition(int code) {
            this.code = code;
        }

    }

    private final Binding binding;

    private final ByteBuffer buffer = new ByteBuffer();

    private int nextOperation = OP_AND;

    private final Namespace<T> namespace;

    private int count;

    public Query(Binding binding, Namespace<T> namespace) {
        this.binding = binding;
        this.namespace = namespace;
        buffer.putVString(namespace.getName());
    }

    public Query<T> join(String joined) {
        return null;
    }

    public Query<T> where(String index, Condition condition, Object... values) {
        buffer.PutVarUInt32(Consts.QUERY_CONDITION)
                .putVString(index)
                .PutVarUInt32(nextOperation)
                .PutVarUInt32(condition.code);

        this.nextOperation = OP_AND;
        count++;

        if (values != null && values.length > 0) {
            buffer.PutVarUInt32(values.length);
            for (Object key : values) {
                putValue(key);
            }
        }

        return this;
    }

    private void putValue(Object value) {
        if (value instanceof Integer) {
            buffer.PutVarUInt32(Consts.VALUE_INT)
                    .putVarInt64((Integer) value);
        } else if (value instanceof String) {
            buffer.PutVarUInt32(Consts.VALUE_STRING)
                    .putVString((String) value);
        }
    }

    public Iterator<T> execute() {
        buffer.PutVarUInt32(Consts.QUERY_END);

        QueryResult queryResult = binding.selectQuery(buffer.bytes(), true, Integer.MAX_VALUE);

        return CprotoIterator.<T>builder()
                .namespace(namespace)
                .queryResult(queryResult)
                .build();
    }

}