package sceneContainer_backend.service;

import cn.hutool.core.util.IdUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import sceneContainer_backend.domains.ResponseResult;
import sceneContainer_backend.pojo.Catalog;
import sceneContainer_backend.pojo.PdfFile;
import sceneContainer_backend.pojo.dto.ChildrenDataDTO;
import sceneContainer_backend.pojo.dto.DeleteOrImportPdfFileDTO;
import sceneContainer_backend.pojo.dto.UploadPdfDTO;
import sceneContainer_backend.repository.MogoDBRepository.CatalogRepository;
import sceneContainer_backend.repository.PdfFileRepository;
import sceneContainer_backend.utils.FileUtils;

import java.io.*;
import java.util.*;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/4/12 21:08
 */
@Service
public class PdfFileService {

    @Value("${resourcesPath}")
    private String resourcesRoot;

    @Autowired
    private PdfFileRepository pdfFileRepository;

    @Autowired
    private CatalogRepository catalogRepository;

    public ResponseResult upload(UploadPdfDTO uploadPdfDTO) {
        String md5;
        String path = "";
        Catalog temp = new Catalog();
        String PdfId = "";
        try {
            //1、存入本地
            md5 = DigestUtils.md5DigestAsHex(uploadPdfDTO.getFile().getInputStream());
            PdfFile oneByMd5AndUserId = pdfFileRepository.findPdfFileByMd5AndUserId(md5, uploadPdfDTO.getUserId());
            if (!Objects.isNull(oneByMd5AndUserId)) {
                return new ResponseResult(201, "此文件已在你的仓库！不可重复上传");
            }
            String originalFilename = uploadPdfDTO.getFile().getOriginalFilename();
            String currentCatalogId = uploadPdfDTO.getCatalogId();
            String currentParentId = "";
            String catalogPath = "";
            LinkedList<String> catalogIdList = new LinkedList<>();
            catalogIdList.addFirst(uploadPdfDTO.getCatalogId());
            while ((currentParentId = catalogRepository.getCatalogById(currentCatalogId).getParentId()).compareTo("-1") != 0) {
                currentCatalogId = currentParentId;
                catalogIdList.addFirst(currentCatalogId);
            }
            Iterator<String> it = catalogIdList.iterator();
            while (it.hasNext()) {
                catalogPath = catalogPath + "/" + it.next();
            }
            String uploadPath = resourcesRoot + "/" + uploadPdfDTO.getUserId() + catalogPath;
            File pdfFilePath = new File(uploadPath);
            if (!pdfFilePath.exists()) {
                pdfFilePath.mkdirs();
            }
            boolean isUploaded = FileUtils.uploadSingleFile(uploadPdfDTO.getFile(), uploadPath, originalFilename);
            if (!isUploaded) {
                throw new Exception("创建本地文件失败！");
            }
            //2、创建文件记录
            PdfId = IdUtil.objectId();
            int size = (int) uploadPdfDTO.getFile().getSize();
            String userId = uploadPdfDTO.getUserId();
            String fileType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            path = uploadPath + "/" + originalFilename;
            Date date = new Date();
            HashMap<String, String> nameList = new HashMap<>();
            nameList.put(uploadPdfDTO.getCatalogId(), originalFilename);
            PdfFile insertPdf = pdfFileRepository.insert(new PdfFile(PdfId, md5, nameList, userId, originalFilename, uploadPdfDTO.getFileName(), size, path, 0, date));
            if (Objects.isNull(insertPdf)) {
                throw new Exception("pdf入库失败！");
            }
            //3、添加catalog记录
            Catalog catalogById = temp = catalogRepository.getCatalogById(uploadPdfDTO.getCatalogId());
            catalogById.setTotal(catalogById.getTotal() + 1);
            catalogById.getChildren().add(new ChildrenDataDTO(PdfId, uploadPdfDTO.getFileName(), fileType, fileType, date));
            Catalog isSave = catalogRepository.save(catalogById);
            if (Objects.isNull(isSave)) {
                throw new Exception("目录添加记录失败！");
            }
            return new ResponseResult(200, "上传成功！");
        } catch (Exception e) {
            switch (e.getMessage()) {
                case "目录添加记录失败！":
                    catalogRepository.save(temp);
                case "pdf入库失败！":
                    if (!Objects.isNull(pdfFileRepository.findById(PdfId))) {
                        pdfFileRepository.deleteById(PdfId);
                    }
                case "创建本地文件失败！":
                    FileUtils.deleteFile(path);
                    break;
            }
            return new ResponseResult(201, "上传失败！");
        }
    }

