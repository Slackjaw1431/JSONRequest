package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootBookApiApplication {

	public static void main(String[] args) {

		Scanner scan = new Scanner(System.in);
		System.out.println("Enter the top number of articles to get");
		int limit = scan.nextInt();
		System.out.println("Enter the page number out of 5");
		int pageNumber = scan.nextInt();

		String[] top = getTopArticles(limit, pageNumber);
		
		for (String s : top) {
			System.out.println(s);
		}

	}

	private static String[] getTopArticles(int limit, int pageNumber) {
		String apiUrl = "https://jsonmock.hackerrank.com/api/articles?page=" + pageNumber;

		try {
			URL url = new URL(apiUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			int responseCode = connection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuilder response = new StringBuilder();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				JSONObject responseObject = new JSONObject(response.toString());
				JSONArray articles = responseObject.getJSONArray("data");

				List<JSONObject> filteredArticles = new ArrayList<>();
				for (int i = 0; i < articles.length(); i++) {
					JSONObject article = articles.getJSONObject(i);
					String title = article.optString("title");
					String storyTitle = article.optString("story_title");

					if (title != null || storyTitle != null) {
						filteredArticles.add(article);
					}
				}

				filteredArticles.sort(Comparator.comparing((JSONObject a) -> -a.optInt("num_comments"))
						.thenComparing((JSONObject a) -> a.optString("title", ""))
						.thenComparing((JSONObject a) -> a.optString("story_title", "")));
			
		        ArrayList<String> listOfTitles = new ArrayList<>();

				int j = 0;
				for (int i = 0; i < filteredArticles.size() && j < limit; i++) {
					JSONObject article = filteredArticles.get(i);
					String title = article.optString("title");
					String storyTitle = article.optString("story_title");

					if (!article.isNull("title")) {
						listOfTitles.add(title);
						j++;
						continue;
					} else if (!article.isNull("story_title")) {
						listOfTitles.add(storyTitle);
						j++;
						continue;
					} else if (article.isNull("title") && article.isNull("story_title")) {
						continue;
					}
				}
				
				listOfTitles.removeIf(String::isEmpty);
				String[] topTitles = new String[listOfTitles.size()];

				topTitles = listOfTitles.toArray(new String[0]);

				return topTitles;

			} else {
				System.out.println("Failed to fetch articles. Response code: " + responseCode);
			}
		} catch (

		Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
