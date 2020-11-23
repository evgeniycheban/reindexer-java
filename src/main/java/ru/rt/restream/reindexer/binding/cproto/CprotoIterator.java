package ru.rt.restream.reindexer.binding.cproto;

import lombok.Builder;
import ru.rt.restream.reindexer.Namespace;
import ru.rt.restream.reindexer.binding.QueryResult;
import ru.rt.restream.reindexer.binding.cproto.json.JsonItemReader;
import ru.rt.restream.reindexer.exceptions.UnimplementedException;

import java.util.Iterator;

/**
 * An iterator over a query result.
 * Maintains a cursor pointing to its current row of data. Initially the cursor is positioned before the first row.
 */
public class CprotoIterator<T> implements Iterator<T> {

    private static final int RESULTS_FORMAT_MASK = 0xF;

    private static final int RESULT_JSON = 0x3;

    private final Namespace<T> namespace;

    private final Connection connection;

    private ItemReader<T> itemReader;

    private ByteBuffer buffer;

    private long requestId;

    private long flags;

    private long totalCount;

    private long qCount;

    private long count;

    private int position;

    @Builder
    public CprotoIterator(Connection connection,
                          Namespace<T> namespace,
                          QueryResult queryResult) {
        this.connection = connection;
        this.namespace = namespace;
        parseQueryResult(queryResult);
    }

    private void parseQueryResult(QueryResult queryResult) {
        this.requestId = queryResult.getRequestId();
        byte[] queryData = queryResult.getQueryData();
        if (queryData.length > 0) {
            buffer = new ByteBuffer(queryData);
            buffer.rewind();
            this.flags = buffer.getVarUInt();
            this.totalCount = buffer.getVarUInt();
            this.qCount = buffer.getVarUInt();
            this.count = buffer.getVarUInt();
            long tag = buffer.getVarUInt();
            if ((flags & RESULTS_FORMAT_MASK) == RESULT_JSON) {
                itemReader = new JsonItemReader<>(namespace);
            } else {
                throw new UnimplementedException();
            }
        }
    }

    @Override
    public boolean hasNext() {
        return position < count;
    }

    /**
     * Read next item. Moves the cursor to the next row.
     *
     * @return read item
     */
    public T next() {
        if (!hasNext()) {
            throw new IllegalStateException("No data to read");
        }

        T item = itemReader.readItem(buffer);

        position++;

        return item;

    }

}