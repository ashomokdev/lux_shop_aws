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


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import com.lux_shop.item_search.dao.DynamoDBEventDaoDeprecated;
import com.lux_shop.item_search.domain.EventDeprecated;
import com.lux_shop.item_search.pojo.City;
import com.lux_shop.item_search.pojo.AddedBy;
import com.lux_shop.item_search.util.Consts;
import org.apache.log4j.Logger;



public class EventFunctions {

    private static final Logger log = Logger.getLogger(EventFunctions.class);

    private static final DynamoDBEventDaoDeprecated eventDao = DynamoDBEventDaoDeprecated.instance();


    public List<EventDeprecated> getAllEventsHandler() {

        log.info("GetAllEvents invoked to scan table for ALL eventDeprecateds");
        List<EventDeprecated> eventDeprecateds = eventDao.findAllEvents();
        log.info("Found " + eventDeprecateds.size() + " total eventDeprecateds.");
        return eventDeprecateds;
    }

//    public List<EventDeprecated> getEventsForTeam(AddedBy addedBy) throws UnsupportedEncodingException {
//
//        if (null == addedBy || addedBy.getTeamName().isEmpty() || addedBy.getTeamName().equals(Consts.UNDEFINED)) {
//            log.error("GetEventsForTeam received null or empty addedBy name");
//            throw new IllegalArgumentException("AddedBy name cannot be null or empty");
//        }
//
//        String name = URLDecoder.decode(addedBy.getTeamName(), "UTF-8");
//        log.info("GetEventsForTeam invoked for addedBy with name = " + name);
//        List<EventDeprecated> eventDeprecateds = eventDao.findEventsByTeam(name);
//        log.info("Found " + eventDeprecateds.size() + " eventDeprecateds for addedBy = " + name);
//
//        return eventDeprecateds;
//    }

    public List<EventDeprecated> getEventsForCity(City city) throws UnsupportedEncodingException {

        if (null == city || city.getCityName().isEmpty() || city.getCityName().equals(Consts.UNDEFINED)) {
            log.error("GetEventsForCity received null or empty city name");
            throw new IllegalArgumentException("City name cannot be null or empty");
        }

        String name = URLDecoder.decode(city.getCityName(), "UTF-8");
        log.info("GetEventsForCity invoked for city with name = " + name);
        List<EventDeprecated> eventDeprecateds = eventDao.findEventsByCity(name);
        log.info("Found " + eventDeprecateds.size() + " eventDeprecateds for city = " + name);

        return eventDeprecateds;
    }

    public void saveOrUpdateEvent(EventDeprecated eventDeprecated) {

        if (null == eventDeprecated) {
            log.error("SaveEvent received null input");
            throw new IllegalArgumentException("Cannot save null object");
        }

        log.info("Saving or updating eventDeprecated for team = " + eventDeprecated.getHomeTeam() + " , date = " + eventDeprecated.getEventDate());
        eventDao.saveOrUpdateEvent(eventDeprecated);
        log.info("Successfully saved/updated eventDeprecated");
    }

    public void deleteEvent(EventDeprecated eventDeprecated) {

        if (null == eventDeprecated) {
            log.error("DeleteEvent received null input");
            throw new IllegalArgumentException("Cannot delete null object");
        }

        log.info("Deleting eventDeprecated for team = " + eventDeprecated.getHomeTeam() + " , date = " + eventDeprecated.getEventDate());
        eventDao.deleteEvent(eventDeprecated.getHomeTeam(), eventDeprecated.getEventDate());
        log.info("Successfully deleted eventDeprecated");
    }

}
