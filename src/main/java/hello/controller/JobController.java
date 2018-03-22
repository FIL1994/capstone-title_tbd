package hello.controller;

import javax.validation.Valid;

import hello.EmptyJsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import hello.model.*;
import hello.repository.*;

import java.util.Optional;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping(path = "job")
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @PostMapping("")
    public ResponseEntity<?> createJob(@Valid @RequestBody Job job) {
        String error = "";
        if (job.getDateOpened() == null) {
            error += "Date opened cannot be null. ";
        } else {
            if (job.getDateOpened().getTime() < System.currentTimeMillis()) {
                error += "Job cannot be opened in the past. ";
            }
            if (job.getDateClosed() != null && job.getDateOpened().getTime() > job.getDateClosed().getTime()) {
                error += "Job open date cannot be after the job close date. ";
            }
        }

        if (job.getDescription() == null || job.getDescription().equals("")) {
            error += "Job description cannot be empty. ";
        }
        if (job.getAvailable() == null) {
            job.setAvailable(false);
        }

        if (!error.equals("")) {
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(jobRepository.save(job));
    }

    @GetMapping(path = {"", "/all"})
    public @ResponseBody
    Iterable<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Job> getJobById(@PathVariable(value = "id") Long jobId) {
        Optional<Job> optionalJob = jobRepository.findById(jobId);
        if (!optionalJob.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Job job = optionalJob.get();

        return ResponseEntity.ok().body(job);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateJob(@PathVariable(value = "id") Long jobId,
                                       @Valid @RequestBody Job jobDetails) {
        String error = "";
        if (jobDetails.getDateOpened() == null) {
            error += "Date opened cannot be null. ";
        } else {
            if (jobDetails.getDateOpened().getTime() < System.currentTimeMillis()) {
                error += "Job cannot be opened in the past. ";
            }
            if (jobDetails.getDateClosed() != null && jobDetails.getDateOpened().getTime() > jobDetails.getDateClosed().getTime()) {
                error += "Job open date cannot be after the job close date. ";
            }
        }

        if (jobDetails.getDescription() == null || jobDetails.getDescription().equals("")) {
            error += "Job description cannot be empty. ";
        }
        if (jobDetails.getAvailable() == null) {
            jobDetails.setAvailable(false);
        }

        if (!error.equals("")) {
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        Optional<Job> optionalJob = jobRepository.findById(jobId);
        if (!optionalJob.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Job job = optionalJob.get();

        Job updatedJob = jobRepository.save(job.merge(jobDetails));

        return ResponseEntity.ok(updatedJob);
    }

    // set a job as available
    @PutMapping("{id}/available")
    public ResponseEntity<Job> setJobAvailability(
            @PathVariable(value = "id") Long jobId
    ) {
        Optional<Job> optionalJob = jobRepository.findById(jobId);
        if (!optionalJob.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Job job = optionalJob.get();

        job.setAvailable(true);

        Job updatedJob = jobRepository.save(job);

        return ResponseEntity.ok(updatedJob);
    }

    // set a job as unavailable
    @PutMapping("{id}/unavailable")
    public ResponseEntity<Job> setJobUnavailable(
            @PathVariable(value = "id") Long jobId
    ) {
        Optional<Job> optionalJob = jobRepository.findById(jobId);
        if (!optionalJob.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Job job = optionalJob.get();

        job.setAvailable(false);

        Job updatedJob = jobRepository.save(job);

        return ResponseEntity.ok(updatedJob);
    }

    // add an employee to a job
    @PutMapping("{id}/employee/{employeeId}")
    public ResponseEntity<Job> addEmployeeToJob(
            @PathVariable(value = "id") Long jobId,
            @PathVariable(value = "employeeId") Long employeeId
    ) {
        Optional<Job> optionalJob = jobRepository.findById(jobId);
        if (!optionalJob.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Job job = optionalJob.get();

        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (!optionalEmployee.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Employee employee = optionalEmployee.get();

        job.getEmployees().add(employee);

        Job updatedJob = jobRepository.save(job);

        return ResponseEntity.ok(updatedJob);
    }

    @PutMapping("{id}/material")
    public ResponseEntity<?> addMaterialToJob(@Valid @RequestBody Material material, @PathVariable(value ="id") Long jobId) {
        Optional<Job> optionalJob = jobRepository.findById(jobId);
        if (!optionalJob.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Job job = optionalJob.get();

        Material newMaterial = materialRepository.save(material);

        job.getMaterials().add(newMaterial);

//        for(Material m : job.getMaterials()) {
//            System.out.println(m.getDescription());
//            try{
//                System.out.println(m.getId());
//            } catch(Exception e){}
//        }

        Job updatedJob = jobRepository.save(job);

        return ResponseEntity.ok(updatedJob);
    }

    // add a customer to a job
//    @PutMapping("{id}/customer/{customerId}")
//    public ResponseEntity<Job> addCustomerToJob(
//            @PathVariable(value = "id") Long jobId,
//            @PathVariable(value = "customerId") Long customerId
//    ) {
//        Optional<Job> optionalJob = jobRepository.findById(jobId);
//        if (!optionalJob.isPresent()) {
//            return ResponseEntity.notFound().build();
//        }
//        Job job = optionalJob.get();
//
//        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
//        if (!optionalCustomer.isPresent()) {
//            return ResponseEntity.notFound().build();
//        }
//        Customer customer = optionalCustomer.get();
//
//        job.setCustomer(customer);
//
//        Job updatedJob = jobRepository.save(job);
//
//        return ResponseEntity.ok(updatedJob);
//    }

    // remove an employee from a job TODO
    @DeleteMapping("{id}/employee/{employeeId}")
    public ResponseEntity<Job> deleteEmployeeFromJob(
            @PathVariable(value = "id") Long jobId,
            @PathVariable(value = "employeeId") Long employeeId
    ) {
        Optional<Job> optionalJob = jobRepository.findById(jobId);
        if (!optionalJob.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Job job = optionalJob.get();

        Optional<Employee> optionalEmployee= employeeRepository.findById(employeeId);
        if(!optionalEmployee.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Employee employee = optionalEmployee.get();

        job.getEmployees().remove(employee);

        Job updatedJob = jobRepository.save(job);
        return ResponseEntity.ok(updatedJob);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteJob(@PathVariable(value = "id") Long jobId) {
        Optional<Job> optionalJob = jobRepository.findById(jobId);
        if (!optionalJob.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Job job = optionalJob.get();

        jobRepository.delete(job);
        return new ResponseEntity<>(new EmptyJsonResponse(), HttpStatus.OK);
    }
}
