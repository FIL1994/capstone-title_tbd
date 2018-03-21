package hello.controller;

import hello.EmptyJsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import hello.model.*;
import hello.repository.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping(path = "customer")
public class CustomerController {
    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping("")
    public @ResponseBody
    Customer createCustomer(@Valid @RequestBody Customer customer) {
        return customerRepository.save(customer);
    }

    // allow get on /customer and /customer/all
    @GetMapping(path = {"", "/all"})
    public @ResponseBody
    Iterable<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable(value = "id") Long customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (!optionalCustomer.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Customer customer = optionalCustomer.get();
        return ResponseEntity.ok().body(customer);
    }

    // get all the jobs for a specific employee
    @GetMapping("{id}/projects")
    public ResponseEntity<Set<Project>> getCustomersProjects(@PathVariable(value = "id") Long customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (!optionalCustomer.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Customer customer = optionalCustomer.get();

        // return ResponseEntity.ok().body(employee.getJobs());
        return new ResponseEntity<>(customer.getProjects(), HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable(value = "id") Long customerId,
                                                   @Valid @RequestBody Customer customerDetails) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (!optionalCustomer.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Customer customer = optionalCustomer.get();

        Customer updatedCustomer = customerRepository.save(customer.merge(customerDetails));
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteCustomer(@PathVariable(value = "id") Long customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (!optionalCustomer.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Customer customer = optionalCustomer.get();

        customerRepository.delete(customer);
        return new ResponseEntity<>(new EmptyJsonResponse(), HttpStatus.OK);
    }
}
