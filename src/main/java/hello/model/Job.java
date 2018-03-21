package hello.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @JsonIgnore
    @OneToMany(mappedBy = "job")
    private Set<JobHours> jobHours = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "job")
    private Set<Material> materials = new HashSet<>();

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

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    public Set<JobHours> getJobHours() {
        return jobHours;
    }

    public void setJobHours(Set<JobHours> jobHours) {
        this.jobHours = jobHours;
    }

    public Set<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(Set<Material> materials) {
        this.materials = materials;
    }

    public Job merge(Job jobToMerge) {
        jobToMerge.setId(this.getId());
        return jobToMerge;
    }
}
