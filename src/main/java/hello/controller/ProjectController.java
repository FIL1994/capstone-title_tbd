package hello.controller;

import hello.EmptyJsonResponse;
import hello.model.*;
import hello.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping(path = "project")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @PostMapping("")
    public ResponseEntity<?> createProject(@Valid @RequestBody Project project) {
        String error = "";
        if (project.getDateOpened() == null) {
            error += "Date opened cannot be null. ";
        } else {
            if (project.getDateOpened().getTime() < System.currentTimeMillis()) {
                error += "Project cannot be opened in the past. ";
            }
            if (project.getDateClosed() != null && project.getDateOpened().getTime() > project.getDateClosed().getTime()) {
                error += "Project open date cannot be after the project close date. ";
            }
        }

        if (project.getDescription() == null || project.getDescription().equals("")) {
            error += "Project description cannot be empty. ";
        }

        if (!error.equals("")) {
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(projectRepository.save(project));
    }

    @GetMapping(path = {"", "/all"})
    public @ResponseBody
    Iterable<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable(value = "id") Long projectId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (!optionalProject.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Project project = optionalProject.get();

        return ResponseEntity.ok().body(project);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateProject(@PathVariable(value = "id") Long projectId,
                                       @Valid @RequestBody Project projectDetails) {
        String error = "";
        if (projectDetails.getDateOpened() == null) {
            error += "Date opened cannot be null. ";
        } else {
            if (projectDetails.getDateOpened().getTime() < System.currentTimeMillis()) {
                error += "Job cannot be opened in the past. ";
            }
            if (projectDetails.getDateClosed() != null && projectDetails.getDateOpened().getTime() > projectDetails.getDateClosed().getTime()) {
                error += "Job open date cannot be after the job close date. ";
            }
        }

        if (projectDetails.getDescription() == null || projectDetails.getDescription().equals("")) {
            error += "Job description cannot be empty. ";
        }

        if (!error.equals("")) {
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (!optionalProject.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Project project = optionalProject.get();

        Project updatedJob = projectRepository.save(project.merge(projectDetails));

        return ResponseEntity.ok(updatedJob);
    }

    // add a customer to a project
    @PutMapping("{id}/customer/{customerId}")
    public ResponseEntity<Project> addCustomerToProject(
            @PathVariable(value = "id") Long projectId,
            @PathVariable(value = "customerId") Long customerId
    ) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (!optionalProject.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Project project = optionalProject.get();

        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (!optionalCustomer.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Customer customer = optionalCustomer.get();

        project.setCustomer(customer);

        Project updatedJob = projectRepository.save(project);

        return ResponseEntity.ok(updatedJob);
    }

    @PutMapping("{id}/invoice")
    public ResponseEntity<?> addInvoiceToProject(@Valid @RequestBody Invoice invoice, @PathVariable(value ="id") Long projectId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (!optionalProject.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Project project = optionalProject.get();

        Invoice newInvoice = invoiceRepository.save(invoice);

        project.setInvoice(newInvoice);

        Project updatedProject = projectRepository.save(project);

        return ResponseEntity.ok(updatedProject);
    }
}
