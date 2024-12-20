/**
 * @author_Nizami_Alekperov
 */

package exam.project.aanulan.controllers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import exam.project.aanulan.dto.AuthenticationDTO;
import exam.project.aanulan.dto.PersonDTO;
import exam.project.aanulan.models.Person;
import exam.project.aanulan.security.JWTUtil;
import exam.project.aanulan.security.PersonDetails;
import exam.project.aanulan.services.ImageService;
import exam.project.aanulan.services.PersonDetailsService;
import exam.project.aanulan.services.RegistrationService;
import exam.project.aanulan.util.PersonValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final RegistrationService registrationService;
    private final PersonValidator personValidator;
    private final JWTUtil jwtUtil;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final PersonDetailsService personDetailsService;
    private final ImageService imageService;

    @Autowired
    public AuthController(RegistrationService registrationService, PersonValidator personValidator,
                          JWTUtil jwtUtil, ModelMapper modelMapper, AuthenticationManager authenticationManager, PersonDetailsService personDetailsService, ImageService imageService) {
        this.registrationService = registrationService;
        this.personValidator = personValidator;
        this.jwtUtil = jwtUtil;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
        this.personDetailsService = personDetailsService;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Пользователь не аутентифицирован");
        }
        Person person = personDetailsService.loadPersonByUsername(((PersonDetails) authentication.getPrincipal()).getUsername());

        imageService.enterImageId(person.getUsername(), image);

        return ResponseEntity.ok(Map.of("message", "данные пользователя успешно изменены"));
    }

    @PostMapping("/login")
    public Map<String, String> performLogin(@RequestBody AuthenticationDTO authenticationDTO) {
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getUsername(),
                        authenticationDTO.getPassword());

        try {
            authenticationManager.authenticate(authInputToken);
        } catch (BadCredentialsException e) {
            return Map.of("message", "Неверно введен пароль или логин!");
        }

        String token = jwtUtil.generateToken(authenticationDTO.getUsername());
        return Map.of("jwt-token", token);
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            if (!jwt.isBlank()) {
                try {
                    String username = jwtUtil.validateTokenAndRetrieveClaim(jwt);
                    Person person = personDetailsService.loadPersonByUsername(username);
                    return ResponseEntity.ok(person);
                } catch (JWTVerificationException e) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT Token");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }

    public Person convertToPerson(PersonDTO personDTO) {
        return this.modelMapper.map(personDTO, Person.class);
    }
}