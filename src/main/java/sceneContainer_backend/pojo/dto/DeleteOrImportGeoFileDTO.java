package sceneContainer_backend.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/3/11 17:20
 */

@Data
@AllArgsConstructor
public class DeleteOrImportGeoFileDTO {

    private String userId;

    private String catalogId;

    private String fileId;
}
