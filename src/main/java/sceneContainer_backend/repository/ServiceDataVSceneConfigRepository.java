package sceneContainer_backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sceneContainer_backend.pojo.ServiceDataVSceneConfig;

@Repository
public interface ServiceDataVSceneConfigRepository extends MongoRepository<ServiceDataVSceneConfig,String> {

    ServiceDataVSceneConfig findServiceDataVSceneConfigBySceneId(String sceneId);

    ServiceDataVSceneConfig findServiceDataVSceneConfigById(String id);
}
