// Copyright 2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License"). You may
// not use this file except in compliance with the License. A copy of the
// License is located at
//
//	  http://aws.amazon.com/apache2.0/
//
// or in the "license" file accompanying this file. This file is distributed
// on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
// express or implied. See the License for the specific language governing
// permissions and limitations under the License.


package com.lux_shop.item_search.dao;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.lux_shop.item_search.domain.EventDeprecated;
import com.lux_shop.item_search.domain.Item;
import com.lux_shop.item_search.manager.DynamoDBManager;
import org.apache.log4j.Logger;

import java.util.*;


public class DynamoDBItemDao implements ItemDao {

    private static final Logger log = Logger.getLogger(DynamoDBItemDao.class);

    private static final DynamoDBMapper mapper = DynamoDBManager.mapper();

    private static volatile DynamoDBItemDao instance;


    private DynamoDBItemDao() {
    }

    public static DynamoDBItemDao instance() {

        if (instance == null) {
            synchronized (DynamoDBItemDao.class) {
                if (instance == null)
                    instance = new DynamoDBItemDao();
            }
        }
        return instance;
    }


    @Override
    public List<Item> findAllItems() {
        return mapper.scan(Item.class, new DynamoDBScanExpression());
    }

    @Override
    public List<Item> findItemsByAddedBy(String addedBy) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":v1", new AttributeValue().withS(addedBy));

        DynamoDBQueryExpression<Item> query = new DynamoDBQueryExpression<Item>()
                .withIndexName(Item.ADDED_BY_INDEX)
                .withConsistentRead(false)
                .withKeyConditionExpression("addedBy = :v1")
                .withExpressionAttributeValues(eav);

        return mapper.query(Item.class, query);


        // NOTE:  without an index, this query would require a full table scan with a filter:
        /*
         DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                                                    .withFilterExpression("addedBy = :v1")
                                                    .withExpressionAttributeValues(eav);

         return mapper.scan(Item.class, scanExpression);
        */
    }

    @Override
    public List<Item> findItemsByStatus(String status) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":v1", new AttributeValue().withS(status));

        // NOTE:  without an index, this query would require a full table scan with a filter:
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("status = :v1")
                .withExpressionAttributeValues(eav);

        return mapper.scan(Item.class, scanExpression);

    }


    @Override
    public void saveOrUpdateItem(Item item) {
        mapper.save(item);
    }

    @Override
    public Optional<Item> findItemById(Long itemId) {

        Item item = mapper.load(Item.class, itemId);

        return Optional.ofNullable(item);
    }


    @Override
    public void deleteItem(Long itemId) {
        Optional<Item> oItem = findItemById(itemId);
        if (oItem.isPresent()) {
            mapper.delete(oItem.get());
        } else {
            log.error("Could not delete event, no such team and date combination");
            throw new IllegalArgumentException("Delete failed for nonexistent event");
        }
    }
}
