package exam.project.aanulan.controllers;

import exam.project.aanulan.dto.PersonDTO;
import exam.project.aanulan.models.Person;
import exam.project.aanulan.repositories.PeopleRepository;
import exam.project.aanulan.security.PersonDetails;
import exam.project.aanulan.services.AdminService;
import exam.project.aanulan.services.ImageService;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;


@Controller
public class ProfileController {
    private final AdminService adminService;

    private final PersonDetailsService personDetailsService;
    private final ModelMapper modelMapper;

    private final PersonValidator personValidator;

    private final PeopleRepository peopleRepository;

    private final PersonService personService;

    private final ImageService imageService;



    @Autowired
    public ProfileController(AdminService adminService, PersonDetailsService personDetailsService, ModelMapper modelMapper, PersonValidator personValidator, PeopleRepository peopleRepository, PersonService personService, ImageService imageService) {
        this.adminService = adminService;
        this.personDetailsService = personDetailsService;
        this.modelMapper = modelMapper;
        this.personValidator = personValidator;
        this.peopleRepository = peopleRepository;
        this.personService = personService;
        this.imageService = imageService;
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
                    "password", person.getPassword(),
                    "imageId", person.getPreviewImageId()));
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при обработке данных пользователя");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Внутренняя ошибка сервера");
        }
    }


    @GetMapping("/showProfile/{username}")
    @ResponseBody
    public ResponseEntity<?> showProfile(@PathVariable("username") String username){
        try {
            Person person = personService.foundByUsername(username);
            System.out.println(person);
            System.out.println(person.getFirstname());
            return ResponseEntity.ok(Map.of(
                    "firstname", person.getFirstname(),
                    "lastname", person.getLastname(),
                    "yearOfBirth", person.getYearOfBirth(),
                    "description", person.getDescription(),
                    "username", person.getUsername(),
                    "password", person.getPassword(),
                    "imageId", person.getPreviewImageId()));
        }catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при обработке данных пользователя");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Внутренняя ошибка сервера");
        }


    }


//    @PostMapping("/path-to-upload")
//    @CrossOrigin(origins = "http://localhost:3000")
//    @ResponseBody
//    public ResponseEntity<?> handleFileUpload(@RequestParam("image") MultipartFile file,
//                                              @RequestParam("username") String name) throws IOException {
//        if (file.isEmpty()) {
//            return ResponseEntity.badRequest().body("Файл не был получен");
//        }
//
//        imageService.enterImageId(name, file);
//
//
//        return ResponseEntity.ok("Файл успешно загружен: " + file.getOriginalFilename());
//    }


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