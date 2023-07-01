package com.crud.crud.service.dto;

import com.crud.crud.domain.Employee;
import jakarta.persistence.Lob;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A DTO representing an employee.
 * <p>
 * DTOs play a crucial role in facilitating data transfer
 * and transformation between different layers or components
 * of a Spring Boot application. They help to control and
 * shape the data, optimize network usage, ensure data privacy,
 * and enhance the maintainability and flexibility of your application.
 * </p>
 */
public class EmployeeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @NotBlank
    @Email
    @Size(min = 5, max = 254)
    private String email;

    private boolean activated = false;

    @Lob
    private byte[] imageUrl;

    public EmployeeDTO() {

    }

    public EmployeeDTO(Employee employee) {
        this.id = employee.getId();
        this.email = employee.getEmail();
        this.activated = employee.isActivated();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.imageUrl = employee.getImageUrl();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public byte[] getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(byte[] imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "EmployeeDTO{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", activated=" + activated +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
