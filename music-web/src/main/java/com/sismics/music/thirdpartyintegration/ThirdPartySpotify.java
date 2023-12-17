package com.sismics.music.thirdpartyintegration;

import java.net.URL;  
import java.io.InputStreamReader;  
import java.net.HttpURLConnection;  
import java.io.BufferedReader;
import com.google.gson.JsonObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

public class ThirdPartySpotify implements ThirdPartyIntegrationStrategy {

	@Override
	public Response search(String queryString,String queryType) throws IOException {
		//generate token		
		String accessToken = getAccessToken();
		String BASE_URI="https://api.spotify.com/v1/search";
		//String SEARCH_URL="https://api.spotify.com/v1/search?type=track&q=indian";
		
		String SEARCH_URI=BASE_URI+"?type="+queryType+"&q="+queryString+"";
		//add query params		
		URL url = new URL(SEARCH_URI);
		//make a get request to search
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestProperty("Authorization","Bearer "+accessToken);
		conn.setRequestProperty("Content-Type","application/json");
		conn.setRequestMethod("GET");
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String output;
		StringBuffer response = new StringBuffer();
		while ((output = in.readLine()) != null) {
			response.append(output);
		}
		in.close();
		// printing result from response

		ObjectMapper mapper = new ObjectMapper();
        com.fasterxml.jackson.core.JsonParser parser = mapper.getFactory().createParser(response.toString());
        
        JsonNode jsonNode = mapper.readTree(parser);
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		
		JsonObjectBuilder finalBuilder = Json.createObjectBuilder();        
		
		
		if(queryType.equalsIgnoreCase("artist")) {
        	finalBuilder = buildJsonForArtist(arrayBuilder,jsonNode);
        }else if(queryType.equalsIgnoreCase("track")){
        	finalBuilder=buildJsonForTracks(arrayBuilder,jsonNode);
        }
		
		javax.json.JsonObject finalJsonObject = finalBuilder.build();

		
    	return Response.ok(finalJsonObject.toString(),MediaType.APPLICATION_JSON).build();
	}
	
