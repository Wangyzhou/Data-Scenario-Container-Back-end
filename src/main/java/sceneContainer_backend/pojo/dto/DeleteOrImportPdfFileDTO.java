package sceneContainer_backend.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/4/13 16:30
 */
@Data
@AllArgsConstructor
public class DeleteOrImportPdfFileDTO {

    private String id;

    private String userId;

    private String catalogId;
}
