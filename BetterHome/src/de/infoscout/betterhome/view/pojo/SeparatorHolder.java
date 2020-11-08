package de.infoscout.betterhome.view.pojo;

public class SeparatorHolder{
	private Boolean needSeparator;
	private String text;
	
	public SeparatorHolder(Boolean n, String t){
		this.needSeparator = n;
		this.text = t;
	}

	public Boolean getNeedSeparator() {
		return needSeparator;
	}

	public void setNeedSeparator(Boolean needSeparator) {
		this.needSeparator = needSeparator;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
