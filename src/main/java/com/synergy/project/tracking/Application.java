package com.synergy.project.tracking;

import com.synergy.project.tracking.model.Contact;
import com.synergy.project.tracking.model.Project;
import com.synergy.project.tracking.model.Status;
import com.synergy.project.tracking.repo.ContactRepository;
import com.synergy.project.tracking.repo.ProjectRepository;
import com.synergy.project.tracking.repo.StatusRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	@Bean
	public CommandLineRunner mappingDemo(ContactRepository contactRepository,
										 ProjectRepository projectRepository,
										 StatusRepository statusRepository) {
		return args -> {
			Status statusNew = new Status("New");
			Status statusOld = new Status("Old");
			statusRepository.saveAll(Arrays.asList(statusNew,statusOld));

			Project project1 = new Project("Project_1", statusNew);
			Project project2 = new Project("Project_2", statusOld);
			projectRepository.saveAll(Arrays.asList(project1,project2));

			Contact contact1 = new Contact("Nikoghosyan Suren",
					"nikoghosyan.surenn@gmail.com", "096-355-292");
			Contact contact2 = new Contact("Talayan Aleqsandr",
					"nikoghosyan.surenn@gmail.com", "096-355-292");
			Contact contact3 = new Contact("Talalyan Eva",
					"nikoghosyan.surenn@gmail.com", "096-355-292");
			// save courses
			contactRepository.saveAll(Arrays.asList(contact1, contact2, contact3));

			// add courses to the student
			project1.getContacts().addAll(Arrays.asList(contact1, contact2, contact3));
			project2.getContacts().addAll(Arrays.asList(contact1,contact3));
			// update the student
			projectRepository.saveAll(Arrays.asList(project1,project2));
		};
	}

}
