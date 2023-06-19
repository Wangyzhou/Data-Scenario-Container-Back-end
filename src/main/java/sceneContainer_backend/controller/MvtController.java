package sceneContainer_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import sceneContainer_backend.service.MvtService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/4/7 10:51
 */
@RestController
public class MvtController {

    @Autowired
    private MvtService mvtService;
    @GetMapping(value = "/mvt/{tableName}/{zoom}/{x}/{y}.pbf")
    public void getMvt(@PathVariable("tableName") String tableName,
                       @PathVariable("zoom") int zoom,
                       @PathVariable("x") int x,
                       @PathVariable("y") int y,
                       HttpServletResponse response) throws IOException {

        mvtService.getMvt(zoom, x, y, tableName, response);
    }
}
