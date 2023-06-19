package sceneContainer_backend.repository.MogoDBRepository;

import sceneContainer_backend.pojo.Catalog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogRepository extends MongoRepository<Catalog, String> {

    Catalog findCatalogByParentId(String ParentId);

    Catalog findCatalogByIdAndUserId(String catalogId, String userId);

    Catalog findCatalogByParentIdAndUserId(String parentId, String userId);

    Catalog findOneByIdAndUserId(String catalogId, String userId);

    Catalog getCatalogById(String id);

    Catalog findCatalogById(String id);
}
