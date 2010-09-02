package com.blogspot.javaadventure.end2endtesting.business.impl;
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.blogspot.javaadventure.end2endtesting.business.api.Greeter;
import com.blogspot.javaadventure.end2endtesting.data.Visitor;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Stephen Connolly
 * @since 01-Sep-2010 07:01:42
 */
@Stateless(name = "greeter")
public class GreeterImpl implements Greeter {

    @PersistenceContext
    private EntityManager em;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String getGreeting(String user) {
        final Query query = em.createNamedQuery(Visitor.BY_NAME);
        query.setParameter("name", user);
        final Date now = new Date();
        for (Visitor v : (List<Visitor>) query.getResultList()) {
            Date last = v.getLastVisit();
            v.setLastVisit(now);
            if (last == null) {
                return "Hello " + v.getName() + ", nice to meet you";
            } else {
                long diff = now.getTime() - last.getTime();
                if (TimeUnit.MILLISECONDS.toSeconds(diff) < 60) {
                    return "Hello " + v.getName() + ", so what has happend to you in the last " +
                            TimeUnit.MILLISECONDS.toSeconds(diff) + " second(s)?";
                }
                if (TimeUnit.MILLISECONDS.toSeconds(diff) < 60 * 60) {
                    return "Hello " + v.getName() + ", so what has happend to you in the last " +
                            TimeUnit.MILLISECONDS.toSeconds(diff) / 60 + " minute(s)?";
                }
                if (TimeUnit.MILLISECONDS.toSeconds(diff) < 60 * 60 * 24) {
                    return "Hello " + v.getName() + ", so what has happend to you in the last " +
                            TimeUnit.MILLISECONDS.toSeconds(diff) / 60 / 60 + " hour(s)?";
                }
                return "Hello " + v.getName() + ", so what has happend to you in the last " +
                        TimeUnit.MILLISECONDS.toSeconds(diff) / 60 / 60 / 24 + " day(s)?";
            }
        }
        Visitor v = new Visitor(user, now);
        em.persist(v);
        return "Hello " + v.getName() + ", nice to meet you";
    }
}