    public ResponseResult delete(DeleteOrImportPdfFileDTO deleteOrImportPdfFileDTO) {
        //1、删Catalog 2、删文件  3、删本地文件（默认成功）
        Catalog tempCatalog = new Catalog();
        PdfFile tempPdfFile = new PdfFile();

        try {
            Catalog catalog = tempCatalog = catalogRepository.getCatalogById(deleteOrImportPdfFileDTO.getCatalogId());
            List<ChildrenDataDTO> children = catalog.getChildren();
            Iterator<ChildrenDataDTO> iterator = children.iterator();
            while (iterator.hasNext()) {
                ChildrenDataDTO temp = iterator.next();
                if (temp.getId().equals(deleteOrImportPdfFileDTO.getId())) {
                    iterator.remove();
                    break;
                }
            }
            catalog.setTotal(catalog.getTotal() - 1);
            Catalog isSaveCatalog = catalogRepository.save(catalog);
            if (Objects.isNull(isSaveCatalog)) {
                throw new Exception("catalog更改失败！");
            }
            PdfFile destPdfFile = tempPdfFile = pdfFileRepository.findPdfFileById(deleteOrImportPdfFileDTO.getId());
            if (Objects.isNull(destPdfFile)) {
                throw new Exception("文件记录不存在！");
            }
            pdfFileRepository.deleteById(deleteOrImportPdfFileDTO.getId());
            if (!Objects.isNull(pdfFileRepository.findPdfFileById(deleteOrImportPdfFileDTO.getId()))) {
                throw new Exception("文件记录删除失败！");
            }
            boolean isDelete = FileUtils.deleteFile(destPdfFile.getPath());
            if (!isDelete) {
                throw new Exception("本地文件删除失败！");
            }
            return new ResponseResult(200, "删除成功！");
        } catch (Exception e) {
            switch (e.getMessage()) {
                case "本地文件删除失败！":

                case "文件记录删除失败！":
                    pdfFileRepository.save(tempPdfFile);
                case "文件记录不存在！":
                case "catalog更改失败！":
                    catalogRepository.save(tempCatalog);
                    break;
                default:
                    break;
            }
            return new ResponseResult(201, "删除失败！");
        }
    }

    public ResponseEntity<InputStreamResource> downloadFile(String fileId) throws IOException {
        PdfFile pdfFileById = pdfFileRepository.findPdfFileById(fileId);
        pdfFileById.setDownloadNum(pdfFileById.getDownloadNum() + 1);
        pdfFileRepository.save(pdfFileById);
        String filePath = pdfFileById.getPath();
        FileSystemResource file = new FileSystemResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", new String((pdfFileById.getDisplayName() + ".pdf").getBytes("UTF-8"), "ISO-8859-1")));
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(file.contentLength())
                .contentType(MediaType.parseMediaType("application/force-download"))
                .body(new InputStreamResource(file.getInputStream()));
    }

    public ResponseEntity<byte[]> getPdfFile(String fileId) {
        PdfFile pdfFile = pdfFileRepository.findPdfFileById(fileId);
        String pdfPath = pdfFile.getPath();
        InputStream is = null;
        try {
            is = new FileInputStream(pdfPath);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) {
                bos.write(buf, 0, len);
            }
            byte[] pdfBytes = bos.toByteArray();

            // 设置响应头Content-Type
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);

            // 返回响应体（字节数组）
            return new ResponseEntity<byte[]>(pdfBytes, headers, HttpStatus.OK);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }

    public ResponseResult importSharedPdfFile(DeleteOrImportPdfFileDTO deleteOrImportPdfFileDTO) {
        //创建file对象
        PdfFile pdfFile = pdfFileRepository.findPdfFileById(deleteOrImportPdfFileDTO.getId());
        File file = new File(pdfFile.getPath());
        //获取file对象的文件输入流
        FileInputStream input = null;
        try {
            input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile(pdfFile.getOriginalName(), pdfFile.getOriginalName(), "pdf", input);
            UploadPdfDTO uploadPdfDTO = new UploadPdfDTO();
            uploadPdfDTO.setFile(multipartFile);
            uploadPdfDTO.setFileName(pdfFile.getDisplayName());
            uploadPdfDTO.setUserId(deleteOrImportPdfFileDTO.getUserId());
            uploadPdfDTO.setCatalogId(deleteOrImportPdfFileDTO.getCatalogId());
            this.upload(uploadPdfDTO);
            return new ResponseResult(200, "导入资源成功！");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
