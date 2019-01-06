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

import com.lux_shop.item_search.domain.EventDeprecated;
import com.lux_shop.item_search.domain.Item;

import java.util.Date;
import java.util.List;
import java.util.Optional;


public interface ItemDao {

    List<Item> findAllItems();

    List<Item> findItemsByAddedBy(String addedBy);

    List<Item> findItemsByStatus(String status);

    void saveOrUpdateItem(Item item);

    Optional<Item> findItemById(Long itemId);

    void deleteItem(Long itemId);

}
