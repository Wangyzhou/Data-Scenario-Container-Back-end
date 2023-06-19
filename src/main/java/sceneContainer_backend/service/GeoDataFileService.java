package sceneContainer_backend.service;

import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import sceneContainer_backend.domains.ResponseResult;
import sceneContainer_backend.pojo.Catalog;
import sceneContainer_backend.pojo.GeoDataFile;
import sceneContainer_backend.pojo.PdfFile;
import sceneContainer_backend.pojo.dto.*;
import sceneContainer_backend.repository.GeoDataFileRepository;
import sceneContainer_backend.repository.MogoDBRepository.CatalogRepository;
import sceneContainer_backend.repository.PostgresRepository.ShpProcessRepository;
import sceneContainer_backend.utils.FileUtils;
import sceneContainer_backend.utils.unZipUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/3/10 21:33
 */
@Service
public class GeoDataFileService {


    @Autowired
    private ShpProcessRepository shpProcessRepository;

    @Autowired
    private GeoDataFileRepository geoDataFileRepository;

    @Autowired
    private CatalogRepository catalogRepository;
    @Value("${resourcesPath}")
    private String resourcesRoot;

    public ResponseResult create(UploadShapefileDTO uploadShapefileDTO) {
        //1、入postgres：文件解压  →   shp →   save
        List<String> unZipFiles = new ArrayList<>();
        String srcFilePath = "";
        String tableName = "";
        try {
            String md5 = DigestUtils.md5DigestAsHex(uploadShapefileDTO.getFile().getInputStream());
            String originalFilename = uploadShapefileDTO.getFile().getOriginalFilename();
            int size = (int) uploadShapefileDTO.getFile().getSize();
            String originalFilenameWithoutPrefix = originalFilename.substring(0, originalFilename.lastIndexOf("."));
            String filePrefix = originalFilename.substring(originalFilename.lastIndexOf("."), originalFilename.length());
            String fileType;
            String fileTotalName = uploadShapefileDTO.getFileName() + filePrefix;
            String ShpId = IdUtil.objectId();
            tableName = originalFilenameWithoutPrefix + "_" + ShpId;
            GeoDataFile oneByMd5AndUserId = geoDataFileRepository.findOneByMd5AndUserId(md5, uploadShapefileDTO.getUserId());
            if (!Objects.isNull(oneByMd5AndUserId)) {
                return new ResponseResult(201, "此文件已在你的仓库！不可重复上传");
            }
            //生成本地目录
            //TODO:封装成工具
            String currentCatalogId = uploadShapefileDTO.getCatalogId();
            String currentParentId = "";
            String catalogPath = "";
            LinkedList<String> catalogIdList = new LinkedList<>();
            catalogIdList.addFirst(uploadShapefileDTO.getCatalogId());
            while ((currentParentId = catalogRepository.getCatalogById(currentCatalogId).getParentId()).compareTo("-1") != 0) {
                currentCatalogId = currentParentId;
                catalogIdList.addFirst(currentCatalogId);
            }
            Iterator<String> it = catalogIdList.iterator();
            while (it.hasNext()) {
                catalogPath = catalogPath + "/" + it.next();
            }
            String uploadPath = resourcesRoot + "/" + uploadShapefileDTO.getUserId() + catalogPath;
            File geoFilePath = new File(resourcesRoot + "/" + uploadShapefileDTO.getUserId() + catalogPath);
            if (!geoFilePath.exists()) {
                geoFilePath.mkdirs();
            }

            boolean isUploaded = FileUtils.uploadSingleFile(uploadShapefileDTO.getFile(), uploadPath, originalFilename);
            srcFilePath = uploadPath + "/" + originalFilename;
            unZipFiles = unZipUtils.unZipFiles(srcFilePath, uploadPath + "/");
            String shpPath = unZipUtils.selectShpfile(unZipFiles);
            fileType = shpPath.substring(shpPath.lastIndexOf(".") + 1);
            if (!isUploaded || !shpPath.contains(".shp")) {
                throw new Exception("上传文件失败或Shapefile文件不符合规范！");
            }
            //2、入postgres，到这一步说明zip上传成功且shp文件解压完毕，可以入库
            Boolean isSave2Pg = shpProcessRepository.shp2pgsql(shpPath, tableName, uploadShapefileDTO.getSrid().toString(), uploadShapefileDTO.getCode());
            if (!isSave2Pg) {
                throw new Exception("空间数据存入postgresql失败！！！");
            }
            //3、在mongo中记录文件信息
            GeoDataFile geoDataFile = new GeoDataFile();
            HashMap<String, String> nameList = new HashMap<>();
            String dataType = shpProcessRepository.getShpType(tableName);
            nameList.put(uploadShapefileDTO.getCatalogId(), originalFilename);
            List<Double> bounds = shpProcessRepository.getShpBox2D(tableName);
            geoDataFile.setId(ShpId);
            geoDataFile.setNameList(nameList);
            geoDataFile.setSize(size);
            geoDataFile.setDate(new Date());
            geoDataFile.setMd5(md5);
            geoDataFile.setDownloadNum(0);
            geoDataFile.setPath(srcFilePath);
            geoDataFile.setUnZipFilesPath(unZipFiles);
            geoDataFile.setPtName(tableName);
            geoDataFile.setOriginalName(originalFilename);
            geoDataFile.setDisplayName(uploadShapefileDTO.getFileName());
            geoDataFile.setDataType(shpProcessRepository.getShpType(tableName));
            geoDataFile.setUserId(uploadShapefileDTO.getUserId());
            geoDataFile.setBounds(bounds);
            geoDataFile.setCenter(this.getCenter(bounds));
            geoDataFile.setCode(uploadShapefileDTO.getCode());
            geoDataFile.setSrid(shpProcessRepository.getSRID(tableName));
            GeoDataFile isInsert = geoDataFileRepository.insert(geoDataFile);
            //TODO:封装
            Catalog fileParentCatalog = catalogRepository.getCatalogById(uploadShapefileDTO.getCatalogId());
            fileParentCatalog.setTotal(fileParentCatalog.getTotal() + 1);
            ChildrenDataDTO fileProfileData = new ChildrenDataDTO(ShpId, uploadShapefileDTO.getFileName(), fileType, dataType, new Date());
            fileParentCatalog.getChildren().add(fileProfileData);
            Catalog updatedCatolog = catalogRepository.save(fileParentCatalog);
            if (Objects.isNull(isInsert) || Objects.isNull(updatedCatolog)) {
                throw new Exception("创建文件记录失败");
            }
            return new ResponseResult(200, "文件上传成功！", ShpId);
        } catch (Exception e) {
            unZipFiles.forEach(file -> FileUtils.deleteFile(file));  //删除解压文件
            FileUtils.deleteFile(srcFilePath);  //删除上传的zip
            shpProcessRepository.deletePgTable(tableName);
            return new ResponseResult(201, e.getMessage());
        }
    }

