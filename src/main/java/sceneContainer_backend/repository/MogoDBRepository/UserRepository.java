package sceneContainer_backend.repository.MogoDBRepository;

import sceneContainer_backend.pojo.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User,String> {

    User findUserByName(String username);

}
