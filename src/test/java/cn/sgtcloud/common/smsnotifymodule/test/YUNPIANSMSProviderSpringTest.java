package cn.sgtcloud.common.smsnotifymodule.test;


import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

import cn.sgtcloud.common.smsnotifymodule.service.exception.IllegalTemplateContentException;
import cn.sgtcloud.common.smsnotifymodule.service.exception.LimitSendCaptchaException;
import cn.sgtcloud.common.smsnotifymodule.service.exception.SmsNotifyException;
import cn.sgtcloud.common.smsnotifymodule.service.impl.YUNPIANSMSProvider;
import cn.sgtcloud.common.smsnotifymodule.service.manager.SmsNotifyManager;

public class YUNPIANSMSProviderSpringTest {
	
	private SmsNotifyManager smsNotifyManager;
	
	@Before
	public void init() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("sms-applicationContext-sample.xml");
		smsNotifyManager=(SmsNotifyManager)applicationContext.getBean("smsNotifyManager");
	}
	@Test
	public void sendMessage() {
		String captcha = smsNotifyManager.getCaptcha();
//		System.out.println(smsNotifyManager.SendMessage("15538856646", "上海游际",captcha,10));
		System.out.println(smsNotifyManager.sendTemplateMessage("15538856646", "【上海游际】{0}({1}手机动态码，请完成验证)，如非本人操作，请忽略本短信", captcha, "\\{(\\d)\\}", captcha,"test"));
//		System.out.println(smsNotifyManager.SendTemplateMessage("15538856646","【上海游际】{0}({1}手机动态码，请完成验证)，如非本人操作，请忽略本短信","123456","\\{(\\d)\\}","123456","神仙消消乐"));
		System.out.println(smsNotifyManager.isMatcher("15538856646", "123456"));
//		System.out.println(smsNotifyManager.isMatcher("15538856646", "123456"));
	}
	
	@Test
	public void getUserInfo() {
//		System.out.println(smsNotifyManager.getUserInfo().toString());
		
	}
	@Test
	public void test(){
		DefaultKaptcha kaptcha = new DefaultKaptcha();
		Properties properties = new Properties();
		properties.setProperty("kaptcha.textproducer.char.string", "0123456789");
		properties.setProperty("kaptcha.textproducer.char.length", "6");
		Config config = new Config(properties);
		kaptcha.setConfig(config);
		smsNotifyManager = new SmsNotifyManager(new YUNPIANSMSProvider(""), kaptcha,10,10,-1);
		System.out.println(kaptcha.createText());
	}
	@Test
	public void test2(){
		sendMessage();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		sendMessage();
		try {
			Thread.sleep(2100);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		sendMessage();
	}
}
