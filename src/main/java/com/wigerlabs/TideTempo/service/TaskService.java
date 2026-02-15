package com.wigerlabs.TideTempo.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wigerlabs.TideTempo.dto.TaskDTO;
import com.wigerlabs.TideTempo.entity.Priority;
import com.wigerlabs.TideTempo.entity.Status;
import com.wigerlabs.TideTempo.entity.Task;
import com.wigerlabs.TideTempo.entity.User;
import com.wigerlabs.TideTempo.util.AppUtil;
import com.wigerlabs.TideTempo.util.HibernateUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class TaskService {
    public String addTask(TaskDTO taskDTO, HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (taskDTO == null) {
            message = "Invalid task data.";
        } else if (taskDTO.getName() == null || taskDTO.getName().isEmpty()) {
            message = "Task name is required.";
        } else if (taskDTO.getPriorityId() <= 0) {
            message = "Priority is required.";
        } else if (taskDTO.getStatusId() <= 0) {
            message = "Status is required.";
        } else {
            HttpSession httpSession = request.getSession(false);
            if (httpSession == null || httpSession.getAttribute("user") == null) {
                message = "User is not logged in.";
            } else {
                User loggedInUser = (User) httpSession.getAttribute("user");
                try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                    Task existingTask = hibernateSession.createQuery("FROM Task WHERE name = :name", Task.class)
                            .setParameter("name", taskDTO.getName())
                            .getSingleResultOrNull();
                    if (existingTask != null) {
                        message = "Task with this name already exists.";
                    } else {
                        Priority priority = hibernateSession.get(Priority.class, taskDTO.getPriorityId());
                        Status taskStatus = hibernateSession.get(Status.class, taskDTO.getStatusId());

                        if (priority == null) {
                            message = "Invalid priority.";
                        } else if (taskStatus == null) {
                            message = "Invalid status.";
                        } else {
                            Task newTask = new Task();
                            newTask.setName(taskDTO.getName());
                            newTask.setDescription(taskDTO.getDescription());
                            newTask.setTimeSpent(taskDTO.getTimeSpent());
                            newTask.setUser(loggedInUser);
                            newTask.setPriority(priority);
                            newTask.setStatus(taskStatus);

                            Transaction transaction = hibernateSession.beginTransaction();
                            try {
                                hibernateSession.persist(newTask);
                                transaction.commit();
                                status = true;
                                message = "Task created successfully.";
                            } catch (Exception e) {
                                if (transaction != null) {
                                    transaction.rollback();
                                }
                                message = "Error occurred while creating task: " + e.getMessage();
                            } finally {
                                hibernateSession.close();
                            }
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

    public String getAllTasks(HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        JsonArray dataArray = new JsonArray();

        HttpSession httpSession = request.getSession(false);
        if (httpSession == null || httpSession.getAttribute("user") == null) {
            message = "User is not logged in.";
        } else {
            User loggedInUser = (User) httpSession.getAttribute("user");
            try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                List<Task> tasks = hibernateSession.createQuery("FROM Task WHERE user.id = :userId", Task.class)
                        .setParameter("userId", loggedInUser.getId())
                        .list();

                for (Task task : tasks) {
                    TaskDTO taskDTO = new TaskDTO(
                            task.getId(),
                            task.getName(),
                            task.getDescription(),
                            task.getTimeSpent(),
                            task.getUser().getId(),
                            task.getPriority().getId(),
                            task.getStatus().getId()
                    );
                    dataArray.add(AppUtil.GSON.toJsonTree(taskDTO));
                }

                status = true;
                message = "Tasks retrieved successfully.";
            } catch (Exception e) {
                message = "Error occurred while retrieving tasks: " + e.getMessage();
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        responseObject.add("data", dataArray);
        return responseObject.toString();
    }

    public String getTaskById(int id, HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        JsonObject dataObject = new JsonObject();

        if (id <= 0) {
            message = "Invalid task ID.";
        } else {
            HttpSession httpSession = request.getSession(false);
            if (httpSession == null || httpSession.getAttribute("user") == null) {
                message = "User is not logged in.";
            } else {
                User loggedInUser = (User) httpSession.getAttribute("user");
                try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                    Task task = hibernateSession.get(Task.class, id);
                    if (task == null) {
                        message = "Task not found.";
                    } else if (task.getUser().getId() != loggedInUser.getId()) {
                        message = "You are not authorized to view this task.";
                    } else {
                        TaskDTO taskDTO = new TaskDTO(
                                task.getId(),
                                task.getName(),
                                task.getDescription(),
                                task.getTimeSpent(),
                                task.getUser().getId(),
                                task.getPriority().getId(),
                                task.getStatus().getId()
                        );
                        dataObject.add("task", AppUtil.GSON.toJsonTree(taskDTO));
                        status = true;
                        message = "Task retrieved successfully.";
                    }
                } catch (Exception e) {
                    message = "Error occurred while retrieving task: " + e.getMessage();
                }
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        responseObject.add("data", dataObject);
        return responseObject.toString();
    }

    public String updateTask(TaskDTO taskDTO, HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (taskDTO == null) {
            message = "Invalid task data.";
        } else if (taskDTO.getId() <= 0) {
            message = "Task ID is required.";
        } else if (taskDTO.getName() == null || taskDTO.getName().isEmpty()) {
            message = "Task name is required.";
        } else if (taskDTO.getPriorityId() <= 0) {
            message = "Priority is required.";
        } else if (taskDTO.getStatusId() <= 0) {
            message = "Status is required.";
        } else {
            HttpSession httpSession = request.getSession(false);
            if (httpSession == null || httpSession.getAttribute("user") == null) {
                message = "User is not logged in.";
            } else {
                User loggedInUser = (User) httpSession.getAttribute("user");
                try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                    Task existingTask = hibernateSession.get(Task.class, taskDTO.getId());
                    if (existingTask == null) {
                        message = "Task not found.";
                    } else if (existingTask.getUser().getId() != loggedInUser.getId()) {
                        message = "You are not authorized to update this task.";
                    } else {
                        Task duplicateTask = hibernateSession.createQuery("FROM Task WHERE name = :name AND id != :id", Task.class)
                                .setParameter("name", taskDTO.getName())
                                .setParameter("id", taskDTO.getId())
                                .getSingleResultOrNull();
                        if (duplicateTask != null) {
                            message = "Task with this name already exists.";
                        } else {
                            Priority priority = hibernateSession.get(Priority.class, taskDTO.getPriorityId());
                            Status taskStatus = hibernateSession.get(Status.class, taskDTO.getStatusId());

                            if (priority == null) {
                                message = "Invalid priority.";
                            } else if (taskStatus == null) {
                                message = "Invalid status.";
                            } else {
                                Transaction transaction = hibernateSession.beginTransaction();
                                try {
                                    existingTask.setName(taskDTO.getName());
                                    existingTask.setDescription(taskDTO.getDescription());
                                    existingTask.setTimeSpent(taskDTO.getTimeSpent());
                                    existingTask.setPriority(priority);
                                    existingTask.setStatus(taskStatus);

                                    hibernateSession.merge(existingTask);
                                    transaction.commit();
                                    status = true;
                                    message = "Task updated successfully.";
                                } catch (Exception e) {
                                    if (transaction != null) {
                                        transaction.rollback();
                                    }
                                    message = "Error occurred while updating task: " + e.getMessage();
                                } finally {
                                    hibernateSession.close();
                                }
                            }
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

    public String deleteTask(int id, HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (id <= 0) {
            message = "Invalid task ID.";
        } else {
            HttpSession httpSession = request.getSession(false);
            if (httpSession == null || httpSession.getAttribute("user") == null) {
                message = "User is not logged in.";
            } else {
                User loggedInUser = (User) httpSession.getAttribute("user");
                try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                    Task task = hibernateSession.get(Task.class, id);
                    if (task == null) {
                        message = "Task not found.";
                    } else if (task.getUser().getId() != loggedInUser.getId()) {
                        message = "You are not authorized to delete this task.";
                    } else {
                        Transaction transaction = hibernateSession.beginTransaction();
                        try {
                            hibernateSession.remove(task);
                            transaction.commit();
                            status = true;
                            message = "Task deleted successfully.";
                        } catch (Exception e) {
                            if (transaction != null) {
                                transaction.rollback();
                            }
                            message = "Error occurred while deleting task: " + e.getMessage();
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

    public String markAsComplete(int id, HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (id <= 0) {
            message = "Invalid task ID.";
        } else {
            HttpSession httpSession = request.getSession(false);
            if (httpSession == null || httpSession.getAttribute("user") == null) {
                message = "User is not logged in.";
            } else {
                User loggedInUser = (User) httpSession.getAttribute("user");
                try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                    Task task = hibernateSession.get(Task.class, id);
                    if (task == null) {
                        message = "Task not found.";
                    } else if (task.getUser().getId() != loggedInUser.getId()) {
                        message = "You are not authorized to update this task.";
                    } else {
                        Status completedStatus = hibernateSession.createQuery("FROM Status WHERE value = :value", Status.class)
                                .setParameter("value", Status.Type.COMPLETED.getValue())
                                .getSingleResultOrNull();
                        if (completedStatus == null) {
                            message = "Completed status not found in database.";
                        } else {
                            Transaction transaction = hibernateSession.beginTransaction();
                            try {
                                task.setStatus(completedStatus);
                                hibernateSession.merge(task);
                                transaction.commit();
                                status = true;
                                message = "Task marked as completed successfully.";
                            } catch (Exception e) {
                                if (transaction != null) {
                                    transaction.rollback();
                                }
                                message = "Error occurred while updating task: " + e.getMessage();
                            } finally {
                                hibernateSession.close();
                            }
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

    public String markAsOngoing(int id, HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (id <= 0) {
            message = "Invalid task ID.";
        } else {
            HttpSession httpSession = request.getSession(false);
            if (httpSession == null || httpSession.getAttribute("user") == null) {
                message = "User is not logged in.";
            } else {
                User loggedInUser = (User) httpSession.getAttribute("user");
                try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                    Task task = hibernateSession.get(Task.class, id);
                    if (task == null) {
                        message = "Task not found.";
                    } else if (task.getUser().getId() != loggedInUser.getId()) {
                        message = "You are not authorized to update this task.";
                    } else {
                        Status ongoingStatus = hibernateSession.createQuery("FROM Status WHERE value = :value", Status.class)
                                .setParameter("value", Status.Type.ONGOING.getValue())
                                .getSingleResultOrNull();
                        if (ongoingStatus == null) {
                            message = "Ongoing status not found in database.";
                        } else {
                            Transaction transaction = hibernateSession.beginTransaction();
                            try {
                                task.setStatus(ongoingStatus);
                                hibernateSession.merge(task);
                                transaction.commit();
                                status = true;
                                message = "Task marked as ongoing successfully.";
                            } catch (Exception e) {
                                if (transaction != null) {
                                    transaction.rollback();
                                }
                                message = "Error occurred while updating task: " + e.getMessage();
                            } finally {
                                hibernateSession.close();
                            }
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
