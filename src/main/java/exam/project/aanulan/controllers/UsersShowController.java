/**
 * @author_Nizami_Alekperov
 */

package exam.project.aanulan.controllers;

import exam.project.aanulan.models.Person;
import exam.project.aanulan.security.PersonDetails;
import exam.project.aanulan.services.PersonDetailsService;
import exam.project.aanulan.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/users")
public class UsersShowController {
    private final PersonService personService;
    private final PersonDetailsService personDetailsService;

    @Autowired
    public UsersShowController(PersonService personService, PersonDetailsService personDetailsService) {
        this.personService = personService;
        this.personDetailsService = personDetailsService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> showAllUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Пользователь не аутентифицирован");
        }

        Person myPerson = personDetailsService.loadPersonByUsername(((PersonDetails) authentication.getPrincipal()).getUsername());
        List<Person> myAllUsers = personService.returnAllUsersExceptMe(myPerson.getUsername());

        if (myAllUsers.isEmpty()) {
            return ResponseEntity.ok(Map.of("myUsers", Collections.emptyList()));
        }

        return ResponseEntity.ok(Map.of(
                "myUsers", myAllUsers));
    }

    // Новый метод для получения id пользователя по его username
    @GetMapping("/id/{username}")
    public ResponseEntity<?> getUserIdByUsername(@PathVariable("username") String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Пользователь не аутентифицирован");
        }

        // Получаем данные о пользователе по его username
        Person user = personService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
        }

        return ResponseEntity.ok(Map.of("id", user.getId())); // Отправляем id пользователя
    }
}