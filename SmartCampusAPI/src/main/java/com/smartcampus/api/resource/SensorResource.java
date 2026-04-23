/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import com.smartcampus.api.model.Sensor;
import com.smartcampus.api.model.Room;
import com.smartcampus.api.data.DB;
import com.smartcampus.api.exception.LinkedResourceNotFoundException;

import java.util.*;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    // POST create sensor
    @POST
    public Response createSensor(Sensor sensor) {

        // 🚨 Validate room exists
        Room room = DB.rooms.get(sensor.getRoomId());

        if (room == null) {
            throw new LinkedResourceNotFoundException("Room does not exist");
        }

        DB.sensors.put(sensor.getId(), sensor);

        // Link sensor to room
        room.getSensorIds().add(sensor.getId());

        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    // GET all sensors (with optional filter)
    @GET
    public Collection<Sensor> getSensors(@QueryParam("type") String type) {

        if (type == null) {
            return DB.sensors.values();
        }

        List<Sensor> filtered = new ArrayList<>();

        for (Sensor s : DB.sensors.values()) {
            if (s.getType().equalsIgnoreCase(type)) {
                filtered.add(s);
            }
        }

        return filtered;
    }

    @Path("/{id}/readings")
    public SensorReadingResource getReadingResource(@PathParam("id") String id) {
        return new SensorReadingResource(id);
    }
}
