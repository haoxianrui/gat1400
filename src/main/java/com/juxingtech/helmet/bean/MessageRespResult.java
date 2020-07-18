package com.juxingtech.helmet.bean;

import java.util.List;

public class MessageRespResult {

    /**
     * method : fcs.faceAlarmEx
     * id : 1
     * info : {"age":38,"alarmCode":"520000000000022020071712093535988","alarmId":"520000000000022020071712093535988","alarmSource":0,"alarmTime":1594958996,"alarmType":"903","beard":0,"capTime":1594959097000,"channelCode":"AH33EDBFA1C7RM49V90F26","channelId":"AH33EDBFA1C7RM49V90F26","channelName":"交警智能头盔1","dataSource":0,"deviceId":"AH33EDBFA1C7RM49V81UB0","emotion":7,"event":"faceAlarmEx","extParam":"","eye":1,"faceBottom":100,"faceImgId":"515949589964824382","faceImgUrl":"http://13.65.33.32:38498/image/efs_AH33EDBF_001/ecd90c1ff2ede3433f5012a0_face_11_3/archivefile1-2020-07-16-131054-F60314E73121410D:465575936/48898.jpg","faceImgUrlEx":"/image/efs_AH33EDBF_001/ecd90c1ff2ede3433f5012a0_face_11_3/archivefile1-2020-07-16-131054-F60314E73121410D:465575936/48898.jpg","faceLeft":100,"faceRecordId":"130304211911902010610220200717121137000010600001","faceRight":100,"faceTop":100,"fringe":2,"gender":1,"glasses":0,"imgUrl":"","imgUrlEx":"","mask":0,"mouth":0,"race":0,"recordId":"130304211911902010610220200717121137000010600001","similarFaces":[{"gender":1,"idNumber":"130304199003078594","idType":111,"name":"郝先瑞","repositoryId":"1218217277","repositoryName":"智慧头盔测试库","similarity":0.9998998641967773,"targetFaceImgId":"MyNdYt7tjoM11ZELS7SAGtVya9zo7eS9","targetFaceImgUrl":"http://13.65.33.32:38498/eagle-pic/download/pic/C5hHpXb4/home/hadoop/picture/static/7571/1594903752099/UserBig_1594903752090_263714.JPG","targetFaceImgUrlEx":"/eagle-pic/download/pic/C5hHpXb4/home/hadoop/picture/static/7571/1594903752099/UserBig_1594903752090_263714.JPG","targetImgUrl":"http://13.65.33.32:38498/eagle-pic/download/pic/C5hHpXb4/home/hadoop/picture/static/7571/1594903752099/UserBig_1594903752090_263714.JPG","targetImgUrlEx":"/eagle-pic/download/pic/C5hHpXb4/home/hadoop/picture/static/7571/1594903752099/UserBig_1594903752090_263714.JPG"}],"status":1,"uid":"13"}
     */

