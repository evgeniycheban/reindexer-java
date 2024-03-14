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
package ru.rt.restream.reindexer.connector;

import org.junit.jupiter.api.Test;
import ru.rt.restream.reindexer.Namespace;
import ru.rt.restream.reindexer.NamespaceOptions;
import ru.rt.restream.reindexer.Query;
import ru.rt.restream.reindexer.ResultIterator;
import ru.rt.restream.reindexer.annotations.Reindex;
import ru.rt.restream.reindexer.db.DbBaseTest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static ru.rt.restream.reindexer.Query.Condition.EQ;
import static ru.rt.restream.reindexer.Query.Condition.GE;
import static ru.rt.restream.reindexer.Query.Condition.GT;
import static ru.rt.restream.reindexer.Query.Condition.LT;
import static ru.rt.restream.reindexer.Query.Condition.SET;

/**
 * Base SubQuery test.
 */
public abstract class SubQueryTest extends DbBaseTest {

    @Test
    public void testWhereWithArgsIndexConditionQueryAndOneNs() {
        Namespace<Person> personsNs = db.openNamespace("persons", NamespaceOptions.defaultOptions(), Person.class);
        for (int i = 0; i < 20; i++) {
            int age = 9 + (i % 8) * 10;
            String name = "Person" + i + "Age" + age;
            personsNs.insert(new Person(i, name, age));
        }

        // select * from persons p
        // where p.age = (select max(age) from person)
        Query<Person> maxAgeSubQuery = personsNs.query().aggregateMax("age");
        Query<Person> eldestPersonsQuery = personsNs.query()
                .where("age", EQ, maxAgeSubQuery);
        ResultIterator<Person> iterator = eldestPersonsQuery.execute();

        List<Person> actualEldestPersons = new ArrayList<>();
        while (iterator.hasNext()) {
            actualEldestPersons.add(iterator.next());
        }

        assertThat(actualEldestPersons.size(), is(2));
    }

    @Test
    public void testWhereWithArgsIndexConditionQueryAndTwoNs() {
        Namespace<Person> personsNs = db.openNamespace("persons", NamespaceOptions.defaultOptions(), Person.class);
        Namespace<Purchase> purchasesNs = db.openNamespace("purchases", NamespaceOptions.defaultOptions(), Purchase.class);
        int purchaseId = 0;
        // 24 persons, everyone has from 0 to 3 purchases, for a total of 36 purchases.
        for (int i = 0; i < 24; i++) {
            int age = 9 + (i % 8) * 10;
            String name = "Person" + i + "Age" + age;
            personsNs.insert(new Person(i, name, age));
            for (int j = 0; j < i % 4; j++) {
                int price = (j + 1) * 10;
                purchasesNs.insert(new Purchase(purchaseId++, i, price, "Asset" + j));
            }
        }

        // Aggregation 'distinct' doesn't support in subquery, so use 'max' in there.
        // select * from purchases p
        // where p.person_id in (select max(id) from persons where age > 60)
        Query<Person> retireeSubQuery = personsNs.query()
                .where("age", GT, 60)
                .aggregateMax("id");
        Query<Purchase> purchasesQuery = purchasesNs.query()
                .where("person_id", EQ, retireeSubQuery);
        ResultIterator<Purchase> iterator = purchasesQuery.execute();

        List<Purchase> actualRetireesPurchases = new ArrayList<>();
        while (iterator.hasNext()) {
            actualRetireesPurchases.add(iterator.next());
        }

        assertThat(actualRetireesPurchases.size(), is(3));
    }

    @Test
    public void testWhereWithArgsQueryConditionValues() {
        Namespace<Banner> bannersNs = db.openNamespace("banners", NamespaceOptions.defaultOptions(), Banner.class);
        Namespace<Purchase> purchasesNs = db.openNamespace("purchases", NamespaceOptions.defaultOptions(), Purchase.class);
        bannersNs.insert(new Banner(1, "Banner"));
        int purchaseId = 0;
        // 24 persons, everyone has from 0 to 3 purchases, for a total of 36 purchases.
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < i % 4; j++) {
                int price = (j + 1) * 10;
                purchasesNs.insert(new Purchase(purchaseId++, i, price, "Asset" + j));
            }
        }

        int personId = 14;
        int personPurchasesCnt = 2;
        int sumPrices = 30; // 10 + 20

        // select * from banners b
        // where b.id = 1 and (select sum(p.price) from purchases p where p.person_id = 14) = 30
        Query<Purchase> subQuery = purchasesNs.query()
                .where("person_id", EQ, personId)
                .aggregateSum("price");
        Query<Banner> bannerExistsOnEqQuery = bannersNs.query()
                .where("id", EQ, 1)
                .where(subQuery, EQ, sumPrices);
        ResultIterator<Banner> bannerExistsOnEqIterator = bannerExistsOnEqQuery.execute();
        assertThat(bannerExistsOnEqIterator.hasNext(), is(true));

        // select * from banners b
        // where b.id = 1 and (select sum(p.price) from purchases p where p.person_id = 14) >= 30
        Query<Banner> bannerExistsQuery = bannersNs.query()
                .where("id", EQ, 1)
                .where(subQuery, GE, sumPrices);
        ResultIterator<Banner> bannerExistsIterator = bannerExistsQuery.execute();
        assertThat(bannerExistsIterator.hasNext(), is(true));

        // select * from banners b
        // where b.id = 1 and (select sum(p.price) from purchases p where p.person_id = 14) < 30
        Query<Banner> bannerNotExistsQuery = bannersNs.query()
                .where("id", EQ, 1)
                .where(subQuery, LT, 30);
        ResultIterator<Banner> bannerNotExistsIterator = bannerNotExistsQuery.execute();
        assertThat(bannerNotExistsIterator.hasNext(), is(false));
    }

    public static class Person {
        @Reindex(name = "id", isPrimaryKey = true)
        private int id;
        @Reindex(name = "full_name")
        private String fullName;
        @Reindex(name = "age")
        private int age;

        public Person() {
        }

        public Person(int id, String fullName, int age) {
            this.id = id;
            this.fullName = fullName;
            this.age = age;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    public static class Purchase {
        @Reindex(name = "id", isPrimaryKey = true)
        private int id;
        @Reindex(name = "person_id")
        private int personId;
        @Reindex(name = "price")
        private int price;
        private String assetName;

        public Purchase() {
        }

        public Purchase(int id, int personId, int price, String assetName) {
            this.id = id;
            this.personId = personId;
            this.price = price;
            this.assetName = assetName;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getPersonId() {
            return personId;
        }

        public void setPersonId(int personId) {
            this.personId = personId;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public String getAssetName() {
            return assetName;
        }

        public void setAssetName(String assetName) {
            this.assetName = assetName;
        }
    }

    public static class Banner {
        @Reindex(name = "id", isPrimaryKey = true)
        private int id;
        private String name;

        public Banner() {
        }

        public Banner(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
