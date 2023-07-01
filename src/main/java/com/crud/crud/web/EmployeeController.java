package com.crud.crud.web;

import com.crud.crud.domain.Employee;
import com.crud.crud.repository.EmployeeRepository;
import com.crud.crud.service.EmployeeService;
import com.crud.crud.service.dto.EmployeeDTO;
import com.crud.crud.web.errors.BadRequestAlertException;
import com.crud.crud.web.errors.EmailAlreadyExistsException;
import com.crud.crud.web.errors.UserNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/crud/")
public class EmployeeController {

    private final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeRepository employeeRepository;

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeRepository employeeRepository, EmployeeService employeeService) {
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
    }

    /**
     * {@code POST  /crud/employees/new}  : Creates a new employee.
     * <p>
     * Creates a new employee if the email is not already used.
     * The employee needs to be activated on creation.
     *
     * @param employeeDTO the employee to create.
     * @return the {@link ModelAndView} with view {employeeDetails (Created)}, or with status {employeeForm (Not created)} if the EmployeeDTO object is not valid.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     * @throws BadRequestAlertException {@code 400 (Bad Request)} if the email is already in use.
     */
    @PostMapping("/employees/new")
    public ModelAndView createEmployee(
            @RequestParam("imageUrl") MultipartFile multipartFile,
            @Valid  @ModelAttribute("employeeDTO") EmployeeDTO employeeDTO,
            BindingResult bindingResult) throws BadRequestAlertException, IOException {
        ModelAndView modelAndView = new ModelAndView();
        // validating the submitted EmployeeDTO object and checks for validation errors
        if (bindingResult.hasErrors()){
            modelAndView.setViewName("employeeForm");
        }
        // verifying EmployeeDTO email existence
        if (employeeDTO.getId() != null) {
            throw new BadRequestAlertException("A new employee cannot already have an ID", "employeeManagement", "idexists");
        } else if (employeeRepository.findOneByEmailIgnoreCase(employeeDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException();
        } else {
            if (!multipartFile.isEmpty()){
                try {
                    byte[] imageContent = multipartFile.getBytes();
                    employeeDTO.setImageUrl(imageContent);
                } catch (IOException e) {
                    throw new IOException("Image Upload failed.");
                }
            }
            Employee newEmployee = employeeService.createEmployee(employeeDTO);
            modelAndView.setViewName("employeeDetails");
            modelAndView.addObject("employee", newEmployee);
        }

        return modelAndView;
    }

    @GetMapping("/employees/new")
    public ModelAndView showEmployeeForm() {
        ModelAndView modelAndView = new ModelAndView("employeeForm");
        modelAndView.addObject("employeeDTO", new EmployeeDTO());
        return modelAndView;
    }

    /**
     * {@code GET /crud/employees or /crud/} : get all users with all the details - calling this are only allowed for the administrators.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @GetMapping(value = {"/employees/","/"})
    public ModelAndView getAllEmployees(Pageable pageable, Model model) {
        final Page<EmployeeDTO> page = employeeService.getAllEmployees(pageable);
        model.addAttribute("page", page);
//        int total = employeeService.
        model.addAttribute("total", page.getTotalElements());

        List<EmployeeDTO> employees = page.getContent();
        List<String> base64Images = new ArrayList<>();

        for (EmployeeDTO emp:employees) {
            byte[] image = emp.getImageUrl();
            String base64Image = java.util.Base64.getEncoder().encodeToString(image);
            base64Images.add(base64Image);
        }

        ModelAndView modelAndView = new ModelAndView("employeeList");
        modelAndView.addObject("employees", page.getContent());
        modelAndView.addObject("base64Images", base64Images);

        return modelAndView;

    }

    @GetMapping("/employee/getImage/{id}")
    @ResponseBody
    void showImage(@PathVariable("id") Long id, HttpServletResponse response, Optional<Employee> emp)
            throws IOException, UserNotFoundException {
        emp = employeeRepository.findById(id);
        if (emp.isPresent()){
            response.setContentType("image/jpeg,image/jpg,image/png,image/gif");
            response.getOutputStream().write(emp.get().getImageUrl());
            response.getOutputStream().close();
        } else
            throw new UserNotFoundException();

    }

    @DeleteMapping("/employee/delete/{email}")
    public String deleteEmployee(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referer,
                                               @PathVariable String email) {
        log.debug("Request to delete Employee: {}", email);
        employeeService.deleteEmployee(email);
        return "redirect:" + referer;
    }
//    @PutMapping("/employees")
//    public ResponseEntity<EmployeeDTO> updateEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) throws EmailAlreadyExistsException {
//        Optional<Employee> existingEmployee = employeeRepository.findOneByEmailIgnoreCase(employeeDTO.getEmail());
//        if (existingEmployee.isPresent() && existingEmployee.get().getId().equals(employeeDTO.getId())){
//            throw new EmailAlreadyExistsException();
//        }
//        Optional<EmployeeDTO> updatedEmployee = employeeService.updateEmployee(employeeDTO);
//
//        return Res
//    }
    // NO API
    public static HttpHeaders createAlert(String applicationName, String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-" + applicationName + "-alert", message);

        try {
            headers.add("X-" + applicationName + "-params", URLEncoder.encode(param, StandardCharsets.UTF_8.toString()));
        } catch (UnsupportedEncodingException var5) {
        }

        return headers;
    }
}
