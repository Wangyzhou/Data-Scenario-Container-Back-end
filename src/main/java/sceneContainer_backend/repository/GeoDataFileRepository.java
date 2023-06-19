package sceneContainer_backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sceneContainer_backend.pojo.GeoDataFile;

import java.util.List;

@Repository
public interface GeoDataFileRepository extends MongoRepository<GeoDataFile,String> {
    GeoDataFile findOneByMd5AndUserId(String md5, String userId);

    GeoDataFile findOneById(String id);

    List<GeoDataFile> findAllByUserId(String userId);
}
