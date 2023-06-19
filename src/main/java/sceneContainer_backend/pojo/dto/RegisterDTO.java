package sceneContainer_backend.pojo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/3/20 15:02
 */
@Data
@NoArgsConstructor
public class RegisterDTO {
    private String name;
    private String password;
    private String email;
    private String institution;     // 用户可能了来自不同的机构
    private MultipartFile avatarFile; //头像二进制文件
}
