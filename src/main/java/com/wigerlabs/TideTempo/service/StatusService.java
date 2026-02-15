package com.wigerlabs.TideTempo.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wigerlabs.TideTempo.entity.Status;
import com.wigerlabs.TideTempo.entity.User;
import com.wigerlabs.TideTempo.util.AppUtil;
import com.wigerlabs.TideTempo.util.HibernateUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Session;

import java.util.List;

public class StatusService {
    public String getAllStatuses(HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message;
        JsonArray dataArray = new JsonArray();

        HttpSession httpSession = request.getSession(false);
        if (httpSession == null || httpSession.getAttribute("user") == null) {
            message = "User is not logged in.";
        } else {
            try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                List<Status> statuses = hibernateSession.createQuery("FROM Status", Status.class).list();

                for (Status statusEntity : statuses) {
                    JsonObject statusObject = new JsonObject();
                    statusObject.addProperty("id", statusEntity.getId());
                    statusObject.addProperty("value", statusEntity.getValue());
                    dataArray.add(statusObject);
                }

                status = true;
                message = "Statuses retrieved successfully.";
            } catch (Exception e) {
                message = "Error occurred while retrieving statuses: " + e.getMessage();
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        responseObject.add("data", dataArray);
        return responseObject.toString();
    }
}
