package org.apache.openejb;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.xml.ws.WebServiceRef;

import com.sun.faces.spi.DiscoverableInjectionProvider;
import com.sun.faces.spi.InjectionProviderException;
import org.apache.openejb.client.LocalInitialContextFactory;

/**
 * Created by IntelliJ IDEA.
 *
 * @author connollys
 * @since Sep 2, 2010 11:05:03 AM
 */
public class OpenEJBInjectionProvider extends DiscoverableInjectionProvider {

    private final javax.naming.Context context;

    public OpenEJBInjectionProvider() throws NamingException {
        Properties p = new Properties();
        p.put(Context.INITIAL_CONTEXT_FACTORY, LocalInitialContextFactory.class.getName());
        context = new InitialContext(p);
    }

    public void inject(Object instance) throws InjectionProviderException {
        if (context == null) {
            // No resource injection
            return;
        }

        Class<?> clazz = instance.getClass();

        try {
            while (clazz != null) {
                // Initialize fields annotations
                Field[] fields = clazz.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    if (fields[i].isAnnotationPresent(Resource.class)) {
                        Resource annotation =
                                fields[i].getAnnotation(Resource.class);
                        lookupFieldResource(context, instance, fields[i],
                                annotation.name(), clazz);
                    }
                    if (fields[i].isAnnotationPresent(EJB.class)) {
                        EJB annotation = fields[i].getAnnotation(EJB.class);
                        lookupFieldResource(context, instance, fields[i],
                                annotation.name(), clazz);
                    }
                    if (fields[i].isAnnotationPresent(WebServiceRef.class)) {
                        WebServiceRef annotation =
                                fields[i].getAnnotation(WebServiceRef.class);
                        lookupFieldResource(context, instance, fields[i],
                                annotation.name(), clazz);
                    }
                    if (fields[i].isAnnotationPresent(PersistenceContext.class)) {
                        PersistenceContext annotation =
                                fields[i].getAnnotation(PersistenceContext.class);
                        lookupFieldResource(context, instance, fields[i],
                                annotation.name(), clazz);
                    }
                    if (fields[i].isAnnotationPresent(PersistenceUnit.class)) {
                        PersistenceUnit annotation =
                                fields[i].getAnnotation(PersistenceUnit.class);
                        lookupFieldResource(context, instance, fields[i],
                                annotation.name(), clazz);
                    }
                }

                // Initialize methods annotations
                Method[] methods = clazz.getDeclaredMethods();

                for (int i = 0; i < methods.length; i++) {
                    if (methods[i].isAnnotationPresent(Resource.class)) {
                        Resource annotation = methods[i].getAnnotation(Resource.class);
                        lookupMethodResource(context, instance, methods[i],
                                annotation.name(), clazz);
                    }
                    if (methods[i].isAnnotationPresent(EJB.class)) {
                        EJB annotation = methods[i].getAnnotation(EJB.class);
                        lookupMethodResource(context, instance, methods[i],
                                annotation.name(), clazz);
                    }
                    if (methods[i].isAnnotationPresent(WebServiceRef.class)) {
                        WebServiceRef annotation =
                                methods[i].getAnnotation(WebServiceRef.class);
                        lookupMethodResource(context, instance, methods[i],
                                annotation.name(), clazz);
                    }
                    if (methods[i].isAnnotationPresent(PersistenceContext.class)) {
                        PersistenceContext annotation =
                                methods[i].getAnnotation(PersistenceContext.class);
                        lookupMethodResource(context, instance, methods[i],
                                annotation.name(), clazz);
                    }
                    if (methods[i].isAnnotationPresent(PersistenceUnit.class)) {
                        PersistenceUnit annotation =
                                methods[i].getAnnotation(PersistenceUnit.class);
                        lookupMethodResource(context, instance, methods[i],
                                annotation.name(), clazz);
                    }
                }

                clazz = clazz.getSuperclass();
            }
        } catch (IllegalAccessException e) {
            throw new InjectionProviderException(e);
        } catch (InvocationTargetException e) {
            throw new InjectionProviderException(e);
        } catch (NamingException e) {
            throw new InjectionProviderException(e);
        }
    }

    public void invokePostConstruct(Object instance) throws InjectionProviderException {
        Class<?> clazz = instance.getClass();

        while (clazz != null) {
            Method[] methods = clazz.getDeclaredMethods();
            Method postConstruct = null;
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].isAnnotationPresent(PostConstruct.class)) {
                    if ((postConstruct != null)
                            || (methods[i].getParameterTypes().length != 0)
                            || (Modifier.isStatic(methods[i].getModifiers()))
                            || (methods[i].getExceptionTypes().length > 0)
                            || (!methods[i].getReturnType().getName().equals("void"))) {
                        throw new IllegalArgumentException("Invalid PostConstruct annotation");
                    }
                    postConstruct = methods[i];
                }
            }

            // At the end the postconstruct annotated
            // method is invoked
            if (postConstruct != null) {
                boolean accessibility = postConstruct.isAccessible();
                postConstruct.setAccessible(true);
                try {
                    postConstruct.invoke(instance);
                } catch (IllegalAccessException e) {
                    throw new InjectionProviderException(e);
                } catch (InvocationTargetException e) {
                    throw new InjectionProviderException(e);
                }
                postConstruct.setAccessible(accessibility);
            }

            clazz = clazz.getSuperclass();
        }
    }

    public void invokePreDestroy(Object instance) throws InjectionProviderException {
        Class<?> clazz = instance.getClass();

        while (clazz != null) {
            Method[] methods = clazz.getDeclaredMethods();
            Method preDestroy = null;
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].isAnnotationPresent(PreDestroy.class)) {
                    if ((preDestroy != null)
                            || (methods[i].getParameterTypes().length != 0)
                            || (Modifier.isStatic(methods[i].getModifiers()))
                            || (methods[i].getExceptionTypes().length > 0)
                            || (!methods[i].getReturnType().getName().equals("void"))) {
                        throw new IllegalArgumentException("Invalid PreDestroy annotation");
                    }
                    preDestroy = methods[i];
                }
            }

            // At the end the postconstruct annotated
            // method is invoked
            if (preDestroy != null) {
                boolean accessibility = preDestroy.isAccessible();
                preDestroy.setAccessible(true);
                try {
                    preDestroy.invoke(instance);
                } catch (IllegalAccessException e) {
                    throw new InjectionProviderException(e);
                } catch (InvocationTargetException e) {
                    throw new InjectionProviderException(e);
                }
                preDestroy.setAccessible(accessibility);
            }

            clazz = clazz.getSuperclass();
        }
    }

    /**
     * Inject resources in specified field.
     */
    protected static void lookupFieldResource(javax.naming.Context context,
                                              Object instance, Field field, String name, Class<?> clazz)
            throws NamingException, IllegalAccessException {

        Object lookedupResource = null;
        boolean accessibility = false;

        if ((name != null) && (name.length() > 0)) {
            lookedupResource = context.lookup(name);
        } else {
            lookedupResource = context.lookup(clazz.getName() + "/" + field.getName());
        }

        accessibility = field.isAccessible();
        field.setAccessible(true);
        field.set(instance, lookedupResource);
        field.setAccessible(accessibility);
    }

    /**
     * Inject resources in specified method.
     */
    protected static void lookupMethodResource(javax.naming.Context context,
                                               Object instance, Method method, String name, Class<?> clazz)
            throws NamingException, IllegalAccessException, InvocationTargetException {

        if (!method.getName().startsWith("set")
                || method.getParameterTypes().length != 1
                || !method.getReturnType().getName().equals("void")) {
            throw new IllegalArgumentException("Invalid method resource injection annotation");
        }

        Object lookedupResource = null;
        boolean accessibility = false;

        if ((name != null) &&
                (name.length() > 0)) {
            lookedupResource = context.lookup(name);
        } else {
            lookedupResource = context.lookup(clazz.getName() + "/" + method.getName().substring(3));
        }

        accessibility = method.isAccessible();
        method.setAccessible(true);
        method.invoke(instance, lookedupResource);
        method.setAccessible(accessibility);
    }
}
