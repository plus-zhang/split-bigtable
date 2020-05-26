package io.banjuer.config.em;

import java.util.HashMap;
import java.util.Map;

public enum ResponseResult {

	/**
	 *
	 */
	success("10000", "操作成功"), error("10001", "操作失败"), warn("10002", "操作警告");

	private String value;
	private String displayName;
	
	static Map<String, ResponseResult> enumMap = new HashMap<>();
	static {
		for (ResponseResult type : ResponseResult.values()) {
			enumMap.put(type.getValue(), type);
		}
	}

	private ResponseResult(String value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public static ResponseResult getEnum(String value) {
		return enumMap.get(value);
	}
}
