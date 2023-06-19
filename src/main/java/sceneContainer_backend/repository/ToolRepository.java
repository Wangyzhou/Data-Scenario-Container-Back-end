package sceneContainer_backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sceneContainer_backend.pojo.Tool;

import java.util.List;

@Repository
public interface ToolRepository extends MongoRepository<Tool, String> {
    List<Tool> findAllByType(String type);

    Tool findToolById(String id);
}
