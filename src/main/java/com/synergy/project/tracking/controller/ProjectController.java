package com.synergy.project.tracking.controller;

import com.synergy.project.tracking.model.Contact;
import com.synergy.project.tracking.model.Project;
import com.synergy.project.tracking.model.Status;
import com.synergy.project.tracking.repo.ContactRepository;
import com.synergy.project.tracking.repo.ProjectRepository;
import com.synergy.project.tracking.repo.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class ProjectController {

    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    ContactRepository contactRepository;
    @Autowired
    StatusRepository statusRepository;

    @GetMapping("/status-list")
    public  ResponseEntity<List<Status>> getStatusList(){
        return new ResponseEntity<>(statusRepository.findAll(),HttpStatus.OK);
    }

    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getProjects(){
        return new ResponseEntity<>(projectRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/project/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable("id") Long id){
        Optional<Project> project = projectRepository.findById(id);
        if(project.isPresent()){
            return new ResponseEntity<>(project.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/delete/{id}")
    private ResponseEntity<Void> deleteBook(@PathVariable("id") Long id)
    {
        try{
            projectRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }catch (EmptyResultDataAccessException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update/{id}")
    public Project updateProject(@RequestBody Project newProject, @PathVariable("id") Long id) {
        System.out.println("newProject = " + newProject + ", id = " + id);
        //Todo need to correct
        //newProject.setStatus(statusRepository.findById(newProject.getStatus().getId()));
        HashSet<Contact> contacts = (HashSet) getPreparedContacts(newProject);
        newProject.setContacts(contacts);
        return projectRepository.findById(id)
            .map(project -> {
                project.setTitle(newProject.getTitle());
                project.setStatus(newProject.getStatus());
                project.setContacts(newProject.getContacts());
                return projectRepository.save(project);
            }).orElseGet(() -> {
                newProject.setId(id);
                return projectRepository.save(newProject);
            });
    }

    @PostMapping("/create")
    public Project createProject(@RequestBody Project newProject){
        System.out.println("newProject = " + newProject);
        return projectRepository.save(newProject);
    }

    private Set<Contact> getPreparedContacts(Project project){
        Set<Contact> result = new HashSet<>();
        if(project == null ||
                project.getContacts() == null ||
                project.getContacts().isEmpty())
            return result;
        //iterate over contacts, check if id <= 0 => new else update
        Iterator<Contact> it = project.getContacts().iterator();
        Contact cnt = null;
        while(it.hasNext()){
            cnt = it.next();
            if(cnt.getId() == 0){
                //Todo need to add checking is exists the contact
                //for preventing duplicate data in contacts
                result.add(contactRepository.save(cnt));
            }else{
                //need to update contact
                result.add(contactRepository.save(cnt));
            }
        }
        return result;
    }
}
