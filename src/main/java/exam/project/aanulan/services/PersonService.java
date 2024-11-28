/**
 * @author_Nizami_Alekperov
 */

package exam.project.aanulan.services;

import exam.project.aanulan.models.Person;
import exam.project.aanulan.repositories.PeopleRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
    private final PeopleRepository peopleRepository;

    public PersonService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    // Метод для поиска пользователя по username
    public Person foundByUsername(String username) {
        Optional<Person> check = peopleRepository.findByUsername(username);
        if (check.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return check.get();
    }

    // Обновление данных пользователя
    @Transactional
    public void update(String usernameOld, Person personNew) {
        Person personOld = foundByUsername(usernameOld);
        personNew.setPreviewImageId(personOld.getPreviewImageId());
        personNew.setId(personOld.getId());
        personNew.setRole(personOld.getRole());
        personNew.setPassword(personOld.getPassword());
        peopleRepository.save(personNew);
    }

    // Метод для получения всех пользователей, кроме текущего
    public List<Person> returnAllUsersExceptMe(String username) {
        List<Person> users = peopleRepository.findAll();
        List<Person> answer = new ArrayList<>();
        for (Person person : users) {
            if (!person.getUsername().equals(username)) {
                answer.add(person);
            }
        }
        return answer;
    }

    // Новый метод для получения пользователя по username
    public Person findByUsername(String username) {
        Optional<Person> person = peopleRepository.findByUsername(username);
        if (person.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return person.get();
    }
}