package model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HouseGoT {

	@JsonProperty("swornMembers")
	private List<String> swornMembers = new ArrayList<>();

	@JsonProperty("founder")
	private String founder;

	@JsonProperty("cadetBranches")
	private List<String> cadetBranches = new ArrayList<>();

	@JsonProperty("words")
	private String words;

	@JsonProperty("founded")
	private String founded;

	@JsonProperty("titles")
	private List<String> titles = new ArrayList<>();

	@JsonProperty("seats")
	private List<String> seats = new ArrayList<>();

	@JsonProperty("url")
	private String url;

	@JsonProperty("coatOfArms")
	private String coatOfArms;

	@JsonProperty("diedOut")
	private String diedOut;

	@JsonProperty("heir")
	private String heir;

	@JsonProperty("name")
	private String name;

	@JsonProperty("region")
	private String region;

	@JsonProperty("currentLord")
	private String currentLord;

	@JsonProperty("ancestralWeapons")
	private List<String> ancestralWeapons = new ArrayList<>();

	@JsonProperty("overlord")
	private String overlord;

	public HouseGoT() {
	}

	public void setSwornMembers(List<String> swornMembers){
		this.swornMembers = swornMembers;
	}

	public List<String> getSwornMembers(){
		return swornMembers;
	}

	public void setFounder(String founder){
		this.founder = founder;
	}

	public String getFounder(){
		return founder;
	}

	public void setCadetBranches(List<String> cadetBranches){
		this.cadetBranches = cadetBranches;
	}

	public List<String> getCadetBranches(){
		return cadetBranches;
	}

	public void setWords(String words){
		this.words = words;
	}

	public String getWords(){
		return words;
	}

	public void setFounded(String founded){
		this.founded = founded;
	}

	public String getFounded(){
		return founded;
	}

	public void setTitles(List<String> titles){
		this.titles = titles;
	}

	public List<String> getTitles(){
		return titles;
	}

	public void setSeats(List<String> seats){
		this.seats = seats;
	}

	public List<String> getSeats(){
		return seats;
	}

	public void setUrl(String url){
		this.url = url;
	}

	public String getUrl(){
		return url;
	}

	public void setCoatOfArms(String coatOfArms){
		this.coatOfArms = coatOfArms;
	}

	public String getCoatOfArms(){
		return coatOfArms;
	}

	public void setDiedOut(String diedOut){
		this.diedOut = diedOut;
	}

	public String getDiedOut(){
		return diedOut;
	}

	public void setHeir(String heir){
		this.heir = heir;
	}

	public String getHeir(){
		return heir;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setRegion(String region){
		this.region = region;
	}

	public String getRegion(){
		return region;
	}

	public void setCurrentLord(String currentLord){
		this.currentLord = currentLord;
	}

	public String getCurrentLord(){
		return currentLord;
	}

	public void setAncestralWeapons(List<String> ancestralWeapons){
		this.ancestralWeapons = ancestralWeapons;
	}

	public List<String> getAncestralWeapons(){
		return ancestralWeapons;
	}

	public void setOverlord(String overlord){
		this.overlord = overlord;
	}

	public String getOverlord(){
		return overlord;
	}

	@Override
 	public String toString(){
		return 
			"HouseGoT{" +
			"swornMembers = '" + swornMembers + '\'' + 
			",founder = '" + founder + '\'' + 
			",cadetBranches = '" + cadetBranches + '\'' + 
			",words = '" + words + '\'' + 
			",founded = '" + founded + '\'' + 
			",titles = '" + titles + '\'' + 
			",seats = '" + seats + '\'' + 
			",url = '" + url + '\'' + 
			",coatOfArms = '" + coatOfArms + '\'' + 
			",diedOut = '" + diedOut + '\'' + 
			",heir = '" + heir + '\'' + 
			",name = '" + name + '\'' + 
			",region = '" + region + '\'' + 
			",currentLord = '" + currentLord + '\'' + 
			",ancestralWeapons = '" + ancestralWeapons + '\'' + 
			",overlord = '" + overlord + '\'' + 
			"}";
		}
}