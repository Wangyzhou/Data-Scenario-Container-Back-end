package sceneContainer_backend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class User  {
    @Id
    private String id;
    private String name;
    private String password;
    private String email;
    private String institution;     // 用户可能了来自不同的机构
    private Date date;
    private Binary avatar; //头像二进制文件
    private List<String> roles = new ArrayList<>(Arrays.asList("user"));
}
