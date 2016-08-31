package com.melvitech.rebutan.crawler;

public enum ECrawlSource {
	
	KASKUS("Kaskus","//div[@class='listing-table']/table/tbody/tr"),
	OLX("Tokobagus/OLX","//div[@data-id-navigation='listing']"),
	BERNIAGA("Berniaga","//div[@id='navigation_left-ads']/div[@class='list_row_keywords-fluid navigation_left-ad-view list_highlight']");

	private final String code;
	private final String baseData;
	
	private ECrawlSource(final String code, final String baseData) {
		this.code = code;
		this.baseData = baseData;
	}

	public String getCode() {
		return code;
	}

	public String getBaseData() {
		return baseData;
	}

}
