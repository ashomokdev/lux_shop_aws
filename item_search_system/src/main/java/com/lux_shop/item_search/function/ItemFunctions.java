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


package com.lux_shop.item_search.function;


import com.lux_shop.item_search.dao.DynamoDBItemDao;
import com.lux_shop.item_search.domain.EventDeprecated;
import com.lux_shop.item_search.domain.Item;
import com.lux_shop.item_search.pojo.City;
import com.lux_shop.item_search.pojo.AddedBy;
import com.lux_shop.item_search.util.Consts;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;


public class ItemFunctions {

    private static final Logger log = Logger.getLogger(ItemFunctions.class);

    private static final DynamoDBItemDao eventDao = DynamoDBItemDao.instance();


    public List<Item> getAllItemsHandler() {

        log.info("GetAllItems invoked to scan table for ALL items");
        List<Item> items = eventDao.findAllItems();
        log.info("Found " + items.size() + " total items.");
        return items;
    }

    public List<Item> getItemsForAddedBy(AddedBy addedBy) throws UnsupportedEncodingException {

        if (null == addedBy || addedBy.getAddedBy().isEmpty() || addedBy.getAddedBy().equals(Consts.UNDEFINED)) {
            log.error("getItemsForAddedBy received null or empty addedBy name");
            throw new IllegalArgumentException("AddedBy name cannot be null or empty");
        }

        String name = URLDecoder.decode(addedBy.getAddedBy(), "UTF-8");
        log.info("getItemsForAddedBy invoked for addedBy with name = " + name);
        List<Item> itemsByAddedBy = eventDao.findItemsByAddedBy(name);
        log.info("Found " + itemsByAddedBy.size() + " items for addedBy = " + name);

        return itemsByAddedBy;
    }

    public List<Item> getItemsForStatus(String status) throws UnsupportedEncodingException {

        if (null == status || status.isEmpty() || status.equals(Consts.UNDEFINED)) {
            log.error("getItemsForStatus received null or empty status");
            throw new IllegalArgumentException("status cannot be null or empty");
        }

        String s = URLDecoder.decode(status, "UTF-8");
        log.info("getItemsForStatus invoked for status = " + s);
        List<Item> itemsByStatus = eventDao.findItemsByStatus(status);
        log.info("Found " + itemsByStatus.size() + " items for status = " + s);

        return itemsByStatus;
    }

    public void saveOrUpdateItem(Item item) {

        if (null == item) {
            log.error("saveOrUpdateItem received null input");
            throw new IllegalArgumentException("Cannot save null object");
        }

        log.info("Saving or updating item: " + item.toString());
        eventDao.saveOrUpdateItem(item);
        log.info("Successfully saved/updated item");
    }

    public void deleteItem(Item item) {

        if (null == item) {
            log.error("deleteItem received null input");
            throw new IllegalArgumentException("Cannot delete null object");
        }

        log.info("Deleting item: " + item.toString());
        eventDao.deleteItem(item.getItemId());
        log.info("Successfully deleted item");
    }

}
