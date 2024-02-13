package exam.project.aanulan.services;

import exam.project.aanulan.models.Image;
import exam.project.aanulan.models.Person;
import exam.project.aanulan.repositories.ImagesRepository;
import exam.project.aanulan.repositories.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


@Service
public class RegistrationService {

    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImagesRepository imagesRepository;

    @Autowired
    public RegistrationService(PeopleRepository peopleRepository, PasswordEncoder passwordEncoder, ImagesRepository imagesRepository) {
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
        this.imagesRepository = imagesRepository;
    }

    @Transactional
    public void register(Person person) throws IOException {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setRole("ROLE_USER");
        Image image = imagesRepository.findById(1).get();
        person.setPreviewImageId(image.getId());
        person.setImages(Collections.singletonList(image));


        peopleRepository.save(person);

    }

}