    public ResponseResult delete(DeleteOrImportGeoFileDTO deleteOrImportGeoFileDTO) {

        ChildrenDataDTO childrenDataDTO = new ChildrenDataDTO();
        Catalog tempCatalog = new Catalog();
        GeoDataFile tempGeoDataFile = new GeoDataFile();
        try {
            //1、更改catalog
            Catalog catalog = tempCatalog = catalogRepository.getCatalogById(deleteOrImportGeoFileDTO.getCatalogId());
            List<ChildrenDataDTO> children = catalog.getChildren();
            Iterator<ChildrenDataDTO> iterator = children.iterator();
            while (iterator.hasNext()) {
                ChildrenDataDTO temp = iterator.next();
                if (temp.getId().equals(deleteOrImportGeoFileDTO.getFileId())) {
                    childrenDataDTO = temp;
                    iterator.remove();
                    break;
                }
            }
            catalog.setTotal(catalog.getTotal() - 1);
            catalogRepository.save(catalog);
            //2、删GeoDataFile中的记录
            GeoDataFile destGeoFile = tempGeoDataFile = geoDataFileRepository.findOneById(deleteOrImportGeoFileDTO.getFileId());
            if (Objects.isNull(destGeoFile)) {
                throw new Exception("文件记录不存在！");
            }
            String tableName = destGeoFile.getPtName();
            String filePath = destGeoFile.getPath();
            List<String> unZipFilesPath = destGeoFile.getUnZipFilesPath();
            geoDataFileRepository.deleteById(deleteOrImportGeoFileDTO.getFileId());
            if (!Objects.isNull(geoDataFileRepository.findOneById(deleteOrImportGeoFileDTO.getFileId()))) {
                throw new Exception("文件记录删除失败！");
            }
            //3、删除pg数据
            boolean isDeleted = shpProcessRepository.deletePgTable(tableName);
            if (!isDeleted) {
                throw new Exception("Pg数据表删除失败！");
            }
            //4、删除本地数据，默认成功
            FileUtils.deleteFile(filePath);
            unZipFilesPath.forEach(file -> FileUtils.deleteFile(file));
            return new ResponseResult(200, "文件删除成功！");
        } catch (Exception e) {
            //模拟事物回滚
            switch (e.getMessage()) {
                case "Pg数据表删除失败！":
                    if (!Objects.isNull(tempGeoDataFile)) {
                        geoDataFileRepository.save(tempGeoDataFile);
                    }
                case "文件记录删除失败！":
                case "文件记录不存在！":
                    if (!Objects.isNull(childrenDataDTO) && !Objects.isNull(tempCatalog)) {
                        tempCatalog.getChildren().add(childrenDataDTO);
                        tempCatalog.setTotal(tempCatalog.getTotal() + 1);
                        catalogRepository.save(tempCatalog);
                    }
                    break;
            }
            return new ResponseResult(201, e.getMessage());
        }
    }

