package cn.sgtcloud.common.smsnotifymodule.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import cn.sgtcloud.common.smsnotifymodule.service.SMSProvider;
import cn.sgtcloud.common.smsnotifymodule.service.UserInfo;
import cn.sgtcloud.common.smsnotifymodule.service.exception.SmsNotifyException;
/**
 * 云片网短信服务供应商
 * @author peisy
 *
 */
public class YUNPIANSMSProvider implements SMSProvider{
	
	// 查账户信息的http地址
    private static String URI_GET_USER_INFO = "http://yunpian.com/v1/user/get.json";

    //通用发送接口的http地址
    private static String URI_SEND_SMS = "http://yunpian.com/v1/sms/send.json";

    //编码格式。发送编码格式统一用UTF-8
    private static String ENCODING = "UTF-8";
    
	private String apiKey = "apiKey";
	
	public YUNPIANSMSProvider(String apiKey){
		this.apiKey=apiKey;
	}
	
	 /**
     * 取账户信息
     *
     * @return json格式字符串
	 * @throws SmsNotifyException 
     * @throws java.io.IOException
     */
	public UserInfo getUserInfo(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("apikey", apiKey);
        String result=post(URI_GET_USER_INFO, params);
        if(StringUtils.isNotEmpty(result)){
        	JSONObject jsonObject = JSONObject.fromObject(result);
        	JSONObject userjsonObject = (JSONObject)jsonObject.get("user");
        	UserInfo info = new UserInfo();
        	info.setBalance(Integer.parseInt(userjsonObject.getString("balance")));
        	info.setApi_version(userjsonObject.getString("api_version"));
        	if(StringUtils.isNotEmpty(userjsonObject.getString("ip_whitelist"))){
        		info.setIp_whitelist(userjsonObject.getString("ip_whitelist").split(","));
        	}
        	return info;
        }else
        	throw new SmsNotifyException();
    }

    /**
     * 通用接口发短信
     *
     * @param apiKey apiKey
     * @param text   　短信内容
     * @param mobile 　接受的手机号
     * @return json格式字符串
     * @throws SmsNotifyException 
     * @throws IOException
     */
	public String sendMessage(String mobile,String text){
        Map<String, String> params = new HashMap<String, String>();
        params.put("apikey", apiKey);
        params.put("text", text);
        params.put("mobile", mobile);
        String result = post(URI_SEND_SMS, params);
        if(StringUtils.isNotEmpty(result)){
        	JSONObject jsonObject = JSONObject.fromObject(result);
        	return jsonObject.optString("msg").toLowerCase();
        }else
        	throw new SmsNotifyException();
    }
    /**
     * 基于HttpClient 4.3的通用POST方法
     *
     * @param url       提交的URL
     * @param paramsMap 提交<参数，值>Map
     * @return 提交响应
     */
    private String post(String url, Map<String, String> paramsMap) {
        CloseableHttpClient client = HttpClients.createDefault();
        String responseText = "";
        CloseableHttpResponse response = null;
        try {
            HttpPost method = new HttpPost(url);
            if (paramsMap != null) {
                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> param : paramsMap.entrySet()) {
                    NameValuePair pair = new BasicNameValuePair(param.getKey(), param.getValue());
                    paramList.add(pair);
                }
                method.setEntity(new UrlEncodedFormEntity(paramList, ENCODING));
            }
            response = client.execute(method);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                responseText = EntityUtils.toString(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return responseText;
    }

}
