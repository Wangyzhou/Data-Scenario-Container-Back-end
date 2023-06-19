package sceneContainer_backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sceneContainer_backend.pojo.MapSceneConfig;

@Repository
public interface MapSceneConfigRepository extends MongoRepository<MapSceneConfig, String> {

    MapSceneConfig findMapSceneConfigBySceneId(String sceneId);

    MapSceneConfig deleteBySceneId(String sceneId);

    MapSceneConfig findMapSceneConfigById(String id);
}
