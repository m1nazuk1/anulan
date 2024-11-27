package exam.project.aanulan.services;

import exam.project.aanulan.controllers.AuthController;
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

    public Person foundByUsername(String username){
        Optional<Person> check = peopleRepository.findByUsername(username);
        if (check.isEmpty())
            throw new UsernameNotFoundException("User not found");

        return check.get();
    }

    @Transactional
    public void update(String usernameOld, Person personNew){
        Person personOld = foundByUsername(usernameOld);
        personNew.setPreviewImageId(personOld.getPreviewImageId());
        personNew.setId(personOld.getId());
        personNew.setRole(personOld.getRole());
        personNew.setPassword(personOld.getPassword());
        peopleRepository.save(personNew);
    }

    public List<Person> returnAllUsersExceptMe(String username){
        List<Person> users = peopleRepository.findAll();
        System.out.println(users);
        List<Person> answer = new ArrayList<>();
        for (Person person: users) {
            if (!person.getUsername().equals(username)){
                answer.add(person);
            }
        }
        return answer;
    }

}