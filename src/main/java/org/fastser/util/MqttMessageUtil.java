package org.fastser.util;


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class MqttMessageUtil {
	
	private static final Logger LOG = Logger.getLogger(MqttMessageUtil.class);
    
    
    private static String broker = null;
    private static int qos = 2;
    
    
    /**
     * publish mqtt message
     * @param serverId
     * @param content
     */
    public static void publish(String topic, String content, String clientId){
    	if(StringUtils.isEmpty(clientId) || StringUtils.isEmpty(broker)){
    		Map<String, String> properties = PropertiesUtil.loadPropertiesToMap();
        	broker = properties.get("mqtt.broker.url");
        	qos = Integer.parseInt(properties.get("mqtt.qos"));
    	}
        publish(topic, content, clientId, broker, qos);
    }
    
    /**
     * publish mqtt message
     * @param topic
     * @param content
     * @param clientId
     * @param broker
     * @param qos
     */
    public static void publish(String topic, String content, String clientId, String broker, int qos){
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(false);
            LOG.debug("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            LOG.debug("Connected");
            LOG.debug("Publishing message: "+content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            sampleClient.publish(topic, message);
            LOG.debug("Message published");
            sampleClient.disconnect();
            LOG.debug("Disconnected");
            System.out.println("Disconnected");
        } catch(MqttException me) {
        	LOG.error("reason "+me.getReasonCode());
        	LOG.error("msg "+me.getMessage());
        	LOG.error("loc "+me.getLocalizedMessage());
        	LOG.error("cause "+me.getCause());
        	LOG.error("excep "+me);
        	System.out.println("excep");
        }
    }
    

    public static void main(String[] args) {
    	Map<String, Object> content = new HashMap<String, Object>();
    	content.put("id", 301787);
    	content.put("pkgName", "tv.xiaocong.landxcg");
    	content.put("name", "小葱斗地主");
    	content.put("icon", "uploadfile/application/icon/20141261514227000.png");
    	content.put("application", "uploadfile/application/application/20144111757953000.apk");
    	content.put("fileSize", 41478996);
    	//publish(889, 123456855, 1, JsonUtil.objToJson(content));
    }
}

