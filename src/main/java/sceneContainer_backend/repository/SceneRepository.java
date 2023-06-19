package sceneContainer_backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sceneContainer_backend.pojo.Scene;

import java.util.List;

@Repository
public interface SceneRepository extends MongoRepository<Scene, String> {
    List<Scene> findAllByUserId(String userId);

    Scene findSceneBySceneId(String sceneId);
}
