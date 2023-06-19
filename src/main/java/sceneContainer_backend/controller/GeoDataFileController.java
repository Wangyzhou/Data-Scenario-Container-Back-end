package sceneContainer_backend.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sceneContainer_backend.domains.ResponseResult;
import sceneContainer_backend.pojo.dto.DeleteOrImportGeoFileDTO;
import sceneContainer_backend.pojo.dto.UploadShapefileDTO;
import sceneContainer_backend.service.GeoDataFileService;

import java.io.IOException;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/3/10 20:26
 */

@RestController
@RequestMapping("/geofile")
public class GeoDataFileController {


    @Autowired
    private GeoDataFileService geoDataFileService;

    @ApiOperation("上传文件")
    @PostMapping
    public ResponseResult uploadGeoDataFile(UploadShapefileDTO uploadShapefileDTO) {
//        UploadShapefileDTO uploadShapefileDTO = new UploadShapefileDTO(multipartFile, fileName, srid, code, userId, catalogId);
        return geoDataFileService.create(uploadShapefileDTO);
    }

    @ApiOperation("删除文件")
    @DeleteMapping
    public ResponseResult deleteGeoDataFile(@RequestParam("userId") String userId,
                                            @RequestParam("catalogId") String catalogId,
                                            @RequestParam("fileId") String fileId) {
        DeleteOrImportGeoFileDTO deleteOrImportGeoFileDTO = new DeleteOrImportGeoFileDTO(userId, catalogId, fileId);
        return geoDataFileService.delete(deleteOrImportGeoFileDTO);
    }

    @GetMapping
    public ResponseResult getFilesInfoInCatolog(@RequestParam("userId") String userId) {
        return geoDataFileService.getList(userId);
    }

    @ApiOperation("下载文件")
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam("fileId") String fileId) throws IOException {
        return geoDataFileService.downloadFile(fileId);
    }

    @ApiOperation("属性表")
    @GetMapping("/getAtrr")
    public ResponseResult getAtrrTable(@RequestParam("shpName") String shpName) {
        return geoDataFileService.getAttrTable(shpName);
    }

    @ApiOperation("字段")
    @GetMapping("/getFields")
    public ResponseResult getFields(@RequestParam("geoId") String geoId) {
        return geoDataFileService.getFields(geoId);
    }

    @ApiOperation("预览信息")
    @GetMapping("/getShpPreview")
    public ResponseResult getShpPreviewInfo(@RequestParam("fileId") String fileId) {
        return geoDataFileService.getShpPreviewInfo(fileId);
    }

    @ApiOperation("导入资源")
    @PostMapping("/importShpResource")
    public ResponseResult importShpResource(@RequestBody DeleteOrImportGeoFileDTO deleteOrImportGeoFileDTO) {
        return geoDataFileService.importSharedGeoResource(deleteOrImportGeoFileDTO);
    }

    @ApiOperation("获取字段唯一值数组")
    @GetMapping("/getUniqueValues")
    public ResponseResult getUniqueValues(@RequestParam("ptName") String ptName,
                                          @RequestParam("field") String field,
                                          @RequestParam("method") String method) {
        return geoDataFileService.getUniqueValues(ptName, field, method);
    }
}
