package exam.project.aanulan.repositories;

import exam.project.aanulan.models.Image;
import exam.project.aanulan.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagesRepository extends JpaRepository<Image, Integer> {
    List<Image> findImagesByPerson(Person person);

    Image findImageBySize(Long size);
}