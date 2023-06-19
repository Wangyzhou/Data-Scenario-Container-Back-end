package sceneContainer_backend.service.MongoDBService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import sceneContainer_backend.domains.ResponseResult;
import sceneContainer_backend.pojo.Catalog;
import sceneContainer_backend.pojo.User;
import sceneContainer_backend.pojo.dto.ChildrenDataDTO;
import sceneContainer_backend.pojo.dto.CreateCatalogDTO;
import sceneContainer_backend.pojo.dto.DeleteOrImportGeoFileDTO;
import sceneContainer_backend.pojo.dto.DeleteOrImportPdfFileDTO;
import sceneContainer_backend.repository.MogoDBRepository.CatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sceneContainer_backend.service.GeoDataFileService;
import sceneContainer_backend.service.PdfFileService;

import java.util.*;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/3/9 20:00
 */
@Slf4j
@Service
public class CatalogService {

    @Autowired
    private CatalogRepository catalogRepository;

    @Autowired
    private GeoDataFileService geoDataFileService;

    @Autowired
    private PdfFileService pdfFileService;

    @Value("${resourcesPath}")
    private String resourcesRoot;

    public Catalog createCatalogByUser(User user) {
        List<ChildrenDataDTO> children = new ArrayList<>();
        String catalogId = UUID.randomUUID().toString();
        Catalog catalog = new Catalog(catalogId, "MyData", "-1", children, 0, user.getId(), 0, new Date());
        log.info("用户：" + user.getName() + "创建根目录:");
        return catalogRepository.insert(catalog);
    }

    public ResponseResult createCatalog(CreateCatalogDTO createCatalogDTO) {
        List<ChildrenDataDTO> children = new ArrayList<>();
        Catalog catalogByParentId = catalogRepository.getCatalogById(createCatalogDTO.getParentCatalogId());
        int level = catalogByParentId.getLevel();
        Catalog catalog = new Catalog(UUID.randomUUID().toString(), createCatalogDTO.getCatalogName(), createCatalogDTO.getParentCatalogId(), children, 0, createCatalogDTO.getUserId(), level + 1, new Date());
        Catalog save = catalogRepository.save(catalog);
        ChildrenDataDTO folder = new ChildrenDataDTO(save.getId(), createCatalogDTO.getCatalogName(), "folder", "folder", new Date());
        catalogByParentId.getChildren().add(folder);
        int total = catalogByParentId.getTotal();
        catalogByParentId.setTotal(total + 1);
        Catalog saveParentCatalog = catalogRepository.save(catalogByParentId);
        if (!Objects.isNull(save) && !Objects.isNull(saveParentCatalog)) {
            log.info("创建文件夹：" + createCatalogDTO.getCatalogName());
            return new ResponseResult(200, "success");
        }
        return new ResponseResult(201, "fail");
    }

    public List<ChildrenDataDTO> getCatalogListByCatalogIdAndUserId(String userId, String catalogId) {
        Catalog parentCatalog = catalogRepository.findCatalogByIdAndUserId(catalogId, userId);
        List<ChildrenDataDTO> childrenData = parentCatalog.getChildren();
        return childrenData;
    }

    public ResponseResult getCatalogByParentIdAndUserId(String parentCatalogId, String userId) {
        Catalog rootCatalog = catalogRepository.findCatalogByParentIdAndUserId(parentCatalogId, userId);
        return new ResponseResult(200, "获取根目录成功", rootCatalog);
    }

    public void deleteEmptyCatalog(String catalogId, String parentCatalogId) {
        Catalog catalog = catalogRepository.getCatalogById(parentCatalogId);
        List<ChildrenDataDTO> children = catalog.getChildren();
        Iterator<ChildrenDataDTO> iterator = children.iterator();
        while (iterator.hasNext()) {
            ChildrenDataDTO temp = iterator.next();
            if (temp.getId().equals(catalogId)) {
                iterator.remove();
                break;
            }
        }
        catalog.setTotal(catalog.getTotal() - 1);
        catalogRepository.save(catalog);
        catalogRepository.deleteById(catalogId);
    }

