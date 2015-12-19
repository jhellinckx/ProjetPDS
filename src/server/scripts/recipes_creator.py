#!/usr/bin/python
import sys
import requests
import ast


try:
	import mysql.connector

except: # no mysql.connector
	print "Need MySQL python connector (provided by Oracle at https://dev.mysql.com/downloads/connector/python/)"
	sys.exit()


db_name = "db_appli"
filename = "../dao/dao.properties"
def db_params():
	username = None
	password = None
	f = open(filename,"r")
	for line in f:
		line = line.strip().split(" ")
		if line[0] == "username":
			username = line[-1]
		elif line[0] == "password":
			password = line[-1]
	f.close()
	return (username, password)

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

def ingredients_from_recipepuppy():
	all_ingredients = []
	for i in range (1, 101):
		response = requests.get(build_http_request(api, minRates=250,p=i))
		responseDict = ast.literal_eval(response.content)
		recipes = responseDict["results"]
		for recipe in recipes :
			recipe_ingredients = recipe["ingredients"].split(", ")
			for ingredient in recipe_ingredients:
				if ingredient not in all_ingredients:
					all_ingredients.append(ingredient)
	f = open("ingredients.txt","w+")
	f.write(str(all_ingredients))

(username, password) = db_params()

cnx = mysql.connector.connect(user=username, database=db_name,password=password)
cursor = cnx.cursor()

f = open("ingredients.txt","r") 
to_file = open("results.txt","w+")
all_ingredients = f.read()[1:-2].split(", ")
todo = len(all_ingredients)
inexisting = []
for ingredient in all_ingredients:
	ingredient = ingredient.strip("'")
	ingredient = ingredient.replace(" ","-")
	query = ("SELECT id_food, generic_name FROM Food WHERE generic_name LIKE '%"+ingredient+"%'")
	cursor.execute(query)
	i=0
	categories = []
	for (id_food, generic_name) in cursor:
		if(i==0):
			to_file.write("INGREDIENT NAME = "+ingredient+"\n")
		if(generic_name not in categories):
			to_file.write(generic_name.encode("utf8")+"\n")
			categories.append(generic_name)
		i+=1
	if(i>0):
		to_file.write("*"*50+"\n")
	else:
		inexisting.append(ingredient)

	todo-=1
	print(str(todo)+" ingredient(s) left")
to_file.write("INEXISTING INGREDIENTS : "+str(inexisting))

cursor.close()
cnx.close()