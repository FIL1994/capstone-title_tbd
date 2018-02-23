package hello.controller;

import javax.validation.Valid;

import hello.model.EmployeeAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import hello.model.Job;
import hello.repository.JobRepository;
import hello.repository.CustomerRepository;
import hello.repository.EmployeeRepository;
import hello.repository.EmployeeAssignmentRepository;

import java.util.List;


@CrossOrigin
@RestController
@RequestMapping(path = "job")
public class JobController {
    @Autowired
    private JobRepository jobRepository;
    private CustomerRepository customerRepository;
    private EmployeeRepository employeeRepository;
    private EmployeeAssignmentRepository employeeAssignmentRepository;

    @PostMapping("")
    public @ResponseBody Job createJob(@Valid @RequestBody Job job) {
        String error = "";
        if(job.getDateOpened().getTime() < System.currentTimeMillis()){
            error += "Job cannot be opened in the past\n";
        }
        if(job.getDateOpened().getTime() > job.getDateClosed().getTime()){
            error += "Job open date cannot be after the job close date\n";
        }
        if(job.getDescription().equals(null) || job.getDescription().equals("")){
            error += "Job description cannot be empty\n";
        }
        if(job.getAvailable().equals(null)){
            job.setAvailable(false);
        }
        if(!job.getCustomerID().equals(null)){
            if(!customerRepository.findOne(job.getCustomerID()).equals(null)){
                error += "Customer does not exist\n";
            }
        }
        if(error.equals("")){
            return jobRepository.save(job);
        }
        else{
            return job;
        }
    }

    @GetMapping(path = {"", "/all"})
    public @ResponseBody
    Iterable<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Job> getJobById(@PathVariable(value = "id") Long jobId) {
        Job job = jobRepository.findOne(jobId);
        if(job == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(job);
    }

    @PutMapping("{id}")
    public ResponseEntity<Job> updateJob(@PathVariable(value = "id") Long jobId,
                                         @Valid @RequestBody Job jobDetails) {
        Job job = jobRepository.findOne(jobId);
        if(job == null) {
            return ResponseEntity.notFound().build();
        }

        String error = "";
        if(job.getDateOpened().getTime() < System.currentTimeMillis()){
            error += "Job cannot be opened in the past\n";
        }
        if(job.getDateOpened().getTime() > job.getDateClosed().getTime()){
            error += "Job open date cannot be after the job close date\n";
        }
        if(job.getDescription().equals(null) || job.getDescription().equals("")){
            error += "Job description cannot be empty";
        }
        if(job.getAvailable().equals(null)){
            job.setAvailable(false);
        }
        if(!job.getCustomerID().equals(null)){
            if(!customerRepository.findOne(job.getCustomerID()).equals(null)){
                error += "Customer does not exist\n";
            }
        }

        if(error.equals("")){
            Job updatedEmployee = jobRepository.save(job.merge(jobDetails));
            return ResponseEntity.ok(updatedEmployee);
        }
        else{
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Job> deleteJob(@PathVariable(value = "id") Long jobId) {
        Job job = jobRepository.findOne(jobId);
        if(job == null) {
            return ResponseEntity.notFound().build();
        }

        jobRepository.delete(job);
        return ResponseEntity.ok().build();
    }
}
