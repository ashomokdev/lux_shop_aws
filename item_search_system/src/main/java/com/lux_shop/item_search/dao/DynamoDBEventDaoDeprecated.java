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
import com.lux_shop.item_search.manager.DynamoDBManager;
import org.apache.log4j.Logger;

import java.util.*;


public class DynamoDBEventDaoDeprecated implements EventDaoDeprecated {

    private static final Logger log = Logger.getLogger(DynamoDBEventDaoDeprecated.class);

    private static final DynamoDBMapper mapper = DynamoDBManager.mapper();

    private static volatile DynamoDBEventDaoDeprecated instance;


    private DynamoDBEventDaoDeprecated() { }

    public static DynamoDBEventDaoDeprecated instance() {

        if (instance == null) {
            synchronized(DynamoDBEventDaoDeprecated.class) {
                if (instance == null)
                    instance = new DynamoDBEventDaoDeprecated();
            }
        }
        return instance;
    }

    @Override
    public List<EventDeprecated> findAllEvents() {
        return mapper.scan(EventDeprecated.class, new DynamoDBScanExpression());
    }

    @Override
    public List<EventDeprecated> findEventsByCity(String city) {

        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":v1", new AttributeValue().withS(city));

        DynamoDBQueryExpression<EventDeprecated> query = new DynamoDBQueryExpression<EventDeprecated>()
                                                    .withIndexName(EventDeprecated.CITY_INDEX)
                                                    .withConsistentRead(false)
                                                    .withKeyConditionExpression("city = :v1")
                                                    .withExpressionAttributeValues(eav);

        return mapper.query(EventDeprecated.class, query);


        // NOTE:  without an index, this query would require a full table scan with a filter:
        /*
         DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                                                    .withFilterExpression("city = :val1")
                                                    .withExpressionAttributeValues(eav);

         return mapper.scan(EventDeprecated.class, scanExpression);
        */

    }

    @Override
    public List<EventDeprecated> findEventsByTeam(String team) {

        DynamoDBQueryExpression<EventDeprecated> homeQuery = new DynamoDBQueryExpression<>();
        EventDeprecated eventDeprecatedKey = new EventDeprecated();
        eventDeprecatedKey.setHomeTeam(team);
        homeQuery.setHashKeyValues(eventDeprecatedKey);
        List<EventDeprecated> homeEventDeprecateds = mapper.query(EventDeprecated.class, homeQuery);

        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":v1", new AttributeValue().withS(team));
        DynamoDBQueryExpression<EventDeprecated> awayQuery = new DynamoDBQueryExpression<EventDeprecated>()
                                                        .withIndexName(EventDeprecated.AWAY_TEAM_INDEX)
                                                        .withConsistentRead(false)
                                                        .withKeyConditionExpression("awayTeam = :v1")
                                                        .withExpressionAttributeValues(eav);

        List<EventDeprecated> awayEventDeprecateds = mapper.query(EventDeprecated.class, awayQuery);

        // need to create a new list because PaginatedList from query is immutable
        List<EventDeprecated> allEventDeprecateds = new LinkedList<>();
        allEventDeprecateds.addAll(homeEventDeprecateds);
        allEventDeprecateds.addAll(awayEventDeprecateds);
        allEventDeprecateds.sort( (e1, e2) -> e1.getEventDate() <= e2.getEventDate() ? -1 : 1 );

        return allEventDeprecateds;
    }

    @Override
    public Optional<EventDeprecated> findEventByTeamAndDate(String team, Long eventDate) {

        EventDeprecated eventDeprecated = mapper.load(EventDeprecated.class, team, eventDate);

        return Optional.ofNullable(eventDeprecated);
    }

    @Override
    public void saveOrUpdateEvent(EventDeprecated eventDeprecated) {

        mapper.save(eventDeprecated);
    }

    @Override
    public void deleteEvent(String team, Long eventDate) {

        Optional<EventDeprecated> oEvent = findEventByTeamAndDate(team, eventDate);
        if (oEvent.isPresent()) {
            mapper.delete(oEvent.get());
        }
        else {
            log.error("Could not delete event, no such team and date combination");
            throw new IllegalArgumentException("Delete failed for nonexistent event");
        }
    }
}
