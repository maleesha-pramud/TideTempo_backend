package com.wigerlabs.TideTempo.service;

import com.google.gson.JsonObject;
import com.wigerlabs.TideTempo.dto.UserDTO;
import com.wigerlabs.TideTempo.entity.User;
import com.wigerlabs.TideTempo.util.AppUtil;
import com.wigerlabs.TideTempo.util.HibernateUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Context;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class UserService {
    public String addUser(UserDTO userDTO) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (userDTO == null) {
            message = "Invalid user data.";
        } else if (userDTO.getFullName() == null || userDTO.getFullName().isEmpty()) {
            message = "Full Name is required.";
        } else if (userDTO.getEmail() == null || userDTO.getEmail().isEmpty()) {
            message = "Email is required.";
        } else if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
            message = "Password is required.";
        } else {
            try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                User existingUser = hibernateSession.createNamedQuery("User.findByEmail", User.class)
                        .setParameter("email", userDTO.getEmail())
                        .getSingleResultOrNull();
                if (existingUser != null) {
                    message = "Email is already registered.";
                } else {
                    User newUser = new User();
                    newUser.setFullName(userDTO.getFullName());
                    newUser.setEmail(userDTO.getEmail());
                    newUser.setPassword(userDTO.getPassword());

                    Transaction transaction = hibernateSession.beginTransaction();
                    try {
                        hibernateSession.persist(newUser);
                        transaction.commit();
                        status = true;
                        message = "User registered successfully.";
                    } catch (Exception e) {
                        if (transaction != null) {
                            transaction.rollback();
                        }
                        message = "Error occurred while registering user: " + e.getMessage();
                    } finally {
                        hibernateSession.close();
                    }
                }
            } catch (Exception e) {
                message = "Error occurred while accessing the database: " + e.getMessage();
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return responseObject.toString();
    }

    public String loginUser(UserDTO userDTO, HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        JsonObject dataObject = new JsonObject();

        if (userDTO == null) {
            message = "Invalid user data.";
        } else if (userDTO.getEmail() == null || userDTO.getEmail().isEmpty()) {
            message = "Email is required.";
        } else if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
            message = "Password is required.";
        } else {
            try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                User existingUser = hibernateSession.createNamedQuery("User.findByEmail", User.class)
                        .setParameter("email", userDTO.getEmail())
                        .getSingleResultOrNull();
                if (existingUser != null && existingUser.getPassword().equals(userDTO.getPassword())) {
                    HttpSession httpSession = request.getSession();
                    httpSession.setAttribute("user", existingUser);

                    dataObject.add("user", AppUtil.GSON.toJsonTree(new UserDTO(
                            existingUser.getId(),
                            existingUser.getFullName(),
                            existingUser.getEmail()
                    )));

                    status = true;
                    message = "Login successful.";
                } else {
                    message = "Invalid email or password.";
                }
            } catch (Exception e) {
                message = "Error occurred while logging in: " + e.getMessage();
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        responseObject.add("data", dataObject);
        return responseObject.toString();
    }

    public String updateUser(UserDTO userDTO, HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (userDTO == null) {
            message = "Invalid user data.";
        } else if (userDTO.getFullName() == null || userDTO.getFullName().isEmpty()) {
            message = "Full Name is required.";
        } else if (userDTO.getEmail() == null || userDTO.getEmail().isEmpty()) {
            message = "Email is required.";
        } else {
            HttpSession httpSession = request.getSession(false);
            if (httpSession == null || httpSession.getAttribute("user") == null) {
                message = "User is not logged in.";
            } else {
                User loggedInUser = (User) httpSession.getAttribute("user");
                try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                    User existingUser = hibernateSession.createNamedQuery("User.findByEmail", User.class)
                            .setParameter("email", userDTO.getEmail())
                            .getSingleResultOrNull();
                    if (existingUser != null && existingUser.getId() != loggedInUser.getId()) {
                        message = "Email is already registered by another user.";
                    } else {
                        Transaction transaction = hibernateSession.beginTransaction();
                        try {
                            loggedInUser.setFullName(userDTO.getFullName());
                            loggedInUser.setEmail(userDTO.getEmail());
                            if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                                loggedInUser.setPassword(userDTO.getPassword());
                            }
                            hibernateSession.merge(loggedInUser);
                            transaction.commit();
                            status = true;
                            message = "User updated successfully.";
                        } catch (Exception e) {
                            if (transaction != null) {
                                transaction.rollback();
                            }
                            message = "Error occurred while updating user: " + e.getMessage();
                        } finally {
                            hibernateSession.close();
                        }
                    }
                } catch (Exception e) {
                    message = "Error occurred while accessing the database: " + e.getMessage();
                }
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return responseObject.toString();
    }
}
