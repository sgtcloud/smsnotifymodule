package cn.sgtcloud.common.smsnotifymodule.test;


import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
//		String captcha = smsNotifyManager.getCaptcha();
//		System.out.println(smsNotifyManager.SendMessage("15538856646", "上海游际",captcha,10));
		System.out.println(smsNotifyManager.SendTemplateMessage("15538856646","【{0}】您的验证码是{1}。如非本人操作，请忽略本短信","123456","\\{(\\d)\\}","上海游际","123456"));
		System.out.println(smsNotifyManager.isMatcher("15538856646", "123456"));
		try {
			System.out.println(smsNotifyManager.isMatcher("15538856646", "123456"));
			System.out.println(smsNotifyManager.isMatcher("15538856646", "123456"));
			System.out.println("==============分界线");
			System.out.println(smsNotifyManager.SendTemplateMessage("15538856647","【{0}】您的验证码是{1}。如非本人操作，请忽略本短信","654321","\\{(\\d)\\}","上海游际","654321"));
			System.out.println(smsNotifyManager.isMatcher("15538856647", "654321"));
			System.out.println("==============分界线");
			System.out.println(smsNotifyManager.SendTemplateMessage("15538856648","【{0}】您的验证码是{1}。如非本人操作，请忽略本短信","654321","\\{(\\d)\\}","上海游际","654321"));
			System.out.println(smsNotifyManager.isMatcher("15538856648", "654321"));
			System.out.println(smsNotifyManager.isMatcher("15538856646", "654321"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void getUserInfo() {
		System.out.println(smsNotifyManager.getUserInfo().toString());
	}
}
