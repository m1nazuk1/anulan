/**
 * @author_Nizami_Alekperov
 */

package exam.project.aanulan.services;

import exam.project.aanulan.models.Image;
import exam.project.aanulan.models.Person;
import exam.project.aanulan.repositories.ImagesRepository;
import exam.project.aanulan.repositories.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImageService {
    private final PeopleRepository peopleRepository;
    private final ImagesRepository imagesRepository;

    @Autowired
    public ImageService(PeopleRepository peopleRepository, ImagesRepository imagesRepository) {
        this.peopleRepository = peopleRepository;
        this.imagesRepository = imagesRepository;
    }

    @Transactional
    public void enterImageId(String username, MultipartFile file) throws IOException {
        Person person = peopleRepository.findByUsername(username).get();

        Image image;
        if(file.getSize() != 0) {
            image = toImageEntity(file);
            image.setPersonUserName(username);
            image.setPreviewImage(true);
            image.setId(person.getId());
            image = imagesRepository.save(image);
            person.addImageToProduct(image);
            person.setPreviewImageId(image.getId());
            image.setPerson(person);
            imagesRepository.save(image);
        }

        peopleRepository.save(person);
    }

    public Image saveImage(MultipartFile file, Person person) throws IOException {
        String originalFileName = file.getOriginalFilename();
        Long size = file.getSize();
        byte[] data = file.getBytes();

        Image image = new Image();
        image.setPersonUserName(person.getUsername());
        image = toImageEntity(file);
        image.setPreviewImage(true);
        image.setOriginalFileName(originalFileName);
        image.setSize(size);
        image.setData(data);
        image.setPerson(person);

        return imagesRepository.save(image);
    }

    public Image toImageEntity(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setName(file.getName());
        image.setOriginalFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setData(file.getBytes());
        return image;
    }
}