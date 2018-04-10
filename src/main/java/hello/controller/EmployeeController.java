package hello.controller;

import hello.EmptyJsonResponse;
import hello.model.Job;
import org.apache.commons.csv.CSVFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.apache.commons.csv.CSVPrinter;

import hello.model.Employee;
import hello.repository.EmployeeRepository;

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
@RequestMapping(path = "employee")
public class EmployeeController {
    @Autowired
    private EmployeeRepository employeeRepository;

    // post an employee
    @PostMapping("")
    public @ResponseBody
    Employee createEmployee(@Valid @RequestBody Employee employee) {
        return employeeRepository.save(employee);
    }

    // get all employees which "/employee" or "/employee/all"
    @GetMapping(path = {"", "/all"})
    public @ResponseBody
    Iterable<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // get a specific employee
    @GetMapping("{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable(value = "id") Long employeeId) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (!optionalEmployee.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Employee employee = optionalEmployee.get();
        return ResponseEntity.ok().body(employee);
    }

    // get all the jobs for a specific employee
    @GetMapping("{id}/jobs")
    public ResponseEntity<Set<Job>> getEmployeesJobs(@PathVariable(value = "id") Long employeeId) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (!optionalEmployee.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Employee employee = optionalEmployee.get();

        // return ResponseEntity.ok().body(employee.getJobs());
        return new ResponseEntity<>(employee.getJobs(), HttpStatus.OK);
    }

    // put (edit) an employee
    @PutMapping("{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable(value = "id") Long employeeId,
                                                   @Valid @RequestBody Employee employeeDetails) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (!optionalEmployee.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Employee employee = optionalEmployee.get();

        Employee updatedEmployee = employeeRepository.save(employee.merge(employeeDetails));
        return ResponseEntity.ok(updatedEmployee);
    }

    // delete an employee
    @DeleteMapping("{id}")
    public ResponseEntity deleteEmployee(@PathVariable(value = "id") Long employeeId) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (!optionalEmployee.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Employee employee = optionalEmployee.get();

        employeeRepository.delete(employee);
        return new ResponseEntity<>(new EmptyJsonResponse(), HttpStatus.OK);
    }

    @GetMapping(value = "/csv", produces="text/csv")
    @ResponseBody
    public FileSystemResource toCsv(HttpServletResponse response) throws IOException {
        Iterable<Employee> employees = employeeRepository.findAll();
        Iterator<Employee> employeeIterator = employees.iterator();

        String fileName = "employees-" +  java.util.UUID.randomUUID() + ".csv";

        try (
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName));

                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader(
                                "ID", "First Name", "Last Name", "Email", "Birth Date", "Address",
                                "City", "Postal Code", "Payroll Start Date",
                                "Phone Number"
                        ))
        ) {
            while(employeeIterator.hasNext()) {
                Employee employee = employeeIterator.next();
                csvPrinter.printRecord(
                        String.valueOf(employee.getId()), employee.getFirstName(), employee.getLastName(),
                        employee.getEmail(), employee.getBirthDate() == null ? "" : employee.getBirthDate().toString(),
                        employee.getAddress(), employee.getCity(), employee.getPostalCode(),
                        employee.getPayrollStartDate() == null ? "" : employee.getPayrollStartDate().toString(),
                        employee.getPhoneNumber()
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
