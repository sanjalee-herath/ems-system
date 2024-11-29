package com.flix.ems_system.controller;

import com.flix.ems_system.dto.TaskDTO;
import com.flix.ems_system.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("task")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    @GetMapping("{id}")
    public TaskDTO getTaskById(@PathVariable int id){
        return taskService.findOne(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_admin')")
    public TaskDTO createTask(@RequestBody TaskDTO task){
        return taskService.create(task);
    }

    @GetMapping
    public List<TaskDTO> getAllTasks(){
        return taskService.findAll();
    }

    @GetMapping("search")
    public List<TaskDTO> searchTask(@RequestParam String title, @RequestParam String description){
        return taskService.search(title, description);
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_admin')")
    public TaskDTO updateTask(@RequestBody TaskDTO taskDTO) {
        return taskService.update(taskDTO);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_admin')")
    public void deleteTask(@PathVariable int id) {
        taskService.delete(id);
    }
}
