package sceneContainer_backend.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/3/22 16:06
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadShapefileDTO {
    private MultipartFile file;
    private String fileName;
    private Integer srid;
    private String code;
    private String userId;
    private String catalogId;
}
