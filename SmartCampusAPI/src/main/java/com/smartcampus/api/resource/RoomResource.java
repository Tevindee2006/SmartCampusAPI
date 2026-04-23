/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import com.smartcampus.api.model.Room;
import com.smartcampus.api.data.DB;
import com.smartcampus.api.exception.RoomNotEmptyException;

import java.util.*;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    // GET all rooms
    @GET
    public Collection<Room> getAllRooms() {
        return DB.rooms.values();
    }

    // POST create room
    @POST
    public Response createRoom(Room room) {
        DB.rooms.put(room.getId(), room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    // GET room by ID
    @GET
    @Path("/{id}")
    public Response getRoom(@PathParam("id") String id) {
        Room room = DB.rooms.get(id);

        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Room not found"))
                    .build();
        }

        return Response.ok(room).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteRoom(@PathParam("id") String id) {

        Room room = DB.rooms.get(id);

        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Room not found"))
                    .build();
        }

        // 🚨 Important rule: cannot delete if sensors exist
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Room has sensors assigned");
        }

        DB.rooms.remove(id);

        return Response.ok(Map.of("message", "Room deleted")).build();
    }
}
