package sceneContainer_backend.repository.PostgresRepository;

import sceneContainer_backend.pojo.PostgresPOJO.TestShp;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestShpDao extends MongoRepository<TestShp,String> {

}
