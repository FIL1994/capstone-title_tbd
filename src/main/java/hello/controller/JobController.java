package hello.controller;

import javax.validation.Valid;

import hello.EmptyJsonResponse;
import hello.model.Customer;
import hello.model.Employee;
import hello.repository.CustomerRepository;
import hello.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import hello.model.Job;
import hello.repository.JobRepository;

import java.util.Optional;

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

    @PostMapping("")
    public @ResponseBody
    Job createJob(@Valid @RequestBody Job job) {
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
            //TODO pass error message
            ResponseEntity.badRequest().build();
        }
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
    public ResponseEntity<Job> updateJob(@PathVariable(value = "id") Long jobId,
                                         @Valid @RequestBody Job jobDetails) {
        Optional<Job> optionalJob = jobRepository.findById(jobId);
        if (!optionalJob.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Job job = optionalJob.get();

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

        if(error.equals("")){
            Job updatedEmployee = jobRepository.save(job.merge(jobDetails));
            return ResponseEntity.ok(updatedEmployee);
        }
        else{
            //TODO: pass error message
            return ResponseEntity.badRequest().build();
        }

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
    public ResponseEntity<Job> setJobAvailability(
            @PathVariable(value = "id") Long jobId
    ) {
        Job job = jobRepository.findOne(jobId);
        if (job == null) {
            return ResponseEntity.notFound().build();
        }
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

    // add a customer to a job
    @PutMapping("{id}/customer/{customerId}")
    public ResponseEntity<Job> addCustomerToJob(
            @PathVariable(value = "id") Long jobId,
            @PathVariable(value = "customerId") Long customerId
    ) {
        Optional<Job> optionalJob = jobRepository.findById(jobId);
        if (!optionalJob.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Job job = optionalJob.get();

        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (!optionalCustomer.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Customer customer = optionalCustomer.get();

        job.setCustomer(customer);

        Job updatedJob = jobRepository.save(job);

        return ResponseEntity.ok(updatedJob);
    }

    // remove an employee from a job TODO
    @DeleteMapping("{id}/employee/{employeeId}")
    public ResponseEntity<Job> deleteEmployeeFromJob(
            @PathVariable(value = "id") Long jobId,
            @PathVariable(value = "employeeId") Long employeeId
    ) {
        Job job = jobRepository.findOne(jobId);
        if (job == null) {
            return ResponseEntity.notFound().build();
        }
        Employee employee = employeeRepository.findOne(employeeId);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }
        job.getEmployees().delete(employee);

        Job updatedJob = jobRepository.save(job);

        return ResponseEntity.ok(updatedJob);
    }

    // remove a customer from a job
    @PutMapping("{id}/customer/{customerId}")
    public ResponseEntity<Job> deleteCustomerToJob(
            @PathVariable(value = "id") Long jobId,
            @PathVariable(value = "customerId") Long customerId
    ) {
        Job job = jobRepository.findOne(jobId);
        if (job == null) {
            return ResponseEntity.notFound().build();
        }
        Customer customer = customerRepository.findOne(customerId);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }

        job.deleteCustomer(customer);

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
