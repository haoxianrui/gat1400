package com.juxingtech.helmet.domain;

import lombok.Data;

import java.util.List;

@Data
public class SubscribeNotificationRequestObject {

    private SubscribeNotificationListObject SubscribeNotificationListObject;

    @Data
    public static class SubscribeNotificationListObject {
        private List<SubscribeNotification> SubscribeNotificationObject;
    }

    @Data
    public static class SubscribeNotification {

        private String NotificationID;
        private String SubscribeID;
        private String Title;
        private String TriggerTime;
        private String InfoIDs;

        private FaceRequestObject.FaceListObject FaceListObject;

        private MotorVehicleRequestObject.MotorVehicleListObject MotorVehicleListObject;

    }

}
