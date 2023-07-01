package com.crud.crud.service;

import com.crud.crud.domain.Employee;
import com.crud.crud.repository.EmployeeRepository;
import com.crud.crud.service.dto.EmployeeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class for managing employees.
 * <p>
 *     By using @Transactional, you can ensure the consistency
 *     and integrity of data operations in your Spring Boot application
 *     and have fine-grained control over transaction boundaries.
 * </p>
 */
@Service
@Transactional
public class EmployeeService {

    private final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee createEmployee(EmployeeDTO employeeDTO){
        Employee employee = new Employee();
        employee.setFirstName(employeeDTO.getFirstName());
        employee.setLastName(employeeDTO.getLastName());
        if (employeeDTO.getEmail() != null){
            employee.setEmail(employeeDTO.getEmail().toLowerCase());
        }
        employee.setImageUrl(employeeDTO.getImageUrl());
        employee.setActivated(true);
        employeeRepository.save(employee);
        log.debug("Employee created: {}", employee);
        return employee;
    }

    @Transactional(readOnly = true)
    public Page<EmployeeDTO> getAllEmployees(Pageable pageable){
        return employeeRepository.findAll(pageable).map(EmployeeDTO::new);
    }

    /**
     * Update all information for a specific employee, and return the modified employee.
     *
     * @param employeeDTO employee to update.
     * @return updated employee.
     */
    public Optional<EmployeeDTO> updateEmployee(EmployeeDTO employeeDTO){
        return Optional
                .of(employeeRepository.findById(employeeDTO.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(employee -> {
                    employee.setFirstName(employeeDTO.getFirstName());
                    employee.setLastName(employeeDTO.getLastName());
                    if (employeeDTO.getEmail() != null){
                        employee.setEmail(employeeDTO.getEmail().toLowerCase());
                    }
                    employee.setImageUrl(employeeDTO.getImageUrl());
                    employee.setActivated(employeeDTO.isActivated());
                    log.debug("Updated Information for Employee: {}",employee);
                    return employee;
                }).map(EmployeeDTO::new);

    }

    public void deleteEmployee(String email){
        employeeRepository.findOneByEmailIgnoreCase(email).ifPresent(
            employee -> {
                employeeRepository.delete(employee);
                log.debug("Deleted Employee: {}",employee);
            });
    }
}
