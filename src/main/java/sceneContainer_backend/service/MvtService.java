package sceneContainer_backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sceneContainer_backend.repository.PostgresRepository.MvtRepository;
import sceneContainer_backend.utils.MvtUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * @description:
 * @author: wyjq
 * @time: 2022/3/10
 */
@Slf4j
@Service
public class MvtService {

    @Autowired
    private MvtRepository mvtRepository;
    public void getMvt(int zoom, int x, int y, String tableName, HttpServletResponse response) {
        try {
            String sql= getMvtSql(zoom,x,y,tableName);
            if (sql==null){
                return;
            }
            log.info("DefaultPgSource: "+zoom + ", " + x + ", " + y +":" + sql);

            byte[] mvtByte=mvtRepository.getMvtFromDefaultPg(sql);
            returnMvtByte(mvtByte,zoom,x,y,response);
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    public String getMvtSql(int zoom, int x, int y,String tableName){
        if (!MvtUtils.tileIsValid(zoom, x, y)) {
            return null;
        }
        HashMap<String, Double> envelope = MvtUtils.tileToEnvelope(zoom, x, y);
        String sql = MvtUtils.envelopeToSQL(envelope, tableName);
        return sql;
    }

    public void returnMvtByte(byte[] mvtByte,int zoom, int x, int y,HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Content-type", "application/vnd.mapbox-vector-tile");
        String mtvFileName = String.format("%d_%d_%d.mvt", zoom, x, y);
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(mtvFileName.getBytes("UTF-8"), "iso-8859-1"));
        OutputStream os = response.getOutputStream();
        os.write(mvtByte);
    }
}
