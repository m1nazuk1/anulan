package exam.project.aanulan.services;

import exam.project.aanulan.models.Person;
import exam.project.aanulan.repositories.PeopleRepository;
import exam.project.aanulan.security.PersonDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.Optional;


@Service
public class PersonDetailsService implements UserDetailsService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PersonDetailsService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<Person> person = peopleRepository.findByUsername(s);

        if (person.isEmpty())
            throw new UsernameNotFoundException("User not found");

        return new PersonDetails(person.get());
    }


    public Person loadPersonByUsername(String s){
        Optional<Person> person = peopleRepository.findByUsername(s);
        if (person.isEmpty())
            throw new UsernameNotFoundException("User not found");

        return person.get();
    }
}