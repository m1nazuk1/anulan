package exam.project.aanulan.controllers;

import exam.project.aanulan.dto.AuthenticationDTO;
import exam.project.aanulan.dto.PersonDTO;
import exam.project.aanulan.models.Person;
import exam.project.aanulan.security.JWTUtil;
import exam.project.aanulan.services.ImageService;
import exam.project.aanulan.services.RegistrationService;
import exam.project.aanulan.util.PersonValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final PersonValidator personValidator;
    private final JWTUtil jwtUtil;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;

    private final ImageService imageService;

    public static String username;

    @Autowired
    public AuthController(RegistrationService registrationService, PersonValidator personValidator,
                          JWTUtil jwtUtil, ModelMapper modelMapper, AuthenticationManager authenticationManager, ImageService imageService) {
        this.registrationService = registrationService;
        this.personValidator = personValidator;
        this.jwtUtil = jwtUtil;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
        this.imageService = imageService;
    }

    @PostMapping("/registration")
    public Map<String, String> performRegistration(@RequestBody @Valid PersonDTO personDTO, BindingResult bindingResult) throws IOException {

        Person person = convertToPerson(personDTO);

        personValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors()) {
            return Map.of("message", "Ошибка!");
        }

        registrationService.register(person);

        String token = jwtUtil.generateToken(person.getUsername());
        return Map.of("jwt-token", token);
    }
    @Transactional
    @PutMapping("/forImage")
    public ResponseEntity<?> forImage(@RequestParam("image") MultipartFile image) throws IOException {


        imageService.enterImageId(username, image);

        return ResponseEntity.ok(Map.of("message", "данные пользователя успешно изменены"));
    }

    @PostMapping("/login")
    public Map<String, String> performLogin(@RequestBody AuthenticationDTO authenticationDTO) {
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getUsername(),
                        authenticationDTO.getPassword());

        this.username = authenticationDTO.getUsername();
        try {
            authenticationManager.authenticate(authInputToken);
        } catch (BadCredentialsException e) {
            return Map.of("message", "Неверно введен пароль или логин!");
        }

        String token = jwtUtil.generateToken(authenticationDTO.getUsername());
        System.out.println(token);
        return Map.of("jwt-token", token);
    }

    public Person convertToPerson(PersonDTO personDTO) {
        return this.modelMapper.map(personDTO, Person.class);
    }
}