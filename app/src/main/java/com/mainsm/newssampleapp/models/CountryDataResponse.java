package com.mainsm.newssampleapp.models;

import com.google.gson.annotations.SerializedName;

public class CountryDataResponse{

	@SerializedName("zip")
	private String zip;

	@SerializedName("country")
	private String country;

	@SerializedName("city")
	private String city;

	@SerializedName("org")
	private String org;

	@SerializedName("timezone")
	private String timezone;

	@SerializedName("query")
	private String query;

	@SerializedName("regionName")
	private String regionName;

	@SerializedName("isp")
	private String isp;

	@SerializedName("lon")
	private double lon;

	@SerializedName("as")
	private String as;

	@SerializedName("countryCode")
	private String countryCode;

	@SerializedName("region")
	private String region;

	@SerializedName("lat")
	private double lat;

	@SerializedName("status")
	private String status;

	public String getZip(){
		return zip;
	}

	public String getCountry(){
		return country;
	}

	public String getCity(){
		return city;
	}

	public String getOrg(){
		return org;
	}

	public String getTimezone(){
		return timezone;
	}

	public String getQuery(){
		return query;
	}

	public String getRegionName(){
		return regionName;
	}

	public String getIsp(){
		return isp;
	}

	public double getLon(){
		return lon;
	}

	public String getAs(){
		return as;
	}

	public String getCountryCode(){
		return countryCode;
	}

	public String getRegion(){
		return region;
	}

	public double getLat(){
		return lat;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"CountryDataResponse{" + 
			"zip = '" + zip + '\'' + 
			",country = '" + country + '\'' + 
			",city = '" + city + '\'' + 
			",org = '" + org + '\'' + 
			",timezone = '" + timezone + '\'' + 
			",query = '" + query + '\'' + 
			",regionName = '" + regionName + '\'' + 
			",isp = '" + isp + '\'' + 
			",lon = '" + lon + '\'' + 
			",as = '" + as + '\'' + 
			",countryCode = '" + countryCode + '\'' + 
			",region = '" + region + '\'' + 
			",lat = '" + lat + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}