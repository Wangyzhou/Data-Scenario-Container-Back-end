package sceneContainer_backend.pojo.dto;

import lombok.Data;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/3/10 18:00
 */

@Data
public class CreateCatalogDTO {

    private String userId;
    private String parentCatalogId;
    private String catalogName;
}
