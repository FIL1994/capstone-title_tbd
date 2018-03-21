package hello.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.*;
import java.sql.Date;

@Entity
public class Employee extends Person {
    private Date birthDate;
    private String address;
    private String city;
    private String socialInsuranceNumber;
    private Date payrollStartDate;
    private String phoneNumber;
    private String postalCode;

    @JsonIgnore
    @ManyToMany(mappedBy = "employee")
    private Set<EmergencyContact> emergencyContacts = new HashSet<>();

    /*
     ManyToMany relationship si managed by employees in the Job model.

     JsonIgnore --> don't send the jobs connected to an employee when getting an employee.
     This prevents infinite recursion.
     */
    @JsonIgnore
    @ManyToMany(mappedBy = "employees")
    private Set<Job> jobs = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "employee")
    private Set<JobHours> jobHours = new HashSet<>();

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSocialInsuranceNumber() {
        return socialInsuranceNumber;
    }

    public void setSocialInsuranceNumber(String socialInsuranceNumber) {
        this.socialInsuranceNumber = socialInsuranceNumber;
    }

    public Date getPayrollStartDate() {
        return payrollStartDate;
    }

    public void setPayrollStartDate(Date payrollStartDate) {
        this.payrollStartDate = payrollStartDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Set<Job> getJobs() {
        return jobs;
    }

    public void setJobs(Set<Job> jobs) {
        this.jobs = jobs;
    }

    public Set<EmergencyContact> getEmergencyContacts() {
        return emergencyContacts;
    }

    public void setEmergencyContacts(Set<EmergencyContact> emergencyContacts) {
        this.emergencyContacts = emergencyContacts;
    }

    public Set<JobHours> getJobHours() {
        return jobHours;
    }

    public void setJobHours(Set<JobHours> jobHours) {
        this.jobHours = jobHours;
    }

    public Employee merge(Employee employeeToMerge) {
        employeeToMerge.setId(this.getId());
        return employeeToMerge;
    }
}
