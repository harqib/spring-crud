package com.crud.crud.config;

import com.crud.crud.domain.Employee;
import com.crud.crud.repository.EmployeeRepository;
import com.crud.crud.service.dto.EmployeeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup)
            return;

        // Specify the path to your image file
        String imagePath = "classpath:static/images/employee1.jpg";
        byte[] imageData = null;
        try {
            Resource resource = resourceLoader.getResource(imagePath);
            InputStream inputStream = resource.getInputStream();
            imageData = StreamUtils.copyToByteArray(inputStream);
        } catch (IOException e) {
            // Handle any exceptions that occur during file reading
            e.printStackTrace();
        }
        creatEmployeeIfNotFound("employee1@test.com","emp1","test",imageData);
        imagePath = "classpath:static/images/employee2.jpg";
        try {
            Resource resource = resourceLoader.getResource(imagePath);
            InputStream inputStream = resource.getInputStream();
            imageData = StreamUtils.copyToByteArray(inputStream);
        } catch (IOException e) {
            // Handle any exceptions that occur during file reading
            e.printStackTrace();
        }
        creatEmployeeIfNotFound("employee2@test.com","emp2","test",imageData);

        alreadySetup = true;
        System.out.println("launched the app .............");
    }


    void creatEmployeeIfNotFound(String email, String fname, String lname, byte[] image) {
        if(employeeRepository.findOneByEmailIgnoreCase(email).isEmpty()) {
            Employee employee = new Employee();
            employee.setEmail(email);
            employee.setFirstName(fname);
            employee.setLastName(lname);
            employee.setImageUrl(image);
            employee.setActivated(true);
            employeeRepository.save(employee);
        }
    }
}
