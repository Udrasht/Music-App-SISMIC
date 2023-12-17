package com.sismics.music.thirdpartyintegration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStreamReader;  
import java.net.HttpURLConnection;  
import java.io.BufferedReader;
import javax.ws.rs.core.Response;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


public class ThirdPartyLastFM implements ThirdPartyIntegrationStrategy {

	@Override
	public Response search(String queryString,String queryType) throws IOException{
		// TODO Auto-generated method stub
		String API_KEY = "5a24f8cfea22982641452cf45409113a";
		String BASE_URI="http://ws.audioscrobbler.com/2.0/";
		
		String SEARCH_URL=BASE_URI+"?method="+queryType+".search&"+queryType+"="+queryString+"&api_key=+"+API_KEY+"&format=json&limit=20";
		
		URL url = new URL(SEARCH_URL);
		
		//make a get request to search
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

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
	@Override
	public Response recommend(String queryTracks,String queryArtists)throws IOException {
		ArrayList<String> artists = getArtist(queryArtists);
		ArrayList<String> tracks = getTracks(queryTracks);
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		
		for (int i =0 ;i<artists.size();i++) {
			getRequestForLastfm(arrayBuilder,artists.get(i),tracks.get(i));
			
		}
		
		JsonObjectBuilder finalBuilder = Json.createObjectBuilder();
    	finalBuilder.add("tracks", arrayBuilder);
    	JsonObject finalJsonObject = finalBuilder.build();
    	
		
    	return Response.ok(finalJsonObject.toString(),MediaType.APPLICATION_JSON).build();
		
	}
	
	private static JsonObjectBuilder buildJsonForTracks(JsonArrayBuilder arrayBuilder, JsonNode jsonNode) {
		// TODO Auto-generated method stub
		JsonNode responseJson = jsonNode.get("results").get("trackmatches").get("track");
		for(JsonNode node : responseJson) {
			JsonObjectBuilder builder = Json.createObjectBuilder();
			
			String trackName = node.get("name").toString();
			trackName = removeQuotes(trackName);
			builder.add("trackName", trackName);
			
			
			String artistName = node.get("artist").toString();
			artistName = removeQuotes(artistName);
			builder.add("artistName", artistName);
			
			
			String previewUrl= node.get("url").toString();
			previewUrl = removeQuotes(previewUrl);
			builder.add("previewUrl",previewUrl);
			
			builder.add("albumArt", "img/last-fm-album-art.png");
			
			arrayBuilder.add(builder);
		}
		
		JsonObjectBuilder finalBuilder = Json.createObjectBuilder();
		finalBuilder.add("tracks", arrayBuilder);
		return finalBuilder;
	}

	private static JsonObjectBuilder buildJsonForArtist(JsonArrayBuilder arrayBuilder, JsonNode jsonNode) {
		JsonNode responseJson = jsonNode.get("results").get("artistmatches").get("artist");
		for(JsonNode node : responseJson) {
			JsonObjectBuilder builder = Json.createObjectBuilder();
			
			String artistName = node.get("name").toString();
			artistName = removeQuotes(artistName);
			builder.add("artistName", artistName);
			
			String previewUrl= node.get("url").toString();
			previewUrl = removeQuotes(previewUrl);
			builder.add("previewUrl",previewUrl);
			
			builder.add("albumArt", "img/last-fm-album-art.png");
			arrayBuilder.add(builder);
		}
		
		JsonObjectBuilder finalBuilder = Json.createObjectBuilder();
		finalBuilder.add("artists", arrayBuilder);
		return finalBuilder;
	}
	public static void getRequestForLastfm(JsonArrayBuilder arrayBuilder,String artist,String track) throws IOException {
		String API_KEY = "5a24f8cfea22982641452cf45409113a";
		String BASE_URI="http://ws.audioscrobbler.com/2.0/";
		
		///2.0/?method=track.getsimilar&artist=cher&track=believe&api_key=YOUR_API_KEY&forma...
		String RECOMMEND_URI=BASE_URI+"?method=track.getsimilar&artist="+artist+"&track="+track+"&api_key=+"+API_KEY+"&format=json&limit=10";
		
		URL url = new URL(RECOMMEND_URI);
		
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		connection.setRequestProperty("Content-Type","application/json");
		connection.setRequestMethod("GET");
		
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String output;

		StringBuffer response = new StringBuffer();
		while ((output = in.readLine()) != null) {
			response.append(output);
		}

		in.close();
		
		ObjectMapper mapper = new ObjectMapper();
        com.fasterxml.jackson.core.JsonParser parser = mapper.getFactory().createParser(response.toString());
        
        JsonNode jsonNode = mapper.readTree(parser);
        JsonNode responseJson = jsonNode.get("similartracks").get("track");
            	
    	for (JsonNode node : responseJson) {
            JsonObjectBuilder builder = Json.createObjectBuilder();
           
    		String trackName = node.get("name").toString();
    		trackName = removeQuotes(trackName);
    		builder.add("trackName", trackName);
    		
    		String trackUrl = node.get("url").toString();
    		trackUrl = removeQuotes(trackUrl);
    		builder.add("trackUrl",trackUrl);

    		String artistName = node.get("artist").get("name").toString();
    		artistName = removeQuotes(artistName);
    		
    		builder.add("artistName",artistName);
    		
    		
    		builder.add("albumArt", "img/last-fm-album-art.png");
    		arrayBuilder.add(builder);
    		
    	}
    	
	}
	
	private static String removeQuotes(String str) {
		return str.substring(1,str.length()-1);
	}
	
	//return a list of artist from a comma separated strings
	private static ArrayList<String> getArtist(String queryArtists) {
		ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(queryArtists.split(",")));
		return arrayList;
	}
	//return a list of tracks from a comma separated strings
	private static ArrayList<String> getTracks(String queryTracks) {
		ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(queryTracks.split(",")));
		return arrayList;
	}


}
