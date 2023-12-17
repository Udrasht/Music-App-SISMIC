package com.sismics.music.thirdpartyintegration;

import java.io.IOException;

import javax.ws.rs.core.Response;

public class ThirdPartyIntegrationService 
{
	private ThirdPartyIntegrationStrategy thirdPartyIntegrationStrategy;
	
	public void setStrategy(ThirdPartyIntegrationStrategy thirdPartyIntegrationStrategy) {
		this.thirdPartyIntegrationStrategy = thirdPartyIntegrationStrategy;
	}
	
	public Response searchSongs(String queryString,String queryType) throws IOException {
		return thirdPartyIntegrationStrategy.search(queryString,queryType);
	}
	
	public Response recommendSongs(String queryString,String queryType) throws IOException {
		return thirdPartyIntegrationStrategy.recommend(queryString,queryType);
	}
}
