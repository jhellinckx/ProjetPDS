#!/usr/bin/python

import sys

try:
	import mysql.connector

except: # no mysql.connector
	print "Need MySQL python connector (provided by Oracle at https://dev.mysql.com/downloads/connector/python/)"
	sys.exit()


db_name = "db_appli"
db_properties_file = "../dao/dao.properties"

def db_params():
	username = None
	password = None
	f = open(db_properties_file,"r")
	for line in f:
		line = line.strip().split(" ")
		if line[0] == "username":
			username = line[-1]
		elif line[0] == "password":
			password = line[-1]
	f.close()
	return (username, password)

def link_ingredients_to_OFF():
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
		ingredientCap = ingredient[0].capitalize() + ingredient[1:]
		query = ("SELECT id_food, categories, product_name FROM Food WHERE categories LIKE '%"+ingredient+"%' OR categories LIKE '%"+ingredientCap+"%'")
		cursor.execute(query)
		i=0
		for (id_food, categories, product_name) in cursor:
			if(i==0):
				to_file.write("INGREDIENT NAME = "+ingredient+"\n")
				to_file.write(categories.encode('utf-8')+"\n")
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


