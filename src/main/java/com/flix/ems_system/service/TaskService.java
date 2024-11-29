package com.flix.ems_system.service;

import com.flix.ems_system.dto.TaskDTO;
import com.flix.ems_system.entity.Task;
import com.flix.ems_system.event.TaskEvent;
import com.flix.ems_system.exception.TaskNotFoundException;
import com.flix.ems_system.repository.TaskRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<String, TaskEvent> kafkaTemplate;
    private final String TASK_CREATED_EVENT = "TASK_CREATED_EVENT";
    private final String TASK_UPDATED_EVENT = "TASK_UPDATED_EVENT";
    private final String TASK_TOPIC = "task-topic";

    @Autowired
    public TaskService(TaskRepository taskRepository, ModelMapper modelMapper, KafkaTemplate<String, TaskEvent> kafkaTemplate) {
        this.taskRepository = taskRepository;
        this.modelMapper = modelMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<TaskDTO> findAll(){
        List<Task> taskList = taskRepository.findAll();
        List<TaskDTO> taskDTOList = new ArrayList<>();
        for(Task task: taskList){
            taskDTOList.add(modelMapper.map(task, TaskDTO.class));
        }
        return taskDTOList;
    }

    public TaskDTO findOne(int id){
        Optional<Task> optionalTask = taskRepository.findById(id);
        if(optionalTask.isPresent()){
            return modelMapper.map(optionalTask.get(), TaskDTO.class);
        }
        else{
            throw new TaskNotFoundException("Task not found with id " + id);
        }
    }

    public List<TaskDTO> search(String title, String description){
        List<Task> taskList = taskRepository.findByTitleAndDescription(title, description);
        List<TaskDTO> taskDTOList = new ArrayList<>();
        for(Task task : taskList){
            taskDTOList.add(modelMapper.map(task, TaskDTO.class));
        }
        return taskDTOList;
    }

    public TaskDTO create(TaskDTO taskDTO){
        Task task = modelMapper.map(taskDTO, Task.class);
        task.setEmployee(taskDTO.getEmployee());
        task = taskRepository.save(task);
        TaskDTO t = modelMapper.map(task, TaskDTO.class);
        publishTaskEvent(TASK_CREATED_EVENT, t);
        return t;
    }

    public TaskDTO update(TaskDTO taskDTO){
        Task task = modelMapper.map(taskDTO, Task.class);
        Task savedTask = taskRepository.save(task);
        TaskDTO t = modelMapper.map(savedTask, TaskDTO.class);
        publishTaskEvent(TASK_UPDATED_EVENT, t);
        return t;
    }

    public void delete(int id){
        taskRepository.deleteById(id);
    }

    public void publishTaskEvent(String event, TaskDTO t) {
        TaskEvent taskEvent = new TaskEvent(event, t.getId(), t.getTitle(),
                t.getDescription(), t.getStartDate(), t.getEndDate(),t.getStatus());
        kafkaTemplate.send(TASK_TOPIC, taskEvent);
    }
}
