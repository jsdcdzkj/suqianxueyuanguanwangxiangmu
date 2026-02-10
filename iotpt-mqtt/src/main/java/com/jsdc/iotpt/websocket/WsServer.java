package com.jsdc.iotpt.websocket;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * ClassName: WsServer
 * Description:
 * date: 2023/5/26 15:41
 *
 * @author bn
 */
@Component
@ServerEndpoint(value = "/websocket/{clientId}")
public class WsServer {


    private static final Logger log = LoggerFactory.getLogger("WsServer");

    private Session session;

    private String clientId;

    private WsClientPool wsClientPool = WsClientPool.getInstance();




    @OnOpen
    public void onOpen(@PathParam("clientId") String clientId, Session session)
    {
        log.info("WebSocket onOpen, clientId={}", clientId);

        this.session = session;
        if (clientId.contains("deviceLift")){
            this.clientId =clientId;
        }else{
            this.clientId = new String(Base64Utils.decodeFromString(clientId));
        }
        if (clientId.equals("jscLift")){
            this.clientId =clientId;
        }
        wsClientPool.addOnlineClient(this.clientId, session);
        wsClientPool.setClientDevice(this.clientId, this.clientId);
    }

    @OnMessage
    public void onMessage(String deviceId)
    {

        log.info("onMessage deviceId={}, clientId={}", deviceId, clientId);
        if (StringUtils.isNotEmpty(clientId) && StringUtils.isNotEmpty(deviceId))
        {
            if (wsClientPool.getOnlineClientMap().containsKey(clientId))
            {
                // 更新该client需要接收的deviceId
                wsClientPool.getClientDeviceMap().put(clientId, deviceId);
            }
            else
            {
                log.warn("Client offline, clientId={}", clientId);
            }
        }
        try
        {
            session.getBasicRemote().sendText("received message:" + clientId + "_" + deviceId);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason)
    {
        log.info("WebSocket onClose, clientId={}", clientId);
        wsClientPool.delOfflineClient(clientId);
    }


    @OnError
    public void onError(Throwable t) throws Throwable
    {
    }
}
