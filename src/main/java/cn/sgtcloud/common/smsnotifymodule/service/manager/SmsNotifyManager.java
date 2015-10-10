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
import cn.sgtcloud.common.smsnotifymodule.service.exception.IllegalTemplateContentException;
import cn.sgtcloud.common.smsnotifymodule.service.exception.LimitSendCaptchaException;
import cn.sgtcloud.common.smsnotifymodule.service.exception.SmsNotifyException;

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
	private Cache<String , String> cache ;
	
	//默认的同一手机号多次发送验证码冷却时间
	private final static int DEFAULT_BLOCKTIME = 30;
	
	//緩存对象 记录每一个手机号最后发送验证码的时间
	private Cache<String , String> lastTimeCache ;
	
	//实例化方法
	public SmsNotifyManager(SMSProvider smsProvider,Producer captchaProducer,int time,int blockTime,int maximumSize){
		init(smsProvider,captchaProducer,time,blockTime,maximumSize);
	}
	
	private void init(SMSProvider smsProvider,Producer captchaProducer,int time,int blockTime,int maximumSize){
		this.smsProvider=smsProvider;
		this.captchaProducer=captchaProducer;
		if(maximumSize <= 0){
			maximumSize = -1;
		}
		if(blockTime < 0){
			blockTime = DEFAULT_BLOCKTIME;
		}
		lastTimeCache = createCache(blockTime,maximumSize);
		cache = createCache(time,maximumSize);
	}
	
	private Cache<String , String> createCache(int time,int maximumSize){
		if(maximumSize < 0){
			return CacheBuilder
			        .newBuilder()
			        .expireAfterWrite(time, DEFAULT_SMS_TIMEUNIT).softValues()
			        .build();
		}else{
			return CacheBuilder
			        .newBuilder()
			        .maximumSize(maximumSize)
			        .expireAfterWrite(time, DEFAULT_SMS_TIMEUNIT).softValues()
			        .build();
		}
		
	}
	/**
	 * 获取用户信息
	 * @return
	 * @throws SmsNotifyException 
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
	public String sendMessage(String mobile,String content,String captcha){
		
		if(checkBlock(mobile)){
			String result=smsProvider.sendMessage(mobile, content);
			if(result.equals("ok")){
				cache.put(mobile, captcha);
				lastTimeCache.put(mobile, captcha);
				return result;
			}else{
				throw new SmsNotifyException(result);
			}
		}else
			throw new LimitSendCaptchaException("同一手机号连续发送短信频率过快，请稍后重试！");
	}
	
	private String send(String mobile,String content,String captcha){
		
		if(StringUtils.isNotBlank(content) && content.equals("error")){
			throw new IllegalTemplateContentException("非法的短信模板和替换内容");
		}else if(checkBlock(mobile)){
			String result=smsProvider.sendMessage(mobile, content);
			if(result.equals("ok")){
				cache.put(mobile, captcha);
				lastTimeCache.put(mobile, captcha);
				return result;
			}else{
				throw new SmsNotifyException(result);
			}
		}else{
			throw new LimitSendCaptchaException("同一手机号连续发送短信频率过快，请稍后重试！");
		}
	}
	
	/**
	 * 发送通用类验证码短信 
	 * eg：【{0}】您的验证码是{1}
	 * @param mobile  接收短信用户的手机号
	 * @param company  短信签名（公司简称、网站名等）
	 * @param captcha 随机生成的验证码
	 * @return String 执行结果   ok：发送成功 ；其他：发送失败原因
	 */
	public String sendTemplateMessage(String mobile, String company,String captcha){
			String content=fillStringByArgs(DEFAULT_SMS_TEMPLATE,DEFAULT_SMS_REGEX,company,captcha);
			return send(mobile,content,captcha);
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
	public String sendTemplateMessage(String mobile,String template,String captcha,String regex,String... args){
		
			String content=fillStringByArgs(template,regex,args);
			return send(mobile,content,captcha);
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
	public String sendTemplateMessage(String mobile, String template,String captcha,Pattern pattern,String... args){
			String content=fillStringByArgs(template,pattern,args);
			return send(mobile,content,captcha);
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
	public String sendTemplateMessage(String mobile, String template,String captcha,Matcher m,String... args){
		
		String content=fillStringByArgs(template,m,args);
		return send(mobile,content,captcha);
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
			String result = cache.get(smobile, new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "Captcha failed";
				}
			});
			if(StringUtils.isNotEmpty(result)){
				if(result.equals("Captcha failed"))
					return "3";
				else if(captcha.equals(result)){
					cache.invalidate(smobile);
					return "1";
				}else{
					return "2";
				}
			}
		} catch (ExecutionException e) {
			log.error(e.getMessage(),e);
		}
		return "4";
	}
	/**
	 * 判断当前手机号是否处于冷却状态（限制同一个手机号连续发送短信的频率）
	 * @param smobile
	 * @return
	 */
	private boolean checkBlock(String smobile){
		try {
			String result = lastTimeCache.get(smobile, new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "Expired";
				}
			});
			if(StringUtils.isNotBlank(result) && result.equals("Expired")){
				return true;
			}
		} catch (ExecutionException e) {
			log.error(e.getMessage(),e);
		}
		return false;
	}
	
}