	private static JsonObjectBuilder buildJsonForTracks(JsonArrayBuilder arrayBuilder, JsonNode jsonNode) {
		JsonNode responseJson = jsonNode.get("tracks").get("items");
		for(JsonNode node : responseJson) {
			JsonObjectBuilder builder = Json.createObjectBuilder();
			
			String trackName = node.get("name").toString();
			trackName = removeQuotes(trackName);
			builder.add("trackName", trackName);
			
			
			JsonNode artistNode = node.get("artists");
    		String artists="";
    		
    		for(JsonNode artist : artistNode) {
    			String artistName = artist.get("name").toString();
    			artistName = removeQuotes(artistName);
    			artists+=artistName+",";
    		}
    		artists = artists.substring(0, artists.length() - 1);
    		
    		builder.add("artist", artists);
    		
    		
    		
    		String previewUrl = node.get("preview_url").toString();
    		previewUrl = removeQuotes(previewUrl);
    		
    		builder.add("previewUrl", previewUrl);
			
			String albumArt = node.get("album").get("images").get(0).get("url").toString();
			albumArt = removeQuotes(albumArt);
			builder.add("albumArt",albumArt);
			
			 arrayBuilder.add(builder);

		}
		JsonObjectBuilder finalBuilder = Json.createObjectBuilder();
		finalBuilder.add("tracks", arrayBuilder);
		return finalBuilder;
		
	}
	private static JsonObjectBuilder buildJsonForArtist(JsonArrayBuilder arrayBuilder, JsonNode jsonNode) {
		// TODO Auto-generated method stub
		JsonNode responseJson = jsonNode.get("artists").get("items");
		
		for(JsonNode node : responseJson) {
			
			 JsonObjectBuilder builder = Json.createObjectBuilder();
			 String artistName = node.get("name").toString();
			 artistName = removeQuotes(artistName);
			 builder.add("artistName", artistName);
			 
			 System.err.println(artistName);

			 String artistImg = "";
			 
			 if(node.get("images").get(0)==null) {
				 builder.add("artistImg", "img/img-unavailable.png");
			 }else {
				 artistImg = node.get("images").get(0).get("url").toString();
				 artistImg = removeQuotes(artistImg);
				 builder.add("artistImg", artistImg);
			 }			 
			 
			 String spotifyUrl = node.get("external_urls").get("spotify").toString();
			 spotifyUrl = removeQuotes(spotifyUrl);
			 builder.add("spotifyUrl",spotifyUrl);
			 
			 arrayBuilder.add(builder);
		}
		JsonObjectBuilder finalBuilder = Json.createObjectBuilder();
		finalBuilder.add("artists", arrayBuilder);
		return finalBuilder;
		
	}
	
	
	@Override
	public Response recommend(String queryString,String queryType) throws IOException {
		System.out.println("SPOTIFY RECOMMENDATION API");
		String accessToken = getAccessToken();
		String BASE_URI="https://api.spotify.com/v1/recommendations";
		
		ArrayList<String> artistNames = getArtistNames(queryString);
		String seedArtists="";
		for (int i=0 ;i<artistNames.size();i++) {
			String seedArtist = getArtistSeed(accessToken,artistNames.get(0));
			seedArtists = seedArtists + seedArtist+",";
		}
		seedArtists = seedArtists.substring(0, seedArtists.length() - 1);		
		queryString=seedArtists;
		
		String RECOMMEND_URI = BASE_URI+"?"+queryType+"="+queryString+"&limit=25";


		URL url = new URL(RECOMMEND_URI);
		
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization","Bearer "+" "+accessToken);
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Connection", "keep-alive");
        
        // Send the request and get response
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        
        
        ObjectMapper mapper = new ObjectMapper();
        com.fasterxml.jackson.core.JsonParser parser = mapper.getFactory().createParser(response.toString());
        
        JsonNode jsonNode = mapper.readTree(parser);
        JsonNode responseJson = jsonNode.get("tracks");
        
    	JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    	
    	for (JsonNode node : responseJson) {
            JsonObjectBuilder builder = Json.createObjectBuilder();
    		
    		String trackName = node.get("name").toString();
    		trackName = removeQuotes(trackName);
    		
    		builder.add("trackName",trackName);


    		JsonNode artistNode = node.get("artists");
    		String artists="";
    		
    		for(JsonNode artist : artistNode) {
    			String artistName = artist.get("name").toString();
    			artistName = removeQuotes(artistName);
    			artists+=artistName+",";
    		}
    		artists = artists.substring(0, artists.length() - 1);
    		
    		builder.add("artist", artists);
    		
    		String previewUrl = node.get("preview_url").toString();
    		previewUrl = removeQuotes(previewUrl);
    		
    		builder.add("previewUrl", previewUrl);
    		
    		String albumArtUrl = node.get("album").get("images").get(1).get("url").toString();
    		albumArtUrl = removeQuotes(albumArtUrl);
    	
  
    		builder.add("albuArt", albumArtUrl);
    		
    		arrayBuilder.add(builder);
    		
    	}
    	
    	JsonObjectBuilder finalBuilder = Json.createObjectBuilder();
    	finalBuilder.add("tracks", arrayBuilder);
    	javax.json.JsonObject finalJsonObject = finalBuilder.build();
    	
		

    	return Response.ok(finalJsonObject.toString(),MediaType.APPLICATION_JSON).build();
	}


	private static String getAccessToken() throws IOException {
		String CLIENT_ID="f897b413968b46288d95dbb1e543541d";
		String CLIENT_SECRET="b4c7897711df415d92b9651f8f827882";
		String AUTH_URL = "https://accounts.spotify.com/api/token";

		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(AUTH_URL);
		httpPost.setHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes(StandardCharsets.UTF_8)));
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		httpPost.setEntity(new StringEntity("grant_type=client_credentials"));

		try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String result = EntityUtils.toString(entity);
				JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();
				String accessToken = jsonObject.get("access_token").getAsString();
				return accessToken;
			}
		}
		return null;
	}
	
	private static String removeQuotes(String str) {
		return str.substring(1,str.length()-1);
	}

	private static ArrayList<String> getArtistNames(String queryString) {
		ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(queryString.split(",")));
		return arrayList;
	}

	public static String getArtistSeed(String accessToken,String artistName) throws IOException {
		//		get request to get artist seed
		String SEARCH_URI = "https://api.spotify.com/v1/search?type=artist&q="+artistName+"";
		
		URL url = new URL(SEARCH_URI);
		
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization","Bearer "+" "+accessToken);
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Connection", "keep-alive");
        
        // Send the request and get response
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        
        ObjectMapper mapper = new ObjectMapper();
        com.fasterxml.jackson.core.JsonParser parser = mapper.getFactory().createParser(response.toString());
        
        JsonNode jsonNode = mapper.readTree(parser);
        JsonNode s = jsonNode.get("artists").get("items").get(0);
	
    	String seed_id = s.get("id").toString();  
    	
    	seed_id = seed_id.substring(1, seed_id.length()-1);
   

    	return seed_id;
	}

}
