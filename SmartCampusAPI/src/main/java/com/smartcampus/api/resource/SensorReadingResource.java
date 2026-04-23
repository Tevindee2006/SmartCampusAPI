/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import com.smartcampus.api.model.*;
import com.smartcampus.api.data.DB;

import java.util.*;

public class SensorReadingResource {

    private String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // GET readings
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SensorReading> getReadings() {
        return DB.readings.getOrDefault(sensorId, new ArrayList<>());
    }

    // POST new reading
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {

        Sensor sensor = DB.sensors.get(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Sensor not found"))
                    .build();
        }

        // 🔥 ADD THIS HERE
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of("error", "Sensor in maintenance"))
                    .build();
        }

        DB.readings.putIfAbsent(sensorId, new ArrayList<>());
        DB.readings.get(sensorId).add(reading);

        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
