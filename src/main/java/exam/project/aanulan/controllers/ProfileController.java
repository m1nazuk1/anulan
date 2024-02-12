package exam.project.aanulan.controllers;

import exam.project.aanulan.dto.PersonDTO;
import exam.project.aanulan.models.Person;
import exam.project.aanulan.security.PersonDetails;
import exam.project.aanulan.services.AdminService;
import exam.project.aanulan.services.PersonDetailsService;
import exam.project.aanulan.services.PersonService;
import exam.project.aanulan.util.PersonValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.Map;


@Controller
public class ProfileController {
    private final AdminService adminService;

    private final PersonDetailsService personDetailsService;
    private final ModelMapper modelMapper;

    private final PersonValidator personValidator;

    private final PersonService personService;




    @Autowired
    public ProfileController(AdminService adminService, PersonDetailsService personDetailsService, ModelMapper modelMapper, PersonValidator personValidator, PersonService personService) {
        this.adminService = adminService;
        this.personDetailsService = personDetailsService;
        this.modelMapper = modelMapper;
        this.personValidator = personValidator;
        this.personService = personService;
    }

    @GetMapping("/hello")
    public String sayHello() {
        return "hello";
    }

    @GetMapping("/showUserInfo")
    @ResponseBody
    public ResponseEntity<?> showUserInfo() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Пользователь не аутентифицирован");
            }
//            PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
            Person person = personDetailsService.loadPersonByUsername(((PersonDetails) authentication.getPrincipal()).getUsername());

            return ResponseEntity.ok(Map.of(
                    "firstname", person.getFirstname(),
                    "lastname", person.getLastname(),
                    "yearOfBirth", person.getYearOfBirth(),
                    "description", person.getDescription(),
                    "username", person.getUsername(),
                    "password", person.getPassword()));
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при обработке данных пользователя");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Внутренняя ошибка сервера");
        }
    }


    @PostMapping("/editUserInfo")
    @ResponseBody
    public ResponseEntity<?> editUserInfo(@RequestBody @Valid PersonDTO personDTO, BindingResult bindingResult){
        Person personOld = personService.foundByUsername(personDTO.getUsername());

        Person personNew = convertToPerson(personDTO);
        personNew.setRole(personOld.getRole());
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при обработке данных пользователя");
        }

        personService.update(personOld.getUsername(), personNew);

        return  ResponseEntity.ok(Map.of("message", "данные пользователя успешно изменены"));

    }


    public Person convertToPerson(PersonDTO personDTO) {
        return this.modelMapper.map(personDTO, Person.class);
    }
    @GetMapping("/admin")
    public String adminPage() {
        adminService.doAdminStuff();
        return "admin";
    }
}