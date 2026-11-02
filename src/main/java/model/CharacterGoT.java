package model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterGoT {

	@JsonProperty("aliases")
	private List<String> aliases = new ArrayList<>();

	@JsonProperty("gender")
	private String gender;

	@JsonProperty("born")
	private String born;

	@JsonProperty("father")
	private String father;

	@JsonProperty("playedBy")
	private List<String> playedBy = new ArrayList<>();

	@JsonProperty("died")
	private String died;

	@JsonProperty("titles")
	private List<String> titles = new ArrayList<>();

	@JsonProperty("allegiances")
	private List<String> allegiances = new ArrayList<>();

	@JsonProperty("url")
	private String url;

	@JsonProperty("spouse")
	private String spouse;

	@JsonProperty("mother")
	private String mother;

	@JsonProperty("books")
	private List<String> books = new ArrayList<>();

	@JsonProperty("culture")
	private String culture;

	@JsonProperty("tvSeries")
	private List<String> tvSeries = new ArrayList<>();

	@JsonProperty("name")
	private String name;

	@JsonProperty("povBooks")
	private List<String> povBooks = new ArrayList<>();


	public CharacterGoT() {
	}

	public void setAliases(List<String> aliases){
		this.aliases = aliases;
	}

	public List<String> getAliases(){
		return aliases;
	}

	public void setGender(String gender){
		this.gender = gender;
	}

	public String getGender(){
		return gender;
	}

	public void setBorn(String born){
		this.born = born;
	}

	public String getBorn(){
		return born;
	}

	public void setFather(String father){
		this.father = father;
	}

	public String getFather(){
		return father;
	}

	public void setPlayedBy(List<String> playedBy){
		this.playedBy = playedBy;
	}

	public List<String> getPlayedBy(){
		return playedBy;
	}

	public void setDied(String died){
		this.died = died;
	}

	public String getDied(){
		return died;
	}

	public void setTitles(List<String> titles){
		this.titles = titles;
	}

	public List<String> getTitles(){
		return titles;
	}

	public void setAllegiances(List<String> allegiances){
		this.allegiances = allegiances;
	}

	public List<String> getAllegiances(){
		return allegiances;
	}

	public void setUrl(String url){
		this.url = url;
	}

	public String getUrl(){
		return url;
	}

	public void setSpouse(String spouse){
		this.spouse = spouse;
	}

	public String getSpouse(){
		return spouse;
	}

	public void setMother(String mother){
		this.mother = mother;
	}

	public String getMother(){
		return mother;
	}

	public void setBooks(List<String> books){
		this.books = books;
	}

	public List<String> getBooks(){
		return books;
	}

	public void setCulture(String culture){
		this.culture = culture;
	}

	public String getCulture(){
		return culture;
	}

	public void setTvSeries(List<String> tvSeries){
		this.tvSeries = tvSeries;
	}

	public List<String> getTvSeries(){
		return tvSeries;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setPovBooks(List<String> povBooks){
		this.povBooks = povBooks;
	}

	public List<String> getPovBooks(){
		return povBooks;
	}

	@Override
 	public String toString(){
		return 
			"CharacterGoT{" +
			"aliases = '" + aliases + '\'' + 
			",gender = '" + gender + '\'' + 
			",born = '" + born + '\'' + 
			",father = '" + father + '\'' + 
			",playedBy = '" + playedBy + '\'' + 
			",died = '" + died + '\'' + 
			",titles = '" + titles + '\'' + 
			",allegiances = '" + allegiances + '\'' + 
			",url = '" + url + '\'' + 
			",spouse = '" + spouse + '\'' + 
			",mother = '" + mother + '\'' + 
			",books = '" + books + '\'' + 
			",culture = '" + culture + '\'' + 
			",tvSeries = '" + tvSeries + '\'' + 
			",name = '" + name + '\'' + 
			",povBooks = '" + povBooks + '\'' + 
			"}";
		}
}