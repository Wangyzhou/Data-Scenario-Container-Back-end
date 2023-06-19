package sceneContainer_backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sceneContainer_backend.pojo.ServiceDataVSceneTool;

@Repository
public interface ServiceDataVSceneToolRepository extends MongoRepository<ServiceDataVSceneTool, String> {

    ServiceDataVSceneTool findServiceDataVSceneToolById(String id);
}
