/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.*;

@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getInfo() {

        Map<String, Object> response = new HashMap<>();

        response.put("version", "v1");
        response.put("contact", "admin@smartcampus.com");

        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("rooms", "/api/v1/rooms");
        endpoints.put("sensors", "/api/v1/sensors");

        response.put("endpoints", endpoints);

        return response;
    }
}
