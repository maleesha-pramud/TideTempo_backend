package com.wigerlabs.TideTempo.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wigerlabs.TideTempo.entity.Priority;
import com.wigerlabs.TideTempo.entity.User;
import com.wigerlabs.TideTempo.util.AppUtil;
import com.wigerlabs.TideTempo.util.HibernateUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Session;

import java.util.List;

public class PriorityService {
    public String getAllPriorities(HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message;
        JsonArray dataArray = new JsonArray();

        HttpSession httpSession = request.getSession(false);
        if (httpSession == null || httpSession.getAttribute("user") == null) {
            message = "User is not logged in.";
        } else {
            try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                List<Priority> priorities = hibernateSession.createQuery("FROM Priority", Priority.class).list();

                for (Priority priority : priorities) {
                    JsonObject priorityObject = new JsonObject();
                    priorityObject.addProperty("id", priority.getId());
                    priorityObject.addProperty("value", priority.getValue());
                    dataArray.add(priorityObject);
                }

                status = true;
                message = "Priorities retrieved successfully.";
            } catch (Exception e) {
                message = "Error occurred while retrieving priorities: " + e.getMessage();
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        responseObject.add("data", dataArray);
        return responseObject.toString();
    }
}
