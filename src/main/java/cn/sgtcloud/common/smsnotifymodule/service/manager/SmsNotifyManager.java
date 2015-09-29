package cn.sgtcloud.common.smsnotifymodule.service.manager;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sgtcloud.common.smsnotifymodule.service.SMSProvider;
import cn.sgtcloud.common.smsnotifymodule.service.UserInfo;

import com.google.code.kaptcha.Producer;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class SmsNotifyManager {
	private static final Logger log = LoggerFactory.getLogger(SmsNotifyManager.class);
	
	//短信服务提供商
	private SMSProvider smsProvider;

	//随机验证码生成对象
	private Producer captchaProducer;
	
	//默认包含占位符的短信模板
	private final static String DEFAULT_SMS_TEMPLATE = "【{0}】您的验证码是{1}"; 
	
	//默认包含占位符的短信模板中占位符的正则表达式
	private final static String DEFAULT_SMS_REGEX = "\\{(\\d)\\}";
	
	
	//默认的短信有效时间单位（SECONDS、MINUTES、HOURS）
	private final static TimeUnit DEFAULT_SMS_TIMEUNIT = TimeUnit.SECONDS;
	
	//緩存对象
	private static Cache<String , String> cache ;
	
	//实例化方法
	public SmsNotifyManager(SMSProvider smsProvider,Producer captchaProducer,int time){
		init(smsProvider,captchaProducer,time,-1);
	}
	//实例化方法
	public SmsNotifyManager(SMSProvider smsProvider,Producer captchaProducer,int time,int maximumSize){
		init(smsProvider,captchaProducer,time,maximumSize);
	}
	
	private void init(SMSProvider smsProvider,Producer captchaProducer,int time,int maximumSize){
		this.smsProvider=smsProvider;
		this.captchaProducer=captchaProducer;
		if(maximumSize < 0){
			SmsNotifyManager.cache= CacheBuilder
			          .newBuilder()
			          .expireAfterWrite(time, DEFAULT_SMS_TIMEUNIT)
			          .build();
		}else{
			SmsNotifyManager.cache= CacheBuilder
			          .newBuilder()
			          .maximumSize(maximumSize)
			          .expireAfterWrite(time, DEFAULT_SMS_TIMEUNIT).softValues()
			          .build();
		}
	}
	/**
	 * 获取用户信息
	 * @return
	 */
	public UserInfo getUserInfo(){
		return smsProvider.getUserInfo();
	}
	/**
	 * 自定义内容发送验证码短信
	 * @param mobile   接收短信用户的手机号
	 * @param content  需要发送内容
	 * @param captcha  随机生成的短信验证码
	 * @return String  执行结果   ok：发送成功 ；其他：发送失败原因
	 */
	public String SendMessage(String mobile,String content,String captcha) {
		
		try {
			String result=smsProvider.sendMessage(mobile, content);
			if(result.equals("ok")){
				cache.put(mobile, captcha);
			}
			return result;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return "异常错误！";
	}
	
	/**
	 * 发送通用类验证码短信 
	 * eg：【{0}】您的验证码是{1}
	 * @param mobile  接收短信用户的手机号
	 * @param company  短信签名（公司简称、网站名等）
	 * @param captcha 随机生成的验证码
	 * @return String 执行结果   ok：发送成功 ；其他：发送失败原因
	 */
	public String SendTemplateMessage(String mobile, String company,String captcha) {
//		String captcha=getCaptcha();
		try {
			String content=fillStringByArgs(DEFAULT_SMS_TEMPLATE,DEFAULT_SMS_REGEX,company,captcha);
			if(StringUtils.isNotBlank(content) && content.equals("error")){
				return "非法的短信模板和替换内容";
			}else{
				String result=smsProvider.sendMessage(mobile, content);
				if(result.equals("ok")){
					cache.put(mobile, captcha);
				}
				return result;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return "异常错误！";
	}
	
	/**
	 * 发送自定义模板验证码短信  
	 * @param mobile  接收短信用户的手机号
	 * @param template  验证码短信模板  eg：【{0}】您的验证码是{1}
	 * @param captcha  要发送的短信验证码
	 * @param regex   短信模板中占位符正则表达式
	 * @param args    短信模板中替换占位符的内容 （占位符和内容的顺序要保持一致）
	 * @return  String  执行结果   ok：发送成功 ；其他：发送失败原因
	 */
	public String SendTemplateMessage(String mobile,String template,String captcha,String regex,String... args) {
		
		try {
			String content=fillStringByArgs(template,regex,args);
			if(StringUtils.isNotBlank(content) && content.equals("error")){
				return "非法的短信模板和替换内容";
			}else{
				String result=smsProvider.sendMessage(mobile, content);
				System.out.println("===result===>"+result);
				if(result.equals("ok")){
					cache.put(mobile, captcha);
				}
				return result;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return "异常错误！";
	}
	
	/**
	 * 发送自定义模板验证码短信  
	 * @param mobile  接收短信用户的手机号
	 * @param template  验证码短信模板  eg：【{0}】您的验证码是{1}
	 * @param captcha  要发送的短信验证码
	 * @param pattern   短信模板中占位符正则表达式pattern对象
	 * @param args    短信模板中替换占位符的内容 （占位符和内容的顺序要保持一致）
	 * @return   String 执行结果   ok：发送成功 ；其他：发送失败的原因   
	 */
	public String SendTemplateMessage(String mobile, String template,String captcha,Pattern pattern,String... args) {
		
		try {
			String content=fillStringByArgs(template,pattern,args);
			if(StringUtils.isNotBlank(content) && content.equals("error")){
				return "非法的短信模板和替换内容";
			}else{
				String result=smsProvider.sendMessage(mobile, content);
				if(result.equals("ok")){
					cache.put(mobile, captcha);
				}
				return result;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return "异常错误！";
	}
	/**
	 * 发送自定义模板验证码短信  
	 * @param mobile  接收短信用户的手机号
	 * @param template  验证码短信模板  eg：【{0}】您的验证码是{1}
	 * @param captcha  要发送的短信验证码
	 * @param m   短信模板中占位符正则表达式Matcher对象
	 * @param args    短信模板中替换占位符的内容 （占位符和内容的顺序要保持一致）
	 * @return  String 执行结果   ok：发送成功 ；其他：发送失败原因  
	 */
	public String SendTemplateMessage(String mobile, String template,String captcha,Matcher m,String... args) {
		
		try {
			String content=fillStringByArgs(template,m,args);
			if(StringUtils.isNotBlank(content) && content.equals("error")){
				return "非法的短信模板和替换内容";
			}else{
				String result=smsProvider.sendMessage(mobile, content);
				if(result.equals("ok")){
					cache.put(mobile, captcha);
				}
				return result;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return "异常错误！";
	}
	
	/**
	 * 字符串占位符替换（例如占位符为‘{0}’、‘{1}’）
	 * @param str 包含占位符字符串内容
	 * @param regex 占位符正则表达式
	 * @param arr 要替换占位符的字符串（顺序和占位符一定要一致）
	 * @return
	 */
	public String fillStringByArgs(String str,String regex,String... arr){
		try{
	        Matcher m = Pattern.compile(regex).matcher(str);
	        int count = 0;
	        while(m.find()){
	            str=str.replace(m.group(),arr[count]);
	            count ++;
	        }
	        return str;
		}catch(Exception e){
			log.error(e.getMessage(), e);
			return "error";
		}
    }
	
	/**
	 * 字符串占位符替换（例如占位符为‘{0}’、‘{1}’）
	 * @param str 包含占位符字符串内容
	 * @param pattern 正则表达式模型对象
	 * @param arr 要替换占位符的字符串（顺序和占位符一定要一致）
	 * @return
	 */
	public String fillStringByArgs(String str,Pattern pattern,String... arr){
		try{
	        Matcher m=pattern.matcher(str);
	        int count = 0;
	        while(m.find()){
	            str=str.replace(m.group(),arr[count]);
	            count ++;
	        }
	        return str;
		}catch(Exception e){
			log.error(e.getMessage(), e);
			return "error";
		}
    }
	/**
	 * 字符串占位符替换（例如占位符为‘{0}’、‘{1}’）
	 * @param str  包含占位符字符串内容
	 * @param m   正则表达式中Matcher对象
	 * @param arr 要替换占位符的字符串（顺序和占位符一定要一致）
	 * @return
	 */
	public String fillStringByArgs(String str,Matcher m,String... arr){
		try{
			int count = 0;
	        while(m.find()){
	            str=str.replace(m.group(),arr[count]);
	            count ++;
	        }
	        return str;
		}catch(Exception e){
			log.error(e.getMessage(), e);
			return "error";
		}
    }
	/**
	 * 生成验证码
	 * @return
	 */
	public String getCaptcha(){
		return captchaProducer.createText();
	}
	/**
	 * 验证验证码是否正确
	 * @param smobile  接受短信用户的手机号
	 * @param captcha  用户输入的验证码
	 * @return 1.验证成功 2.验证码输入错误 3.验证码已失效 4.异常操作
	 */
	public String isMatcher(String smobile,String captcha){
		
		try {
			String result=cache.get(smobile, new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "验证码已失效！";
				}
			});
			if(StringUtils.isNotEmpty(result) && result.equals("验证码已失效！")){
				return "3";
			}
			if(captcha.equals(result)){
				cache.invalidate(smobile);
				return "1";
			}else{
				return "2";
			}
		} catch (ExecutionException e) {
			log.error(e.getMessage(), e);
		}
		return "4";
	}
	
}
