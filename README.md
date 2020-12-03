rx-connector
====================

Reindexer is an embeddable, in-memory, document-oriented database with a high-level Query builder interface. 
Rx-connector allows to connect to a Reindexer instance from java-application.

## Usage

Here is example of basic rx-connector usage:

```java
//Define an item class
public class Item {

    // 'id' is a primary key
    @Reindex(name = "id", isPrimaryKey = true)
    private Integer id;

    // add index by 'name' field
    @Reindex(name = "name")
    private String name;

    // add index articles by 'articles' array
    @Reindex(name = "articles")
    private List<Integer> articles;

    // add sortable index by 'year' field
    @Reindex(name = "year", type = TREE)
    private Integer year;

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", articles=" + articles +
                ", year=" + year +
                '}';
    }

    public Item(Integer id, String name, List<Integer> articles, Integer year) {
        this.id = id;
        this.name = name;
        this.articles = articles;
        this.year = year;
    }

    public static void main(String[] args) throws Exception {

        // Init a database instance and choose the binding (builtin). Configure connection pool size and connection
        // timeout. Database should be created explicitly via reindexer_tool.
        Reindexer reindexer = Configuration.builder()
                .url("cproto://localhost:6534/testdb")
                .connectionPoolSize(1)
                .connectionTimeout(30L)
                .getReindexer();

        // Create new namespace with name 'items', which will store objects of type 'Item'
        reindexer.openNamespace("items", NamespaceOptions.defaultOptions(), Item.class);

        // Generate dataset
        for (int i = 0; i < 1000000; i++) {
            Random random = new Random();
            reindexer.upsert("items", new Item(
                    i,
                    "Vasya",
                    Arrays.asList(random.nextInt() % 100, random.nextInt() % 100), 2000 + random.nextInt() % 50)
            );
        }

        // Query multiple documents, execute the query and return an iterator
        CloseableIterator<Item> iterator = reindexer.query("items", Item.class)
                .sort("year", false)
                .where("name", EQ, "Vasya")
                .where("year", GT, 2020)
                .where("articles", SET, 6, 1, 8)
                .limit(10)
                .offset(0)
                .execute();

        // Iterate over results
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

        // Iterator must be closed
        iterator.close();

        //Update single item
        reindexer.query("items", Item.class)
            .where("id", EQ, 5)
            .set("name", "Vova")
            .update();
        
        //Update multiple fields
        reindexer.query("items", Item.class)
            .where("id", EQ, 5)
            .set("name", "Vova")
            .set("year", 2021)
            .update();

        //Update multiple items items
        reindexer.query("items", Item.class)
            .where("id", LT, 5)
            .set("name", "Petya")
            .update();
        
        //Drop an item field
        reindexer.query("items", Item.class)
            .where("id", EQ, 6)
            .drop("name");

    }

}
```
### Complex Primary Keys and Composite Indexes

A Document can have multiple fields as a primary key. To enable this feature add composite index to object.
Composite index is an index that involves multiple fields, it can be used instead of several separate indexes.

```java
// Composite index
@Reindex(name = "id+sub_id", isPrimaryKey = true, subIndexes = {"id", "sub_id"})
public class Item {

    // 'id' is a part of a primary key
    @Reindex(name = "id")
    private Integer id;

    // 'sub_id' is a part of a primary key
    @Reindex(name = "sub_id")
    private String subId;

}
```

Query for composite index:

```java
reindexer.query("items", Item.class)
    .whereComposite("id+sub_id", EQ, 1, "test")
    .execute();
```

### Transactions and batch update

Reindexer supports transactions. Transaction are performs atomic namespace update. There are synchronous and 
async transaction available. To start transaction method db.beginTransaction() is used. This method creates transaction 
object, which provides usual Update/Upsert/Insert/Delete interface for application. For RPC clients there is 
transactions count limitation - each connection can't has more than 1024 opened transactions at the same time.

#### Synchronous mode

```java
// Create new transaction object
Transaction<Item> tx = db.beginTransaction("items", Item.class);
// Fill transaction object
tx.upsert(new Item(100, "Vasya", Arrays.asList(6, 1, 8), 2019));
tx.upsert(new Item(101, "Vova", Arrays.asList(7, 2, 9), 2020));
tx.query().where("id", EQ, 102).set("name", "Petya").update();
// Apply transaction
tx.commit();
```

#### Async batch mode (TODO)

#### Transactions commit strategies

Depends on amount changes in transaction there are 2 possible Commit strategies:

- Locked atomic update. Reindexer locks namespace and applying all changes under common lock. This mode is used with small amounts of changes.
- Copy & atomic replace. In this mode Reindexer makes namespace's snapshot, applying all changes to this snapshot, and atomically replaces namespace without lock.

#### Implementation notes

1. Transaction object is not thread safe and can't be used from different threads.
2. Transaction object holds Reindexer's resources, therefore application should explicitly call Rollback or Commit, otherwise resources will leak.
3. It is possible to call Query from transaction by call `tx.query().execute(); ...`. Only read-committed isolation is available. Changes made in active transaction is invisible to current and another transactions.
