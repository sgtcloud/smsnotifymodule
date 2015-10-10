# 特性 #
通过一套统一的基于短信验证码操作的api对多个短信服务商进行了抽象<br/>
1. 抽象了短信验证码发送，获取用户在短信服务商平台信息。<br/>
2. 提供了云片网（短信服务商）的实现。<br/>
3. 提供了代码和spring注入的单元测试例子<br/>
4. 支持使用spring注入<br/>
# 快速开始 #
**通过maven安装**<br/>
<pre>
&lt;dependency>
  &lt;groupId>cn.sgtcloud.common&lt;/groupId>
  &lt;artifactId>smsnotifymodule&lt;/artifactId>
  &lt;version>0.0.1-SNAPSHOT&lt;/version>
&lt;/dependency>
&lt;!-- 下面是源码和javadoc，非必须添加 -->
&lt;dependency>
  &lt;groupId>cn.sgtcloud.common&lt;/groupId>
  &lt;artifactId>smsnotifymodule&lt;/artifactId>
  &lt;version>0.0.1-SNAPSHOT&lt;/version>
  &lt;classifier>sources&lt;/classifier>
&lt;/dependency>
&lt;dependency>
  &lt;groupId>cn.sgtcloud.common&lt;/groupId>
  &lt;artifactId>smsnotifymodule&lt;/artifactId>
  &lt;version>0.0.1-SNAPSHOT&lt;/version>
  &lt;classifier>javadoc&lt;/classifier>
&lt;/dependency>
</pre>

**spring方式注入**<br/>

这里以云片网为例的配置<br/>
1.创建bean声明，可从相关人员获得生产环境的参数<br/>
<pre>
&lt;!-- <a href="http://yunpian.com" target="_blank">云片网</a>的配置 示例-->
&lt;bean id="yunpianProvider"
	class="cn.sgtcloud.common.smsnotifymodule.service.impl.YUNPIANSMSProvider">
	&lt;constructor-arg index="0" value="apikey" />
&lt;/bean>
&lt;!-- 随机验证码生成配置 -->
&lt;bean id="captchaProducer" class="com.google.code.kaptcha.impl.DefaultKaptcha">
 &lt;property name="config">
  &lt;bean class="com.google.code.kaptcha.util.Config">
   &lt;constructor-arg>
    &lt;props>
     &lt;prop key="kaptcha.image.width">250&lt;/prop>
     &lt;prop key="kaptcha.image.height">100&lt;/prop>
     &lt;prop key="kaptcha.border">no&lt;/prop>
     &lt;prop key="kaptcha.border.color">105,179,90&lt;/prop>
     &lt;prop key="kaptcha.textproducer.font.color">red&lt;/prop>
     &lt;prop key="kaptcha.textproducer.font.size">90&lt;/prop>
     &lt;prop key="kaptcha.session.key">code&lt;/prop>
     &lt;!--配置验证码生成规则-->
     &lt;prop key="kaptcha.textproducer.char.string">0123456789&lt;/prop>
     &lt;!--配置验证码生成长度-->
     &lt;prop key="kaptcha.textproducer.char.length">6&lt;/prop>
     &lt;prop key="kaptcha.textproducer.font.names">宋体,楷体,微软雅黑&lt;/prop>
    &lt;/props>
   &lt;/constructor-arg>
  &lt;/bean>
 &lt;/property>
&lt;/bean>
&lt;!-- 验证码短信管理配置 -->
&lt;bean id="smsNotifyManager" class="cn.sgtcloud.common.smsnotifymodule.service.manager.SmsNotifyManager">
	&lt;constructor-arg index="0" ref="yunpianProvider"/>
	&lt;constructor-arg index="1" ref="captchaProducer"/>
	&lt;!-- 配置guava cache 有效期  短信验证码有效期单位秒（SECONDS） -->
	&lt;constructor-arg index="2" value="5"/>
	&lt;!-- 配置 限制同一个手机号连续发送短信的频率 秒为单位 建议设置为30 值为0时 不进行限制 -->
	&lt;constructor-arg index="3" value="30"/>
	&lt;!-- 配置guava cache 最大size 值为不大于0时，不进行限制最大值 -->
	&lt;constructor-arg index="4" value="1000"/>
&lt;/bean>
</pre>
2.代码中引用
<pre>
	@Autowired
	private SmsNotifyManager smsNotifyManager;
</pre>
3.获取用户信息(在短信服务商平台上的信息，比如短信剩余量、当前api版本号等信息)
<pre>
	smsNotifyManager.getUserInfo();
</pre>
4.生成验证码
<pre>
    smsNotifyManager.getCaptcha();
</pre>
5.发送验证码短信
<pre>
    &lt;!--发送通用类验证码短信 eg：【{0}】您的验证码是{1}-->
    smsNotifyManager.sendMessage("手机号","公司名称","验证码");
    &lt;!--发送模板类验证码短信 -->
    smsNotifyManager.sendMessage("手机号","短信模板","验证码","短信模板中占位符正则表达式",<br>    "短信模板中替换占位符的内容");
</pre>
6.验证验证码是否正确
<pre>
    smsNotifyManager.isMatcher("手机号","验证码");
</pre>
7.字符串中占位符替换（模板类短信使用）
<pre>
    smsNotifyManager.fillStringByArgs("包含占位符字符串内容","占位符正则表达式","要替换占位符的字符串");
</pre>

**代码方式声明<br/>**
1.创建实例<br>
  <pre>
    &lt;!-- 实例一个短信服务提供商(这里是<a href="http://yunpian.com" target="_blank">云片网</a>)-->
    SMSProvider yunpianProvider = new YUNPIANSMSProvider("apikey");
    &lt;!-- 实例一个验证码生成工具类 DefaultKaptcha（详情参考<a href="https://github.com/axet/kaptcha" target="_blank">kaptcha教程</a>）-->
    DefaultKaptcha kaptcha = new DefaultKaptcha();
    Properties properties = new Properties();
    properties.setProperty("kaptcha.textproducer.char.string", "0123456789");
    properties.setProperty("kaptcha.textproducer.char.length", "6");
    Config config = new Config(properties);
    kaptcha.setConfig(config);
    SmsNotifyManager smsNotifyManager = new SmsNotifyManager(yunpianProvider,kaptcha,"短信有效期时间","冷却时间","最大size设置");
  </pre> 
2.其他操作同spring方式。