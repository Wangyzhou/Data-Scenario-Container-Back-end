package sceneContainer_backend.controller;

import sceneContainer_backend.domains.ResponseResult;
import sceneContainer_backend.pojo.dto.ChildrenDataDTO;
import sceneContainer_backend.pojo.dto.CreateCatalogDTO;
import sceneContainer_backend.service.MongoDBService.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/3/9 19:59
 */
@RestController
@RequestMapping("/catalog")
public class CatalogController {

    @Autowired
    private CatalogService catalogService;

    @PostMapping
    public ResponseResult createCatalog(@RequestBody CreateCatalogDTO createCatalogDTO) {
        return catalogService.createCatalog(createCatalogDTO);
    }

    @GetMapping("/getList")
    public ResponseResult getListByParentId(@RequestParam("userId") String userId, @RequestParam("catalogId") String catalogId) {
        List<ChildrenDataDTO> childrenList = catalogService.getCatalogListByCatalogIdAndUserId(userId, catalogId);
        if(!Objects.isNull(childrenList)) {
            return new ResponseResult(200, "获取目录列表成功！", childrenList);
        }
        return new ResponseResult(201, "获取目录列表失败！");
    }
    @GetMapping("/getRoot")
    public ResponseResult getRoot(@RequestParam("parentId") String parentId,@RequestParam("userId") String userId) {
        return catalogService.getCatalogByParentIdAndUserId(parentId, userId);
    }
    @DeleteMapping
    public ResponseResult deleteCatalog(@RequestParam("parentId") String catalogId,@RequestParam("userId") String userId) {
        return catalogService.deleteCatalog(catalogId, userId);
    }
    @GetMapping("/getCatalogBreadCrumb")
    public ResponseResult getCatalogBreadCrumb(@RequestParam("catalogId")String catalogId) {
        return catalogService.getCatalogBreadCrumb(catalogId);
    }
}
