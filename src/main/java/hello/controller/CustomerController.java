package hello.controller;

import hello.EmptyJsonResponse;
import org.apache.commons.csv.CSVFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.apache.commons.csv.CSVPrinter;

import hello.model.*;
import hello.repository.*;

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

    @GetMapping(value = "/csv", produces="text/csv")
    @ResponseBody
    public FileSystemResource toCsv(HttpServletResponse response) throws IOException {
        Iterable<Customer> customers = customerRepository.findAll();
        Iterator<Customer> customerIterator = customers.iterator();

        String fileName = "customers-" + java.util.UUID.randomUUID() + ".csv";

        try (
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName));

                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader(
                                "ID", "First Name", "Last Name", "Email", "Cell Phone Number",
                                "Work Phone Number", "Company Name",
                                "Primary Contact Name", "Primary Contact Email", "Primary Contact Phone Number",
                                "Secondary Contact Name", "Secondary Contact Email", "Secondary Contact Phone Number"
                        ))
        ) {
            while(customerIterator.hasNext()) {
                Customer customer = customerIterator.next();
                csvPrinter.printRecord(
                        String.valueOf(customer.getId()), customer.getFirstName(), customer.getLastName(),
                        customer.getEmail(), customer.getCellPhoneNumber(), customer.getWorkPhoneNumber(),
                        customer.getCompanyName(), customer.getPrimaryContactName(), customer.getPrimaryEmail(),
                        customer.getPrimaryPhoneNumber(), customer.getSecondaryContactName(),
                        customer.getSecondaryEmail(), customer.getSecondaryPhoneNumber()
                );
                csvPrinter.flush();
            }
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