    public ResponseResult getList(String userId) {
        List<GeoDataFile> geoDataFiles = geoDataFileRepository.findAllByUserId(userId);
        ArrayList<ReturnGeoDataDTO> returnGeoDataDTOS = new ArrayList<>();
        geoDataFiles.forEach(geoFile -> returnGeoDataDTOS.add(new ReturnGeoDataDTO(geoFile.getId(), geoFile.getDisplayName())));
        return new ResponseResult(200, "返回数据成功！", returnGeoDataDTOS);
    }

    public ResponseEntity<InputStreamResource> downloadFile(String fileId) throws IOException {
        GeoDataFile geoDataFile = geoDataFileRepository.findOneById(fileId);
        geoDataFile.setDownloadNum(geoDataFile.getDownloadNum() + 1);
        geoDataFileRepository.save(geoDataFile);
        String filePath = geoDataFile.getPath();
        FileSystemResource file = new FileSystemResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", new String((geoDataFile.getDisplayName() + ".zip").getBytes("UTF-8"), "ISO-8859-1")));
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(file.contentLength())
                .contentType(MediaType.parseMediaType("application/force-download"))
                .body(new InputStreamResource(file.getInputStream()));
    }

    public ResponseResult getAttrTable(String shpName) {
        List shpAttrInfoFromPG = shpProcessRepository.getShpAttrInfoFromPG(shpName);
        if (shpAttrInfoFromPG.size() != 0) {
            return new ResponseResult(200, "获取成功！", shpAttrInfoFromPG);
        }
        return new ResponseResult(201, "获取失败！");
    }

    public ResponseResult getShpPreviewInfo(String fileId) {
        PreviewShpDTO previewShpDTO = new PreviewShpDTO();
        GeoDataFile geoFile = geoDataFileRepository.findOneById(fileId);
        if (Objects.isNull(geoFile)) {
            return new ResponseResult(201, "未查到该shp");
        }
        previewShpDTO.setName(geoFile.getDisplayName());
        previewShpDTO.setId(geoFile.getId());
        previewShpDTO.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(geoFile.getDate().getTime()));
        previewShpDTO.setSize(geoFile.getSize());
        previewShpDTO.setCode(geoFile.getCode());
        previewShpDTO.setSrid(geoFile.getSrid());
        previewShpDTO.setType(geoFile.getDataType());
        previewShpDTO.setDownloadNum(geoFile.getDownloadNum());
        previewShpDTO.setBbox(geoFile.getBounds());
        previewShpDTO.setPtName(geoFile.getPtName());
        return new ResponseResult(200, "查询成功", previewShpDTO);
    }

    public ResponseResult importSharedGeoResource(DeleteOrImportGeoFileDTO deleteOrImportGeoFileDTO) {
        GeoDataFile geoDataFile = geoDataFileRepository.findOneById(deleteOrImportGeoFileDTO.getFileId());
        File file = new File(geoDataFile.getPath());
        //获取file对象的文件输入流
        FileInputStream input = null;
        try {
            input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile(geoDataFile.getOriginalName(), geoDataFile.getOriginalName(), "zip", input);
            UploadShapefileDTO uploadShapefileDTO = new UploadShapefileDTO();
            uploadShapefileDTO.setFile(multipartFile);
            uploadShapefileDTO.setFileName(geoDataFile.getDisplayName());
            uploadShapefileDTO.setUserId(deleteOrImportGeoFileDTO.getUserId());
            uploadShapefileDTO.setCatalogId(deleteOrImportGeoFileDTO.getCatalogId());
            uploadShapefileDTO.setSrid(geoDataFile.getSrid());
            uploadShapefileDTO.setCode(geoDataFile.getCode());
            this.create(uploadShapefileDTO);
            return new ResponseResult(200, "导入资源成功！");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public ResponseResult importSharedGeoResource(String catalogId, String filePath, String userId) {
        File file = new File(filePath);
        file.getName();
        //获取file对象的文件输入流
        FileInputStream input = null;
        try {
            input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), "zip", input);
            UploadShapefileDTO uploadShapefileDTO = new UploadShapefileDTO();
            uploadShapefileDTO.setFile(multipartFile);
            uploadShapefileDTO.setFileName(file.getName().replace(".zip", ""));
            uploadShapefileDTO.setUserId(userId);
            uploadShapefileDTO.setCatalogId(catalogId);
            uploadShapefileDTO.setSrid(4326);
            uploadShapefileDTO.setCode("UTF-8");
            ResponseResult responseResult = this.create(uploadShapefileDTO);
            return new ResponseResult(200, "导入资源成功！", responseResult.getData().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Double> getCenter(List<Double> bounds) {
        ArrayList<Double> center = new ArrayList<>();
        center.add((bounds.get(0) + bounds.get(2)) / 2);
        center.add((bounds.get(1) + bounds.get(3)) / 2);
        return center;
    }

    public ResponseResult getFields(String geoId) {
        GeoDataFile oneById = geoDataFileRepository.findOneById(geoId);
        List<String> fields = shpProcessRepository.getFields(oneById.getPtName());
        return new ResponseResult(200, "成功", fields);
    }

    public ResponseResult getUniqueValues(String ptName, String field, String method) {
        return shpProcessRepository.getUniqueValues(ptName, field, method);
    }
}
