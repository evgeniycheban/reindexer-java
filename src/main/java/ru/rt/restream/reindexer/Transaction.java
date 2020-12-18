package ru.rt.restream.reindexer;

import ru.rt.restream.reindexer.binding.Binding;
import ru.rt.restream.reindexer.binding.Consts;
import ru.rt.restream.reindexer.binding.TransactionContext;
import ru.rt.restream.reindexer.binding.cproto.ByteBuffer;
import ru.rt.restream.reindexer.binding.cproto.ItemWriter;
import ru.rt.restream.reindexer.binding.cproto.json.JsonItemWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * An object that represents the context of a transaction.
 */
public class Transaction<T> {

    /**
     * Namespace in which the transaction is executed.
     */
    private final ReindexerNamespace<T> namespace;

    /**
     * Binding to Reindexer instance.
     */
    private final Binding binding;

    /**
     * The completion service to wait for a {@link DeferredResult} of type {@link T}.
     */
    private final CompletionService<DeferredResult<T>> completionService;

    /**
     * The futures list.
     */
    private final List<Future<DeferredResult<T>>> futures = new ArrayList<>();

    /**
     * The {@link ItemWriter} for JSON serialization.
     */
    private final ItemWriter<T> itemWriter = new JsonItemWriter<>();

    /**
     * Indicates that the current transaction is started.
     */
    private boolean started;

    /**
     * Indicates that the current transaction is finalized.
     */
    private boolean finalized;

    /**
     * The async request error.
     */
    private Exception asyncError;

    /**
     * The transaction context.
     */
    private TransactionContext transactionContext;

    /**
     * Creates an instance.
     *
     * @param namespace the namespace
     * @param binding   a binding to Reindexer instance
     * @param executor  executor to run async requests
     */
    public Transaction(ReindexerNamespace<T> namespace, Binding binding, Executor executor) {
        this.namespace = namespace;
        this.binding = binding;
        this.completionService = new ExecutorCompletionService<>(executor);
    }

    /**
     * Starts a transaction.
     *
     * @throws IllegalStateException if the current transaction is finalized
     */
    public void start() {
        checkFinalized();
        if (started) {
            return;
        }
        transactionContext = binding.beginTx(namespace.getName());
        started = true;
    }

    /**
     * Commits the current transaction.
     * Waits for worker threads to finish processing async requests.
     *
     * @throws IllegalStateException if the current transaction is finalized
     * @throws RuntimeException      if there is an error while processing async requests
     */
    public void commit() {
        checkFinalized();
        if (!started) {
            return;
        }
        awaitResults();
        checkAsyncError();
        transactionContext.commit();
        transactionContext.close();
        finalized = true;
    }

    /**
     * Rollbacks the current transaction.
     * Waits for worker threads to finish processing async requests.
     * It is safe to call rollback after commit.
     */
    public void rollback() {
        if (!started || finalized) {
            return;
        }
        awaitResults();
        transactionContext.rollback();
        transactionContext.close();
        asyncError = null;
        finalized = true;
    }

    private void checkFinalized() {
        if (finalized) {
            throw new IllegalStateException("Transaction is finalized");
        }
    }

    private void checkAsyncError() {
        if (asyncError != null) {
            throw new RuntimeException(asyncError);
        }
    }

