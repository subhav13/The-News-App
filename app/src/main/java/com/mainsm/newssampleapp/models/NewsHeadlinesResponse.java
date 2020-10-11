package com.mainsm.newssampleapp.models;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class NewsHeadlinesResponse{
	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}

	@SerializedName("totalResults")
	private int totalResults;

	@SerializedName("articles")
	private List<ArticlesItem> articles;

	@SerializedName("status")
	private String status;

	public int getTotalResults(){
		return totalResults;
	}

	public List<ArticlesItem> getArticles(){
		return articles;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"NewsHeadlinesResponse{" + 
			"totalResults = '" + totalResults + '\'' + 
			",articles = '" + articles + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}