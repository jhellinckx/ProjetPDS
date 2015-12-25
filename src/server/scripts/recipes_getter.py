#!/usr/bin/python
import sys
import requests
import ast
from bs4 import BeautifulSoup

api = "http://www.recipepuppy.com/api/"

def build_http_request(api_url, **kwargs):
	items = kwargs.items()
	params=""
	if len(items)>0 :
		params+="?" # Parameters HTTP indicator
		http_param = "%(key)s=%(value)s&"
		for name, value in items:
			params += http_param % {"key" : name, "value" : value}
		params = params[:-1] # Get rid of last '&'
	return api_url+params

def recipes_from_recipepuppy():
	all_recipes = []
	for i in range (1, 101):
		response = requests.get(build_http_request(api, minRates=250,p=i))
		responseDict = ast.literal_eval(response.content)
		recipes = responseDict["results"]
		for recipe in recipes :
			if "allrecipes" in recipe["href"]:
				all_recipes.append(recipe)
	f = open("allrecipes.txt","w+")
	f.write(str(all_recipes))

def parse_recipe_info(url):
	res = requests.get(url)
	parsed_res = BeautifulSoup(res.content)
	ratingInfos = 	{
					"rating": 		None,
					"n_ratings": 	None
					}	
	parsed_rating = BeautifulSoup(str(parsed_res.body.find("span",attrs={"class":"aggregate-rating","itemprop":"aggregateRating"})))
	if parsed_rating != None :
		meta_tag = parsed_rating.find("meta")
		if meta_tag != None :
			ratingInfos["rating"] = meta_tag.attrs["content"]
		span_tag = parsed_rating.find("span", attrs={"class":"review-count"})
		if span_tag != None:
			ratingInfos["n_ratings"] = str(span_tag.text)


	nutrInfos = {
				"calories": 			None, 
				"fatContent":			None, 
				"carbohydrateContent":	None,
				"proteinContent":		None,
				"cholesterolContent":	None
				}

	parsed_nutr = BeautifulSoup(str(parsed_res.body.find("div",attrs={"class":"recipe-nutrition__form"})))
	if parsed_nutr != None:
		for nutr in nutrInfos.keys():
			nutr_li_tag = parsed_nutr.find("li",attrs={"itemprop":nutr})
			if nutr_li_tag != None:
				nutrInfos[nutr] = str(nutr_li_tag.text)

	return dict(ratingInfos, **nutrInfos)

def parse_allrecipes_info(filename="allrecipes.txt"):
	CYAN = "\033[36m"
	GREEN = "\033[32m"
	RED = "\033[31m"
	RESET = "\033[0m"
	f = open(filename,"r")
	recipes = ast.literal_eval(f.read())

	# Try to get the already done URLs
	done_recipes = {}
	try:
		doneFile = open(filename.split(".")[0]+"_with_infos"+".txt","r")
		doneRaw = doneFile.read()
		for i in range(len(doneRaw)-1, -1, -1): # In case the last writing did not complete the write operation of a recipe
			if doneRaw[i] == '}':
				doneRaw = doneRaw[:i+1] + "]"
				break
		done_recipes_list = ast.literal_eval(doneRaw)
		for recipe in done_recipes_list:
			done_recipes[recipe["href"].replace("\\","")] = recipe
	except:
		print RED + "Could not find previous file. Starting fresh..." + RESET
	finally:
		doneFile.close()

	toFile = open(filename.split(".")[0]+"_with_infos"+".txt","w+")
	toFile.write("[")
	for i in range(len(recipes)) :
		url = recipes[i]["href"].replace("\\","")
		if url in done_recipes:
			recipes[i] = done_recipes[url]
			print CYAN + str(i+1) + "/" + str(len(recipes)) + RESET + " " + url + " -> " + GREEN + "DONE" + RESET
		else:
			infos = parse_recipe_info(url)
			print CYAN + str(i+1) + "/" + str(len(recipes)) + RESET + " " + url + " -> " + str(infos)
			recipes[i] = dict(recipes[i], **infos) # Concatenate the dictionaries
		toFile.write(str(recipes[i]))
		if(i < len(recipes)-1):
			toFile.write(",")
	toFile.write("]")
	toFile.close()
	f.close()


