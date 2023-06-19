package sceneContainer_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sceneContainer_backend.domains.ResponseResult;
import sceneContainer_backend.pojo.dto.DeleteOrImportPdfFileDTO;
import sceneContainer_backend.pojo.dto.UploadPdfDTO;
import sceneContainer_backend.service.PdfFileService;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/4/12 20:51
 */
@RestController
@RequestMapping("/pdffile")
public class PdfFileController {

    @Autowired
    private PdfFileService pdfFileService;

    @PostMapping
    public ResponseResult upload(UploadPdfDTO uploadPdfDTO) {
        return pdfFileService.upload(uploadPdfDTO);
    }

    @DeleteMapping
    public ResponseResult delete(DeleteOrImportPdfFileDTO deleteOrImportPdfFileDTO) {
        return pdfFileService.delete(deleteOrImportPdfFileDTO);
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadPdf(@RequestParam("fileId") String fileId) throws IOException {
        return pdfFileService.downloadFile(fileId);
    }

    @GetMapping("/getPdfFile")
    public ResponseEntity<byte[]> getPdfFile(@RequestParam("fileId") String fileId) throws FileNotFoundException {
        return pdfFileService.getPdfFile(fileId);
    }
    @PostMapping("/importSharedPdfFile")
    public ResponseResult importSharedPdfFile(@RequestBody DeleteOrImportPdfFileDTO deleteOrImportPdfFileDTO) {
        return pdfFileService.importSharedPdfFile(deleteOrImportPdfFileDTO);
    }

}
