package com.wigerlabs.TideTempo.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wigerlabs.TideTempo.dto.SubTaskDTO;
import com.wigerlabs.TideTempo.entity.Priority;
import com.wigerlabs.TideTempo.entity.Status;
import com.wigerlabs.TideTempo.entity.SubTask;
import com.wigerlabs.TideTempo.entity.Task;
import com.wigerlabs.TideTempo.entity.User;
import com.wigerlabs.TideTempo.util.AppUtil;
import com.wigerlabs.TideTempo.util.HibernateUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class SubTaskService {
    public String addSubTask(SubTaskDTO subTaskDTO, HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (subTaskDTO == null) {
            message = "Invalid subtask data.";
        } else if (subTaskDTO.getName() == null || subTaskDTO.getName().isEmpty()) {
            message = "Subtask name is required.";
        } else if (subTaskDTO.getTaskId() <= 0) {
            message = "Task ID is required.";
        } else if (subTaskDTO.getPriorityId() <= 0) {
            message = "Priority is required.";
        } else if (subTaskDTO.getStatusId() <= 0) {
            message = "Status is required.";
        } else {
            HttpSession httpSession = request.getSession(false);
            if (httpSession == null || httpSession.getAttribute("user") == null) {
                message = "User is not logged in.";
            } else {
                User loggedInUser = (User) httpSession.getAttribute("user");
                try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                    SubTask existingSubTask = hibernateSession.createQuery("FROM SubTask WHERE name = :name", SubTask.class)
                            .setParameter("name", subTaskDTO.getName())
                            .getSingleResultOrNull();
                    if (existingSubTask != null) {
                        message = "Subtask with this name already exists.";
                    } else {
                        Task task = hibernateSession.get(Task.class, subTaskDTO.getTaskId());
                        if (task == null) {
                            message = "Invalid task.";
                        } else if (task.getUser().getId() != loggedInUser.getId()) {
                            message = "You are not authorized to add subtask to this task.";
                        } else {
                            Priority priority = hibernateSession.get(Priority.class, subTaskDTO.getPriorityId());
                            Status subTaskStatus = hibernateSession.get(Status.class, subTaskDTO.getStatusId());

                            if (priority == null) {
                                message = "Invalid priority.";
                            } else if (subTaskStatus == null) {
                                message = "Invalid status.";
                            } else {
                                SubTask newSubTask = new SubTask();
                                newSubTask.setName(subTaskDTO.getName());
                                newSubTask.setDescription(subTaskDTO.getDescription());
                                newSubTask.setTimeSpent(subTaskDTO.getTimeSpent());
                                newSubTask.setTask(task);
                                newSubTask.setPriority(priority);
                                newSubTask.setStatus(subTaskStatus);

                                Transaction transaction = hibernateSession.beginTransaction();
                                try {
                                    hibernateSession.persist(newSubTask);
                                    transaction.commit();
                                    status = true;
                                    message = "Subtask created successfully.";
                                } catch (Exception e) {
                                    if (transaction != null) {
                                        transaction.rollback();
                                    }
                                    message = "Error occurred while creating subtask: " + e.getMessage();
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

    public String getAllSubTasks(HttpServletRequest request) {
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
                List<SubTask> subTasks = hibernateSession.createQuery("FROM SubTask WHERE task.user.id = :userId", SubTask.class)
                        .setParameter("userId", loggedInUser.getId())
                        .list();

                for (SubTask subTask : subTasks) {
                    JsonObject subTaskObject = new JsonObject();
                    subTaskObject.addProperty("id", subTask.getId());
                    subTaskObject.addProperty("name", subTask.getName());
                    subTaskObject.addProperty("description", subTask.getDescription());
                    subTaskObject.addProperty("timeSpent", subTask.getTimeSpent());
                    subTaskObject.addProperty("taskId", subTask.getTask().getId());
                    subTaskObject.addProperty("priorityId", subTask.getPriority().getId());
                    subTaskObject.addProperty("priorityName", subTask.getPriority().getValue());
                    subTaskObject.addProperty("statusId", subTask.getStatus().getId());
                    subTaskObject.addProperty("statusName", subTask.getStatus().getValue());
                    dataArray.add(subTaskObject);
                }

                status = true;
                message = "Subtasks retrieved successfully.";
            } catch (Exception e) {
                message = "Error occurred while retrieving subtasks: " + e.getMessage();
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        responseObject.add("data", dataArray);
        return responseObject.toString();
    }

    public String getSubTaskById(int id, HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        JsonObject dataObject = new JsonObject();

        if (id <= 0) {
            message = "Invalid subtask ID.";
        } else {
            HttpSession httpSession = request.getSession(false);
            if (httpSession == null || httpSession.getAttribute("user") == null) {
                message = "User is not logged in.";
            } else {
                User loggedInUser = (User) httpSession.getAttribute("user");
                try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                    SubTask subTask = hibernateSession.get(SubTask.class, id);
                    if (subTask == null) {
                        message = "Subtask not found.";
                    } else if (subTask.getTask().getUser().getId() != loggedInUser.getId()) {
                        message = "You are not authorized to view this subtask.";
                    } else {
                        SubTaskDTO subTaskDTO = new SubTaskDTO(
                                subTask.getId(),
                                subTask.getName(),
                                subTask.getDescription(),
                                subTask.getTimeSpent(),
                                subTask.getTask().getId(),
                                subTask.getPriority().getId(),
                                subTask.getStatus().getId()
                        );
                        dataObject.add("subtask", AppUtil.GSON.toJsonTree(subTaskDTO));
                        status = true;
                        message = "Subtask retrieved successfully.";
                    }
                } catch (Exception e) {
                    message = "Error occurred while retrieving subtask: " + e.getMessage();
                }
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        responseObject.add("data", dataObject);
        return responseObject.toString();
    }

    public String getSubTasksByTaskId(int taskId, HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";
        JsonArray dataArray = new JsonArray();

        if (taskId <= 0) {
            message = "Invalid task ID.";
        } else {
            HttpSession httpSession = request.getSession(false);
            if (httpSession == null || httpSession.getAttribute("user") == null) {
                message = "User is not logged in.";
            } else {
                User loggedInUser = (User) httpSession.getAttribute("user");
                try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                    Task task = hibernateSession.get(Task.class, taskId);
                    if (task == null) {
                        message = "Task not found.";
                    } else if (task.getUser().getId() != loggedInUser.getId()) {
                        message = "You are not authorized to view subtasks for this task.";
                    } else {
                        List<SubTask> subTasks = hibernateSession.createQuery("FROM SubTask WHERE task.id = :taskId", SubTask.class)
                                .setParameter("taskId", taskId)
                                .list();

                        for (SubTask subTask : subTasks) {
                            SubTaskDTO subTaskDTO = new SubTaskDTO(
                                    subTask.getId(),
                                    subTask.getName(),
                                    subTask.getDescription(),
                                    subTask.getTimeSpent(),
                                    subTask.getTask().getId(),
                                    subTask.getPriority().getId(),
                                    subTask.getStatus().getId()
                            );
                            dataArray.add(AppUtil.GSON.toJsonTree(subTaskDTO));
                        }

                        status = true;
                        message = "Subtasks retrieved successfully.";
                    }
                } catch (Exception e) {
                    message = "Error occurred while retrieving subtasks: " + e.getMessage();
                }
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        responseObject.add("data", dataArray);
        return responseObject.toString();
    }

    public String updateSubTask(SubTaskDTO subTaskDTO, HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (subTaskDTO == null) {
            message = "Invalid subtask data.";
        } else if (subTaskDTO.getId() <= 0) {
            message = "Subtask ID is required.";
        } else if (subTaskDTO.getName() == null || subTaskDTO.getName().isEmpty()) {
            message = "Subtask name is required.";
        } else if (subTaskDTO.getTaskId() <= 0) {
            message = "Task ID is required.";
        } else if (subTaskDTO.getPriorityId() <= 0) {
            message = "Priority is required.";
        } else if (subTaskDTO.getStatusId() <= 0) {
            message = "Status is required.";
        } else {
            HttpSession httpSession = request.getSession(false);
            if (httpSession == null || httpSession.getAttribute("user") == null) {
                message = "User is not logged in.";
            } else {
                User loggedInUser = (User) httpSession.getAttribute("user");
                try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                    SubTask existingSubTask = hibernateSession.get(SubTask.class, subTaskDTO.getId());
                    if (existingSubTask == null) {
                        message = "Subtask not found.";
                    } else if (existingSubTask.getTask().getUser().getId() != loggedInUser.getId()) {
                        message = "You are not authorized to update this subtask.";
                    } else {
                        SubTask duplicateSubTask = hibernateSession.createQuery("FROM SubTask WHERE name = :name AND id != :id", SubTask.class)
                                .setParameter("name", subTaskDTO.getName())
                                .setParameter("id", subTaskDTO.getId())
                                .getSingleResultOrNull();
                        if (duplicateSubTask != null) {
                            message = "Subtask with this name already exists.";
                        } else {
                            Task task = hibernateSession.get(Task.class, subTaskDTO.getTaskId());
                            if (task == null) {
                                message = "Invalid task.";
                            } else if (task.getUser().getId() != loggedInUser.getId()) {
                                message = "You are not authorized to assign subtask to this task.";
                            } else {
                                Priority priority = hibernateSession.get(Priority.class, subTaskDTO.getPriorityId());
                                Status subTaskStatus = hibernateSession.get(Status.class, subTaskDTO.getStatusId());

                                if (priority == null) {
                                    message = "Invalid priority.";
                                } else if (subTaskStatus == null) {
                                    message = "Invalid status.";
                                } else {
                                    Transaction transaction = hibernateSession.beginTransaction();
                                    try {
                                        existingSubTask.setName(subTaskDTO.getName());
                                        existingSubTask.setDescription(subTaskDTO.getDescription());
                                        existingSubTask.setTimeSpent(subTaskDTO.getTimeSpent());
                                        existingSubTask.setTask(task);
                                        existingSubTask.setPriority(priority);
                                        existingSubTask.setStatus(subTaskStatus);

                                        hibernateSession.merge(existingSubTask);
                                        transaction.commit();
                                        status = true;
                                        message = "Subtask updated successfully.";
                                    } catch (Exception e) {
                                        if (transaction != null) {
                                            transaction.rollback();
                                        }
                                        message = "Error occurred while updating subtask: " + e.getMessage();
                                    } finally {
                                        hibernateSession.close();
                                    }
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

    public String deleteSubTask(int id, HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (id <= 0) {
            message = "Invalid subtask ID.";
        } else {
            HttpSession httpSession = request.getSession(false);
            if (httpSession == null || httpSession.getAttribute("user") == null) {
                message = "User is not logged in.";
            } else {
                User loggedInUser = (User) httpSession.getAttribute("user");
                try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                    SubTask subTask = hibernateSession.get(SubTask.class, id);
                    if (subTask == null) {
                        message = "Subtask not found.";
                    } else if (subTask.getTask().getUser().getId() != loggedInUser.getId()) {
                        message = "You are not authorized to delete this subtask.";
                    } else {
                        Transaction transaction = hibernateSession.beginTransaction();
                        try {
                            hibernateSession.remove(subTask);
                            transaction.commit();
                            status = true;
                            message = "Subtask deleted successfully.";
                        } catch (Exception e) {
                            if (transaction != null) {
                                transaction.rollback();
                            }
                            message = "Error occurred while deleting subtask: " + e.getMessage();
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
            message = "Invalid subtask ID.";
        } else {
            HttpSession httpSession = request.getSession(false);
            if (httpSession == null || httpSession.getAttribute("user") == null) {
                message = "User is not logged in.";
            } else {
                User loggedInUser = (User) httpSession.getAttribute("user");
                try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                    SubTask subTask = hibernateSession.get(SubTask.class, id);
                    if (subTask == null) {
                        message = "Subtask not found.";
                    } else if (subTask.getTask().getUser().getId() != loggedInUser.getId()) {
                        message = "You are not authorized to update this subtask.";
                    } else {
                        Status completedStatus = hibernateSession.createQuery("FROM Status WHERE value = :value", Status.class)
                                .setParameter("value", Status.Type.COMPLETED.getValue())
                                .getSingleResultOrNull();
                        if (completedStatus == null) {
                            message = "Completed status not found in database.";
                        } else {
                            Transaction transaction = hibernateSession.beginTransaction();
                            try {
                                subTask.setStatus(completedStatus);
                                hibernateSession.merge(subTask);
                                transaction.commit();
                                status = true;
                                message = "Subtask marked as completed successfully.";
                            } catch (Exception e) {
                                if (transaction != null) {
                                    transaction.rollback();
                                }
                                message = "Error occurred while updating subtask: " + e.getMessage();
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
            message = "Invalid subtask ID.";
        } else {
            HttpSession httpSession = request.getSession(false);
            if (httpSession == null || httpSession.getAttribute("user") == null) {
                message = "User is not logged in.";
            } else {
                User loggedInUser = (User) httpSession.getAttribute("user");
                try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
                    SubTask subTask = hibernateSession.get(SubTask.class, id);
                    if (subTask == null) {
                        message = "Subtask not found.";
                    } else if (subTask.getTask().getUser().getId() != loggedInUser.getId()) {
                        message = "You are not authorized to update this subtask.";
                    } else {
                        Status ongoingStatus = hibernateSession.createQuery("FROM Status WHERE value = :value", Status.class)
                                .setParameter("value", Status.Type.ONGOING.getValue())
                                .getSingleResultOrNull();
                        if (ongoingStatus == null) {
                            message = "Ongoing status not found in database.";
                        } else {
                            Transaction transaction = hibernateSession.beginTransaction();
                            try {
                                subTask.setStatus(ongoingStatus);
                                hibernateSession.merge(subTask);
                                transaction.commit();
                                status = true;
                                message = "Subtask marked as ongoing successfully.";
                            } catch (Exception e) {
                                if (transaction != null) {
                                    transaction.rollback();
                                }
                                message = "Error occurred while updating subtask: " + e.getMessage();
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
