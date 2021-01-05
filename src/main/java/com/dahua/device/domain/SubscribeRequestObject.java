package com.dahua.device.domain;

import lombok.Data;

import java.util.List;

@Data
public class SubscribeRequestObject {

    private SubscribeListObject SubscribeListObject;

    @Data
    public static class SubscribeListObject {
        private List<Subscribe> SubscribeObject;
    }

    @Data
    public static class Subscribe {

        private String SubscribeID;
        private String Title;
        private String SubscribeDetail;
        private String ResourceURI;
        private String ApplicantName;
        private String ApplicantOrg;
        private String BeginTime;
        private String EndTime;
        private String ReceiveAddr;
        private Integer OperateType;
        private Integer SubscribeStatus;
        private String Reason;
        private String SubscribeCancelOrg;
        private String SubscribeCancelPerson;
        private String CancelTime;
        private String CancelReason;
    }

}
