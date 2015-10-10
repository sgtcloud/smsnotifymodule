package cn.sgtcloud.common.smsnotifymodule.service;

import cn.sgtcloud.common.smsnotifymodule.service.exception.SmsNotifyException;


/**
 * 抽象的sms供应商接口
 * @author peisy
 *
 */
public interface SMSProvider {
	/**
	 * 发送短信
	 * @author peisy
	 * @param mobile  接收短信手机号
	 * @param text    短信内容
	 * @return
	 */
	String sendMessage(String mobile,String text);
	/**
	 * 获取sms供应商的账号信息
	 * @author peisy
	 * @return
	 */
	UserInfo getUserInfo();
}
