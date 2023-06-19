package sceneContainer_backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sceneContainer_backend.pojo.PdfFile;

@Repository
public interface PdfFileRepository extends MongoRepository<PdfFile, String> {

    PdfFile findPdfFileByMd5AndUserId(String md5, String userId);

    PdfFile findPdfFileById(String pdfId);

}