    private void awaitResults() {
        try {
            while (!futures.isEmpty()) {
                Future<DeferredResult<T>> future = completionService.take();
                futures.remove(future);
                DeferredResult<T> result = future.get();
                if (result.hasError()) {
                    asyncError = result.getError();
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            asyncError = e;
        } catch (ExecutionException e) {
            asyncError = e;
        } finally {
            futures.forEach(f -> f.cancel(true));
            futures.clear();
        }
    }

    /**
     * Inserts the given item data in the current transaction.
     * Starts a transaction if not started.
     *
     * @param item the item data
     * @throws IllegalStateException if the current transaction is finalized
     */
    public void insert(T item) {
        start();
        modifyItem(item, Reindexer.MODE_INSERT);
    }

    /**
     * Updates the given item data in the current transaction.
     * Starts a transaction if not started.
     *
     * @param item the item data
     * @throws IllegalStateException if the current transaction is finalized
     */
    public void update(T item) {
        start();
        modifyItem(item, Reindexer.MODE_UPDATE);
    }

    /**
     * Inserts or updates the given item data in the current transaction.
     * Starts a transaction if not started.
     *
     * @param item the item data
     * @throws IllegalStateException if the current transaction is finalized
     */
    public void upsert(T item) {
        start();
        modifyItem(item, Reindexer.MODE_UPSERT);
    }

    /**
     * Deletes the given item data in the current transaction.
     * Starts a transaction if not started.
     *
     * @param item the item data
     * @throws IllegalStateException if the current transaction is finalized
     */
    public void delete(T item) {
        start();
        modifyItem(item, Reindexer.MODE_DELETE);
    }

    /**
     * Inserts the given item data in the current transaction asynchronously.
     * Starts a transaction if not started.
     *
     * @param item     the item data
     * @param callback the {@link Consumer} which accepts a {@link DeferredResult} of type {@link T}
     * @throws IllegalStateException if the current transaction is finalized
     */
    public void insertAsync(T item, Consumer<DeferredResult<T>> callback) {
        start();
        modifyItemAsync(item, Reindexer.MODE_INSERT, callback);
    }

    /**
     * Updates the given item data in the current transaction asynchronously.
     * Starts a transaction if not started.
     *
     * @param item     the item data
     * @param callback the {@link Consumer} which accepts a {@link DeferredResult} of type {@link T}
     * @throws IllegalStateException if the current transaction is finalized
     */
    public void updateAsync(T item, Consumer<DeferredResult<T>> callback) {
        start();
        modifyItemAsync(item, Reindexer.MODE_UPDATE, callback);
    }

    /**
     * Inserts or updates the given item data in the current transaction asynchronously.
     * Starts a transaction if not started.
     *
     * @param item     the item data
     * @param callback the {@link Consumer} which accepts a {@link DeferredResult} of type {@link T}
     * @throws IllegalStateException if the current transaction is finalized
     */
    public void upsertAsync(T item, Consumer<DeferredResult<T>> callback) {
        start();
        modifyItemAsync(item, Reindexer.MODE_UPSERT, callback);
    }

    /**
     * Deletes the given item data in the current transaction asynchronously.
     * Starts a transaction if not started.
     *
     * @param item     the item data
     * @param callback the {@link Consumer} which accepts a {@link DeferredResult} of type {@link T}
     * @throws IllegalStateException if the current transaction is finalized
     */
    public void deleteAsync(T item, Consumer<DeferredResult<T>> callback) {
        start();
        modifyItemAsync(item, Reindexer.MODE_DELETE, callback);
    }

    private void modifyItemAsync(T item, int mode, Consumer<DeferredResult<T>> callback) {
        Future<DeferredResult<T>> future = completionService.submit(() -> {
            DeferredResult<T> result = new DeferredResult<>();
            try {
                modifyItem(item, mode);
                result.setItem(item);
            } catch (Exception e) {
                result.setError(e);
            }
            if (callback != null) {
                callback.accept(result);
            }
            return result;
        });
        futures.add(future);
    }

    private void modifyItem(T item, int mode) {
        int format = Consts.FORMAT_JSON;
        byte[] data = serialize(item, format);
        String[] precepts = namespace.getPrecepts();
        transactionContext.modifyItem(format, data, mode, precepts, 0);
    }

    private byte[] serialize(T item, int format) {
        ByteBuffer buffer = new ByteBuffer();
        buffer.putVarInt64(format);
        itemWriter.writeItem(buffer, item);
        return buffer.bytes();
    }

    /**
     * Creates a {@link Query} with the current transaction for Update or Delete or Read.
     * Read-committed isolation is available for read operations.
     * Changes made in the current transaction is invisible to the current and another transactions.
     *
     * @return a {@link Query} with the current transaction
     */
    public Query<T> query() {
        return new Query<>(binding, namespace, transactionContext);
    }

}
