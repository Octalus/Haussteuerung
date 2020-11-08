package de.infoscout.betterhome.model.device.components;

public class IpSetting {

	private String Ip;

	private String Netmask;

	private String Gateway;

	private String Dns;

	public void setIp(String ip) {
		Ip = ip;
	}

	public String getIp() {
		return Ip;
	}

	public void setNetmask(String netmask) {
		Netmask = netmask;
	}

	public String getNetmask() {
		return Netmask;
	}

	public void setGateway(String gateway) {
		Gateway = gateway;
	}

	public String getGateway() {
		return Gateway;
	}

	public void setDns(String dns) {
		Dns = dns;
	}

	public String getDns() {
		return Dns;
	}

}