    private String method;
    private int id;
    private InfoBean info;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    public static class InfoBean {
        /**
         * age : 38
         * alarmCode : 520000000000022020071712093535988
         * alarmId : 520000000000022020071712093535988
         * alarmSource : 0
         * alarmTime : 1594958996
         * alarmType : 903
         * beard : 0
         * capTime : 1594959097000
         * channelCode : AH33EDBFA1C7RM49V90F26
         * channelId : AH33EDBFA1C7RM49V90F26
         * channelName : 交警智能头盔1
         * dataSource : 0
         * deviceId : AH33EDBFA1C7RM49V81UB0
         * emotion : 7
         * event : faceAlarmEx
         * extParam :
         * eye : 1
         * faceBottom : 100.0
         * faceImgId : 515949589964824382
         * faceImgUrl : http://13.65.33.32:38498/image/efs_AH33EDBF_001/ecd90c1ff2ede3433f5012a0_face_11_3/archivefile1-2020-07-16-131054-F60314E73121410D:465575936/48898.jpg
         * faceImgUrlEx : /image/efs_AH33EDBF_001/ecd90c1ff2ede3433f5012a0_face_11_3/archivefile1-2020-07-16-131054-F60314E73121410D:465575936/48898.jpg
         * faceLeft : 100.0
         * faceRecordId : 130304211911902010610220200717121137000010600001
         * faceRight : 100.0
         * faceTop : 100.0
         * fringe : 2
         * gender : 1
         * glasses : 0
         * imgUrl :
         * imgUrlEx :
         * mask : 0
         * mouth : 0
         * race : 0
         * recordId : 130304211911902010610220200717121137000010600001
         * similarFaces : [{"gender":1,"idNumber":"130304199003078594","idType":111,"name":"郝先瑞","repositoryId":"1218217277","repositoryName":"智慧头盔测试库","similarity":0.9998998641967773,"targetFaceImgId":"MyNdYt7tjoM11ZELS7SAGtVya9zo7eS9","targetFaceImgUrl":"http://13.65.33.32:38498/eagle-pic/download/pic/C5hHpXb4/home/hadoop/picture/static/7571/1594903752099/UserBig_1594903752090_263714.JPG","targetFaceImgUrlEx":"/eagle-pic/download/pic/C5hHpXb4/home/hadoop/picture/static/7571/1594903752099/UserBig_1594903752090_263714.JPG","targetImgUrl":"http://13.65.33.32:38498/eagle-pic/download/pic/C5hHpXb4/home/hadoop/picture/static/7571/1594903752099/UserBig_1594903752090_263714.JPG","targetImgUrlEx":"/eagle-pic/download/pic/C5hHpXb4/home/hadoop/picture/static/7571/1594903752099/UserBig_1594903752090_263714.JPG"}]
         * status : 1
         * uid : 13
         */

        private int age;
        private String alarmCode;
        private String alarmId;
        private int alarmSource;
        private int alarmTime;
        private String alarmType;
        private int beard;
        private long capTime;
        private String channelCode;
        private String channelId;
        private String channelName;
        private int dataSource;
        private String deviceId;
        private int emotion;
        private String event;
        private String extParam;
        private int eye;
        private double faceBottom;
        private String faceImgId;
        private String faceImgUrl;
        private String faceImgUrlEx;
        private double faceLeft;
        private String faceRecordId;
        private double faceRight;
        private double faceTop;
        private int fringe;
        private int gender;
        private int glasses;
        private String imgUrl;
        private String imgUrlEx;
        private int mask;
        private int mouth;
        private int race;
        private String recordId;
        private int status;
        private String uid;
        private List<SimilarFacesBean> similarFaces;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getAlarmCode() {
            return alarmCode;
        }

        public void setAlarmCode(String alarmCode) {
            this.alarmCode = alarmCode;
        }

        public String getAlarmId() {
            return alarmId;
        }

        public void setAlarmId(String alarmId) {
            this.alarmId = alarmId;
        }

        public int getAlarmSource() {
            return alarmSource;
        }

        public void setAlarmSource(int alarmSource) {
            this.alarmSource = alarmSource;
        }

        public int getAlarmTime() {
            return alarmTime;
        }

        public void setAlarmTime(int alarmTime) {
            this.alarmTime = alarmTime;
        }

        public String getAlarmType() {
            return alarmType;
        }

        public void setAlarmType(String alarmType) {
            this.alarmType = alarmType;
        }

        public int getBeard() {
            return beard;
        }

        public void setBeard(int beard) {
            this.beard = beard;
        }

        public long getCapTime() {
            return capTime;
        }

        public void setCapTime(long capTime) {
            this.capTime = capTime;
        }

        public String getChannelCode() {
            return channelCode;
        }

        public void setChannelCode(String channelCode) {
            this.channelCode = channelCode;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getChannelName() {
            return channelName;
        }

        public void setChannelName(String channelName) {
            this.channelName = channelName;
        }

        public int getDataSource() {
            return dataSource;
        }

        public void setDataSource(int dataSource) {
            this.dataSource = dataSource;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public int getEmotion() {
            return emotion;
        }

        public void setEmotion(int emotion) {
            this.emotion = emotion;
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public String getExtParam() {
            return extParam;
        }

        public void setExtParam(String extParam) {
            this.extParam = extParam;
        }

        public int getEye() {
            return eye;
        }

        public void setEye(int eye) {
            this.eye = eye;
        }

        public double getFaceBottom() {
            return faceBottom;
        }

        public void setFaceBottom(double faceBottom) {
            this.faceBottom = faceBottom;
        }

        public String getFaceImgId() {
            return faceImgId;
        }

        public void setFaceImgId(String faceImgId) {
            this.faceImgId = faceImgId;
        }

        public String getFaceImgUrl() {
            return faceImgUrl;
        }

        public void setFaceImgUrl(String faceImgUrl) {
            this.faceImgUrl = faceImgUrl;
        }

        public String getFaceImgUrlEx() {
            return faceImgUrlEx;
        }

        public void setFaceImgUrlEx(String faceImgUrlEx) {
            this.faceImgUrlEx = faceImgUrlEx;
        }

        public double getFaceLeft() {
            return faceLeft;
        }

        public void setFaceLeft(double faceLeft) {
            this.faceLeft = faceLeft;
        }

        public String getFaceRecordId() {
            return faceRecordId;
        }

        public void setFaceRecordId(String faceRecordId) {
            this.faceRecordId = faceRecordId;
        }

        public double getFaceRight() {
            return faceRight;
        }

        public void setFaceRight(double faceRight) {
            this.faceRight = faceRight;
        }

        public double getFaceTop() {
            return faceTop;
        }

        public void setFaceTop(double faceTop) {
            this.faceTop = faceTop;
        }

        public int getFringe() {
            return fringe;
        }

        public void setFringe(int fringe) {
            this.fringe = fringe;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public int getGlasses() {
            return glasses;
        }

        public void setGlasses(int glasses) {
            this.glasses = glasses;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getImgUrlEx() {
            return imgUrlEx;
        }

        public void setImgUrlEx(String imgUrlEx) {
            this.imgUrlEx = imgUrlEx;
        }

        public int getMask() {
            return mask;
        }

        public void setMask(int mask) {
            this.mask = mask;
        }

        public int getMouth() {
            return mouth;
        }

        public void setMouth(int mouth) {
            this.mouth = mouth;
        }

        public int getRace() {
            return race;
        }

        public void setRace(int race) {
            this.race = race;
        }

        public String getRecordId() {
            return recordId;
        }

        public void setRecordId(String recordId) {
            this.recordId = recordId;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public List<SimilarFacesBean> getSimilarFaces() {
            return similarFaces;
        }

        public void setSimilarFaces(List<SimilarFacesBean> similarFaces) {
            this.similarFaces = similarFaces;
        }

        public static class SimilarFacesBean {
            /**
             * gender : 1
             * idNumber : 130304199003078594
             * idType : 111
             * name : 郝先瑞
             * repositoryId : 1218217277
             * repositoryName : 智慧头盔测试库
             * similarity : 0.9998998641967773
             * targetFaceImgId : MyNdYt7tjoM11ZELS7SAGtVya9zo7eS9
             * targetFaceImgUrl : http://13.65.33.32:38498/eagle-pic/download/pic/C5hHpXb4/home/hadoop/picture/static/7571/1594903752099/UserBig_1594903752090_263714.JPG
             * targetFaceImgUrlEx : /eagle-pic/download/pic/C5hHpXb4/home/hadoop/picture/static/7571/1594903752099/UserBig_1594903752090_263714.JPG
             * targetImgUrl : http://13.65.33.32:38498/eagle-pic/download/pic/C5hHpXb4/home/hadoop/picture/static/7571/1594903752099/UserBig_1594903752090_263714.JPG
             * targetImgUrlEx : /eagle-pic/download/pic/C5hHpXb4/home/hadoop/picture/static/7571/1594903752099/UserBig_1594903752090_263714.JPG
             */

            private int gender;
            private String idNumber;
            private int idType;
            private String name;
            private String repositoryId;
            private String repositoryName;
            private double similarity;
            private String targetFaceImgId;
            private String targetFaceImgUrl;
            private String targetFaceImgUrlEx;
            private String targetImgUrl;
            private String targetImgUrlEx;

            public int getGender() {
                return gender;
            }

            public void setGender(int gender) {
                this.gender = gender;
            }

            public String getIdNumber() {
                return idNumber;
            }

            public void setIdNumber(String idNumber) {
                this.idNumber = idNumber;
            }

            public int getIdType() {
                return idType;
            }

            public void setIdType(int idType) {
                this.idType = idType;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getRepositoryId() {
                return repositoryId;
            }

            public void setRepositoryId(String repositoryId) {
                this.repositoryId = repositoryId;
            }

            public String getRepositoryName() {
                return repositoryName;
            }

            public void setRepositoryName(String repositoryName) {
                this.repositoryName = repositoryName;
            }

            public double getSimilarity() {
                return similarity;
            }

            public void setSimilarity(double similarity) {
                this.similarity = similarity;
            }

            public String getTargetFaceImgId() {
                return targetFaceImgId;
            }

            public void setTargetFaceImgId(String targetFaceImgId) {
                this.targetFaceImgId = targetFaceImgId;
            }

            public String getTargetFaceImgUrl() {
                return targetFaceImgUrl;
            }

            public void setTargetFaceImgUrl(String targetFaceImgUrl) {
                this.targetFaceImgUrl = targetFaceImgUrl;
            }

            public String getTargetFaceImgUrlEx() {
                return targetFaceImgUrlEx;
            }

            public void setTargetFaceImgUrlEx(String targetFaceImgUrlEx) {
                this.targetFaceImgUrlEx = targetFaceImgUrlEx;
            }

            public String getTargetImgUrl() {
                return targetImgUrl;
            }

            public void setTargetImgUrl(String targetImgUrl) {
                this.targetImgUrl = targetImgUrl;
            }

            public String getTargetImgUrlEx() {
                return targetImgUrlEx;
            }

            public void setTargetImgUrlEx(String targetImgUrlEx) {
                this.targetImgUrlEx = targetImgUrlEx;
            }
        }
    }
}
