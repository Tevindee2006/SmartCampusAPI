/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api.data;
import com.smartcampus.api.model.Room;
import com.smartcampus.api.model.Sensor;
import com.smartcampus.api.model.SensorReading;
import java.util.*;
/**
 *
 * @author tevindee
 */
public class DB {
    public static Map<String, Room> rooms = new HashMap<>();
    public static Map<String, Sensor> sensors = new HashMap<>();
    public static Map<String, List<SensorReading>> readings = new HashMap<>();
}
