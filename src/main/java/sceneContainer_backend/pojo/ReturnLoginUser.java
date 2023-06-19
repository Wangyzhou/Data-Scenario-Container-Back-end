package sceneContainer_backend.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/3/20 16:02
 */
@Data
public class ReturnLoginUser {
    private String id;
    private String username;
    private byte[] avatar; //头像二进制文件
    private List<String> roles = new ArrayList<>(Arrays.asList("user"));
}
