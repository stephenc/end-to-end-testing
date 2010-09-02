/*
 * Copyright (c) 2008 Avaya Inc.
 *
 * All rights reserved.
 */

package com.blogspot.javaadventure.end2endtesting.beans;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.transaction.UserTransaction;

import com.blogspot.javaadventure.end2endtesting.business.api.Greeter;

/**
 * Created by IntelliJ IDEA.
 *
 * @author connollys
 * @since Sep 2, 2010 10:24:53 AM
 */
public class GreeterBean {

    private String name;

    @EJB(name = "greeter")
    private Greeter greeter;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getGreeting() throws Exception {
        return greeter.getGreeting(name);
    }
}
