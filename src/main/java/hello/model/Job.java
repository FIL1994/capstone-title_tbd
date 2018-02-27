package hello.model;

import javax.persistence.*;
import java.util.*;
import java.sql.Date;

@Entity
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date dateOpened;
    private Date dateClosed;
    private String description;
    private Boolean available;

    // make a table called "employee_assignments" to handle the ManyToMany relationship with Employee
    @ManyToMany
    @JoinTable(name = "employee_assignments")
    private Set<Employee> employees = new HashSet<>();

    @ManyToOne
    @PrimaryKeyJoinColumn
    private Customer customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateOpened() {
        return dateOpened;
    }

    public void setDateOpened(Date dateOpened) {
        this.dateOpened = dateOpened;
    }

    public Date getDateClosed() {
        return dateClosed;
    }

    public void setDateClosed(Date dateClosed) {
        this.dateClosed = dateClosed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void deleteCustomer(Customer customer) {
        this.customer = customer;
    }

    public Job merge(Job jobToMerge) {
        jobToMerge.setId(this.getId());
        return jobToMerge;
    }
}
