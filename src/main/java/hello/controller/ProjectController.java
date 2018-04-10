package hello.controller;

import hello.EmptyJsonResponse;
import hello.model.*;
import hello.repository.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

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

    @GetMapping("{id}/jobs")
    public @ResponseBody
    Iterable<Job> getJobsForProject(@PathVariable(value = "id") Long projectId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);

        Project project = optionalProject.get();

        return project.getJobs();
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

        invoice.setProject(project);
        Invoice newInvoice = invoiceRepository.save(invoice);

        project.setInvoice(newInvoice);

        Project updatedProject = projectRepository.save(project);

        return ResponseEntity.ok(updatedProject);
    }

    @GetMapping(value = "/csv", produces="text/csv")
    @ResponseBody
    public FileSystemResource toCsv(HttpServletResponse response) throws IOException {
        Iterable<Project> projects = projectRepository.findAll();
        Iterator<Project> projectIterator = projects.iterator();

        String fileName = "projects-" +  java.util.UUID.randomUUID() + ".csv";

        try (
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName));

                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader(
                                "ID", "Description", "Date Opened", "Date Closed",
                                "Customer ID", "Customer First Name", "Customer Last Name"
                        ))
        ) {
            while(projectIterator.hasNext()) {
                Project project = projectIterator.next();
                Customer customer = project.getCustomer();

                Boolean hasCustomer = customer != null;

                csvPrinter.printRecord(
                        String.valueOf(project.getId()), project.getDescription(),
                        project.getDateOpened() == null ? "" : project.getDateOpened(),
                        project.getDateClosed() == null ? "" : project.getDateClosed(),
                        !hasCustomer ? "" : customer.getId(),
                        !hasCustomer ? "" : customer.getFirstName(),
                        !hasCustomer ? "" : customer.getLastName()
                );
            }
            csvPrinter.flush();
        }

        File file = new File(fileName);
        FileSystemResource fileSystemResource = new FileSystemResource(file);

        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        file.delete();
                    }
                },
                5000
        );

        return fileSystemResource;
    }
}
