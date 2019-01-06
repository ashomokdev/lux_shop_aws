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


package com.lux_shop.item_search.domain;


import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@DynamoDBTable(tableName = "Item")
public class Item implements Serializable {
    public static final String ADDED_BY_INDEX = "Added_by-Index";

    private static final long serialVersionUID = -8243145429438016232L;

    public enum Status {
        ACCEPTED,
        DECLINED
    }

    @DynamoDBHashKey
    private Long itemId;

    @DynamoDBRangeKey
    private Date date;

    @DynamoDBAttribute
    private String photoLink;

    @DynamoDBAttribute
    @DynamoDBTypeConvertedEnum
    private Status status;

    @DynamoDBAttribute
    private String link;

    @DynamoDBIndexHashKey(globalSecondaryIndexName = ADDED_BY_INDEX)
    private String addedBy;

    @DynamoDBAttribute
    private Double price;
}
