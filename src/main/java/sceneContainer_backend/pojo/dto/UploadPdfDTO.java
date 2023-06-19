package sceneContainer_backend.pojo.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/4/12 20:54
 */
@Data
public class UploadPdfDTO {
    private MultipartFile file;
    private String fileName;
    private String userId;
    private String catalogId;
}
