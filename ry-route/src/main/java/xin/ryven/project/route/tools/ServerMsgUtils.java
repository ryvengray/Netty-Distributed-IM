package xin.ryven.project.route.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import xin.ryven.project.common.enums.Status;
import xin.ryven.project.common.http.Resp;
import xin.ryven.project.common.vo.MsgVo;
import xin.ryven.project.common.vo.ServerAddress;
import xin.ryven.project.route.config.ApplicationProperties;

/**
 * @author gray
 */
@Component
@Slf4j
public class ServerMsgUtils {

    private final RestTemplate restTemplate;
    private final ApplicationProperties properties;

    @Autowired
    public ServerMsgUtils(RestTemplate restTemplate, ApplicationProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public Resp sendMsg(ServerAddress serverAddress, MsgVo msgVo) {
        //组装参数
        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
        postParameters.add("userId", msgVo.getUserId().toString());
        postParameters.add("username", msgVo.getUsername());
        postParameters.add("toUserId", msgVo.getToUserId().toString());
        postParameters.add("toUsername", msgVo.getToUsername());
        postParameters.add("content", msgVo.getContent());
        postParameters.add("type", msgVo.getType().toString());

        //构建header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        //请求
        HttpEntity<MultiValueMap> httpEntity = new HttpEntity<>(postParameters, headers);
        String url = "http://" + serverAddress.getLocaleHost() + ":" + serverAddress.getHttpPort() + properties.getSendMsgUrl();
        try {
            ResponseEntity<Resp> respResponseEntity = restTemplate.postForEntity(url, httpEntity, Resp.class);
            return respResponseEntity.getBody();
        } catch (Exception e) {
            log.error("Do http request error, url = {}", url, e);
        }
        return Resp.status(Status.SERVER_BREAK);
    }

}
