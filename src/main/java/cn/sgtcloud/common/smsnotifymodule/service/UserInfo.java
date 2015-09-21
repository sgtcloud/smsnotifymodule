package cn.sgtcloud.common.smsnotifymodule.service;

import java.io.Serializable;
import java.util.Arrays;

public class UserInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer balance;
	
	private String[] ip_whitelist;
	
	private String api_version;

	public Integer getBalance() {
		return balance;
	}

	public void setBalance(Integer balance) {
		this.balance = balance;
	}

	public String[] getIp_whitelist() {
		return ip_whitelist;
	}

	public void setIp_whitelist(String[] ip_whitelist) {
		this.ip_whitelist = ip_whitelist;
	}

	public String getApi_version() {
		return api_version;
	}

	public void setApi_version(String api_version) {
		this.api_version = api_version;
	}

	@Override
	public String toString() {
		return "UserInfo [balance=" + balance + ", ip_whitelist="
				+ Arrays.toString(ip_whitelist) + ", api_version="
				+ api_version + "]";
	}
	
	
}
