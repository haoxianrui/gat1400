package com.juxingtech.helmet.bean;

import lombok.Data;

import java.util.List;

/**
 * @author haoxr
 * @date 2020-07-03
 **/
@Data
public class MessageResp {


    /**
     * nextMsgId : 232968
     * results : ["{\"method\":\"fcs.faceAlarmEx\",\"id\":1,\"info\":{\"age\":38,\"alarmCode\":\"520000000000022020071712093535988\",\"alarmId\":\"520000000000022020071712093535988\",\"alarmSource\":0,\"alarmTime\":1594958996,\"alarmType\":\"903\",\"beard\":0,\"capTime\":1594959097000,\"channelCode\":\"AH33EDBFA1C7RM49V90F26\",\"channelId\":\"AH33EDBFA1C7RM49V90F26\",\"channelName\":\"交警智能头盔1\",\"dataSource\":0,\"deviceId\":\"AH33EDBFA1C7RM49V81UB0\",\"emotion\":7,\"event\":\"faceAlarmEx\",\"extParam\":\"\",\"eye\":1,\"faceBottom\":100.0,\"faceImgId\":\"515949589964824382\",\"faceImgUrl\":\"http://13.65.33.32:38498/image/efs_AH33EDBF_001/ecd90c1ff2ede3433f5012a0_face_11_3/archivefile1-2020-07-16-131054-F60314E73121410D:465575936/48898.jpg\",\"faceImgUrlEx\":\"/image/efs_AH33EDBF_001/ecd90c1ff2ede3433f5012a0_face_11_3/archivefile1-2020-07-16-131054-F60314E73121410D:465575936/48898.jpg\",\"faceLeft\":100.0,\"faceRecordId\":\"130304211911902010610220200717121137000010600001\",\"faceRight\":100.0,\"faceTop\":100.0,\"fringe\":2,\"gender\":1,\"glasses\":0,\"imgUrl\":\"\",\"imgUrlEx\":\"\",\"mask\":0,\"mouth\":0,\"race\":0,\"recordId\":\"130304211911902010610220200717121137000010600001\",\"similarFaces\":[{\"gender\":1,\"idNumber\":\"130304199003078594\",\"idType\":111,\"name\":\"郝先瑞\",\"repositoryId\":\"1218217277\",\"repositoryName\":\"智慧头盔测试库\",\"similarity\":0.9998998641967773,\"targetFaceImgId\":\"MyNdYt7tjoM11ZELS7SAGtVya9zo7eS9\",\"targetFaceImgUrl\":\"http://13.65.33.32:38498/eagle-pic/download/pic/C5hHpXb4/home/hadoop/picture/static/7571/1594903752099/UserBig_1594903752090_263714.JPG\",\"targetFaceImgUrlEx\":\"/eagle-pic/download/pic/C5hHpXb4/home/hadoop/picture/static/7571/1594903752099/UserBig_1594903752090_263714.JPG\",\"targetImgUrl\":\"http://13.65.33.32:38498/eagle-pic/download/pic/C5hHpXb4/home/hadoop/picture/static/7571/1594903752099/UserBig_1594903752090_263714.JPG\",\"targetImgUrlEx\":\"/eagle-pic/download/pic/C5hHpXb4/home/hadoop/picture/static/7571/1594903752099/UserBig_1594903752090_263714.JPG\"}],\"status\":1,\"uid\":\"13\"}}"]
     * totalCount : 1
     */

    private String nextMsgId;
    private int totalCount;
    private List<String> results;

}