    public ResponseResult deleteCatalog(String catalogId, String userId) {
        //
        Boolean isDelete = deleteCatalogByRecursion(catalogId, userId);
        if (isDelete) {
            return new ResponseResult(200, "删除成功！");
        }
        return new ResponseResult(201, "删除失败！");
    }

    //递归删除catalog中所有的对象
    public Boolean deleteCatalogByRecursion(String catalogId, String userId) {
        //
        Catalog currentCatalog = catalogRepository.findCatalogById(catalogId);
        //递归出口：当catalog中没有对象或者只有一个对象且该对象为file
        if (currentCatalog.getChildren().size() == 0) {
            deleteEmptyCatalog(catalogId, currentCatalog.getParentId());
            return true;
        } else if (currentCatalog.getChildren().size() == 1 && !currentCatalog.getChildren().get(0).getFileType().equals("folder")) {
            switch (currentCatalog.getChildren().get(0).getFileType()) {
                case "shp":
                    DeleteOrImportGeoFileDTO deleteOrImportGeoFileDTO = new DeleteOrImportGeoFileDTO(userId, catalogId, currentCatalog.getChildren().get(0).getId());
                    geoDataFileService.delete(deleteOrImportGeoFileDTO);        //默认成功，递归回滚？
                    deleteEmptyCatalog(catalogId, currentCatalog.getParentId());
                    return true;
                case "pdf":
                    DeleteOrImportPdfFileDTO deleteOrImportPdfFileDTO = new DeleteOrImportPdfFileDTO(currentCatalog.getChildren().get(0).getId(), userId, catalogId);
                    pdfFileService.delete(deleteOrImportPdfFileDTO);
                    deleteEmptyCatalog(catalogId, currentCatalog.getParentId());
                    break;
            }
        }
        for (int i = 0; i < currentCatalog.getChildren().size(); i++) {
            ChildrenDataDTO childrenData = currentCatalog.getChildren().get(i);
            switch (childrenData.getFileType()) {
                case "folder":
                    deleteCatalogByRecursion(childrenData.getId(), userId);
                    break;
                case "shp":
                    DeleteOrImportGeoFileDTO shpFile = new DeleteOrImportGeoFileDTO(userId, catalogId, childrenData.getId());
                    geoDataFileService.delete(shpFile);
                    break;
                case "pdf":
                    DeleteOrImportPdfFileDTO pdfFile = new DeleteOrImportPdfFileDTO(childrenData.getId(), userId, catalogId);
                    pdfFileService.delete(pdfFile);
                    break;
            }
        }
        deleteEmptyCatalog(catalogId, currentCatalog.getParentId());
        return true;
    }

    public ResponseResult getCatalogBreadCrumb(String catalogId) {
        Catalog currentCatalog = catalogRepository.getCatalogById(catalogId);
        LinkedList<Map<String, Object>> catalogIdList = new LinkedList<>();
        while (currentCatalog != null) {
            HashMap<String, Object> breadcrumbItem = new HashMap<>();
            breadcrumbItem.put("id", currentCatalog.getLevel());
            breadcrumbItem.put("title", currentCatalog.getName());
            breadcrumbItem.put("catalogId", currentCatalog.getId());
            catalogIdList.addFirst(breadcrumbItem);
            currentCatalog = catalogRepository.getCatalogById(currentCatalog.getParentId());
        }
        return new ResponseResult(200, "获取成功！", catalogIdList);
    }

    public String getCatalogFileSystemListByCatalogId(String catalogId) {
        Catalog currentCatalog = catalogRepository.getCatalogById(catalogId);
        String userId = currentCatalog.getUserId();
        String currentCatalogId = catalogId;
        String currentParentId = "";
        String catalogPath = "";
        LinkedList<String> catalogIdList = new LinkedList<>();
        catalogIdList.addFirst(currentCatalogId);
        while ((currentParentId = catalogRepository.getCatalogById(currentCatalogId).getParentId()).compareTo("-1") != 0) {
            currentCatalogId = currentParentId;
            catalogIdList.addFirst(currentCatalogId);
        }
        Iterator<String> it = catalogIdList.iterator();
        while (it.hasNext()) {
            catalogPath = catalogPath + "/" + it.next();
        }
        String fullPath = resourcesRoot + "/" + userId + catalogPath;
        return fullPath;
    }
}
