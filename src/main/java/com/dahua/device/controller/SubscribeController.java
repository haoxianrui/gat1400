package com.dahua.device.controller;

import cn.hutool.json.JSONUtil;
import com.dahua.device.domain.SubscribeReq;
import com.dahua.device.domain.SubscribeRequestObject;
import com.dahua.device.common.result.Result;
import com.juxingtech.helmet.domain.*;
import com.dahua.device.domain.SubscribeNotificationRequestObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "GAT/1400 订阅接口")
@RestController
@Slf4j
public class SubscribeController {

    @Autowired
    private RestTemplate restTemplate;

    String subscribeUrl;

    public SubscribeController(
            @Value("${server1400.ip}") String ip,
            @Value("${server1400.port}") String port
    ) {
        subscribeUrl = "http://" + ip + ":" + port + "/VIID/Subscribes";
    }


    @PostMapping
    @ApiOperation(value = "订阅", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "subscribeReq", value = "实体JSON对象", required = true, paramType = "body", dataType = "SubscribeReq")

    })
    @RequestMapping("/subscribes")
    public Result subscribe(
            @RequestBody SubscribeReq subscribeReq
    ) {

        // 订阅统一标识码 = 公安机关机构代码 + 子类型编码（03-订阅）+ 时间编码（YYYYMMDDhhmmss） + 流水序号（00001）

        String time = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());

        String subscribeID = "123456789012" + "03" + time + "00001";

        String title = "订阅人脸识别、车牌识别消息";

        String subscribeDetail = "12,13"; // 订阅类别: 12-人脸信息 13-车辆信息

        String resourceURI = subscribeReq.getDeviceId(); // 订阅资源路径

        String applicantName = "xxx"; // 申请人

        String applicantOrg = "xxx公司"; // 申请单位

        String beginTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());

        String endTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now().plusMinutes(5l));

        String receiveAddr = "http://localhost:8888/receive";

        Integer operateType = 0;

        SubscribeRequestObject subscribeRequestObject = new SubscribeRequestObject();
        SubscribeRequestObject.SubscribeListObject subscribeListObject = new SubscribeRequestObject.SubscribeListObject();
        subscribeRequestObject.setSubscribeListObject(subscribeListObject);


        List<SubscribeRequestObject.Subscribe> subscribeList = new ArrayList<>();

        SubscribeRequestObject.Subscribe subscribe = new SubscribeRequestObject.Subscribe();
        subscribe.setSubscribeID(subscribeID);
        subscribe.setTitle(title);
        subscribe.setSubscribeDetail(subscribeDetail);
        subscribe.setResourceURI(resourceURI);
        subscribe.setApplicantName(applicantName);
        subscribe.setApplicantOrg(applicantOrg);
        subscribe.setBeginTime(beginTime);
        subscribe.setEndTime(endTime);
        subscribe.setReceiveAddr(receiveAddr);
        subscribe.setOperateType(operateType);
        subscribeList.add(subscribe);
        subscribeListObject.setSubscribeObject(subscribeList);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json;charset=utf-8"));
        headers.set("User-Identify", subscribeReq.getDeviceId());
        HttpEntity<String> httpEntity = new HttpEntity<>(JSONUtil.toJsonStr(subscribeRequestObject), headers);
        log.info("上传人脸消息体：{}", JSONUtil.toJsonStr(subscribeRequestObject));
        // 请求执行
        ResponseEntity<String> responseEntity = restTemplate.exchange(subscribeUrl, HttpMethod.POST, httpEntity, String.class);
        if (org.apache.http.HttpStatus.SC_OK == responseEntity.getStatusCode().value()) {
            return Result.success();
        }
        return Result.failed("订阅失败");
    }


    @PostMapping("/receive")
    public Result receive(
            @RequestBody SubscribeNotificationRequestObject subscribeNotificationRequestObject
    ) {
        return Result.success();
    }
}
