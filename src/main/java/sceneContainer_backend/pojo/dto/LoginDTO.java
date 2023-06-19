package sceneContainer_backend.pojo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/3/8 17:36
 */
@Data
@NoArgsConstructor
public class LoginDTO {
    private String username;
    private String password;
}
