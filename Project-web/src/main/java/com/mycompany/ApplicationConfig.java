/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.Application;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.jackson.JacksonFeature;

@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put(CommonProperties.MOXY_JSON_FEATURE_DISABLE, true);
        return props;
    }

    private void addRestResourceClasses(Set<Class<?>> resources) {

        resources.add(com.mycompany.AdminResource.class);
        resources.add(com.mycompany.CartResource.class);
        resources.add(com.mycompany.GenericResource.class);
        resources.add(com.mycompany.InboxResource.class);
        resources.add(com.mycompany.ProductResource.class);
        resources.add(com.mycompany.UsersResource.class);
        resources.add(com.mycompany.WishlistResource.class);
        resources.add(util.JacksonConfig.class);
    }
}
/**
 package beans;

  import java.util.HashMap;
  import java.util.Map;
  import java.util.Set;
  import javax.ws.rs.core.Application;
  import org.glassfish.jersey.jackson.JacksonFeature;
  import org.glassfish.jersey.CommonProperties;

  @javax.ws.rs.ApplicationPath("webresources")
  public class ApplicationConfig extends Application {

      @Override
      public Set<Class<?>> getClasses() {
          Set<Class<?>> resources = new java.util.HashSet<>();
          addRestResourceClasses(resources);
          return resources;
      }

      private void addRestResourceClasses(Set<Class<?>> resources) {
          resources.add(beans.GenericResource.class);
          resources.add(util.JacksonConfig.class);
          resources.add(JacksonFeature.class);
      }

      @Override
      public Map<String, Object> getProperties() {
          Map<String, Object> props = new HashMap<>();
          props.put(CommonProperties.MOXY_JSON_FEATURE_DISABLE, true);
          return props;
      }
  }
 */