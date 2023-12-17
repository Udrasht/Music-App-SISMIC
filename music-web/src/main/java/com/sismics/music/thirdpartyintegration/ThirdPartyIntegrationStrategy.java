package com.sismics.music.thirdpartyintegration;
import javax.ws.rs.core.Response;

import java.io.IOException;

public interface ThirdPartyIntegrationStrategy {
	public Response search(String queryString,String queryType) throws IOException;
	public Response recommend(String queryString,String queryType)throws IOException;
}
