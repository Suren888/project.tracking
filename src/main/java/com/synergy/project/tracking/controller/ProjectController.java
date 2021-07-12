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

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
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
    public ResponseEntity updateProject(@RequestBody Project newProject, @PathVariable("id") Long id) {
        try{
            validateProject(newProject);
        }catch (final ConstraintViolationException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        System.out.println("newProject = " + newProject + ", id = " + id);
        //Todo need to correct
        HashSet<Contact> contacts = (HashSet) getPreparedContacts(newProject);
        newProject.setContacts(contacts);
        return projectRepository.findById(id)
            .map(project -> {
                project.setTitle(newProject.getTitle());
                project.setStatus(newProject.getStatus());
                project.setContacts(newProject.getContacts());
                return new ResponseEntity(projectRepository.save(project), HttpStatus.OK);
            }).orElseGet(() -> {
                newProject.setId(id);
                return new ResponseEntity(projectRepository.save(newProject), HttpStatus.OK);
            });
    }

    @PostMapping("/create")
    public ResponseEntity createProject(@RequestBody Project newProject){
        System.out.println("newProject = " + newProject);
        try{
            validateProject(newProject);
        }catch (final ConstraintViolationException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        HashSet<Contact> contacts = (HashSet) getPreparedContacts(newProject);
        newProject.setContacts(contacts);
        return new ResponseEntity<Project>(projectRepository.save(newProject), HttpStatus.OK);
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

    @Autowired
    private Validator validator;
    private void validateProject(Project project){
        Set<ConstraintViolation<Project>> violations = validator.validate(project);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Project> constraintViolation : violations) {
                sb.append(constraintViolation.getMessage());
            }
            Iterator<Contact> it = project.getContacts().iterator();
            Contact cnt = null;
            while(it.hasNext()){
                cnt = it.next();
                Set<ConstraintViolation<Contact>> violationsContact = validator.validate(cnt);
                if (!violations.isEmpty()) {
                    for (ConstraintViolation<Contact> constraintViolation : violationsContact) {
                        sb.append(constraintViolation.getMessage());
                    }
                }
            }
            if(sb.length()>0)
                throw new ConstraintViolationException("Error occurred: " + sb.toString(), violations);
        }
    }
}
