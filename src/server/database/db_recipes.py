#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys
import ast

try:
	import mysql.connector
	from mysql.connector import errorcode

except: # no mysql.connector
	print "Error : need MySQL python connector (provided by Oracle at https://dev.mysql.com/downloads/connector/python/).\n\
	Ubuntu install : http://codeinthehole.com/writing/how-to-set-up-mysql-for-python-on-ubuntu/"
	sys.exit()

YELLOW = "\033[33m"
MAGENTA = "\033[35m"
GREEN = "\033[32m"
RED = "\033[31m"
RESET = "\033[0m"

db_name = "db_appli"
db_properties_filename = "../dao/dao.properties"
ingredients_filename = "raw/db_ingredients.txt"
recipes_filename = "raw/db_recipes.txt"

def db_params():
	username = None
	password = None
	f = open(db_properties_filename,"r")
	for line in f:
		line = line.strip().split(" ")
		if line[0] == "username":
			username = line[-1]
		elif line[0] == "password":
			password = line[-1]
	f.close()
	return (username, password)

def drop_added_tables_in_db(tables=None):
	if tables == None : tables = ["RecipesIngredients", "Recipes", "RecipesIngredientsLists"]
	drop_command = ("DROP TABLE `%s`")

	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()
	cursor.execute(("SET FOREIGN_KEY_CHECKS=0"))
	for table in tables:
		sys.stdout.write("Dropping " + MAGENTA + table + RESET + " table... ")
		try:
			cursor.execute(drop_command%table)
			sys.stdout.write(GREEN + "OK" + RESET + "\n")
		except mysql.connector.Error as err:
			sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")
	
	cursor.execute(("SET FOREIGN_KEY_CHECKS=1"))


	cnx.commit()
	cursor.close()
	cnx.close()

def add_ingredients_in_db():
	ingredients_table_command = (
    	"CREATE TABLE `RecipesIngredients` ("
    	"  `id` int(11) NOT NULL AUTO_INCREMENT,"
    	"  `name` varchar(100) NOT NULL,"
    	"  PRIMARY KEY (`id`)"
    	") ENGINE=InnoDB")

	ingredients_insert_command = (
		"INSERT INTO RecipesIngredients "
		"(name) "
		"VALUES (%(name)s)")

	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()

	# Create RecipesIngredients table
	sys.stdout.write("Creating " + MAGENTA + "RecipesIngredients" + RESET + " table... ")
	try:
		cursor.execute(ingredients_table_command)
		sys.stdout.write(GREEN + "OK" + RESET + "\n")
	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")

	# Insert ingredients from file
	ingredients_file = open(ingredients_filename, "r")
	ingredients_list = ast.literal_eval(ingredients_file.read())
	ingredients_file.close()
	i = 1
	for ingredient in ingredients_list:
		index_format = "%" + str(len(str(len(ingredients_list)))) + "d"
		indicator = "["+index_format+"/"+index_format+"]"
		sys.stdout.write(indicator%(i, len(ingredients_list)) + " Inserting " + YELLOW + ingredient + RESET + " into " + MAGENTA + "RecipesIngredients" + RESET + "... ")
		try:
			cursor.execute(ingredients_insert_command, {"name" : ingredient})
			sys.stdout.write(GREEN + "OK" + RESET + "\n")
		except mysql.connector.Error as err:
			sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")
		i += 1

	cnx.commit()
	cursor.close()
	cnx.close()

def add_recipes_in_db():
	recipes_table_command = (
    	"CREATE TABLE `Recipes` ("
    	"  `id` int(11) NOT NULL AUTO_INCREMENT,"
    	"  `rating` varchar(16) NOT NULL,"
    	"  `n_ratings` varchar(16) NOT NULL,"
    	"  `calories` varchar(16) NOT NULL,"
    	"  `fatContent` varchar(16) NOT NULL,"
    	"  `carbsContent` varchar(16) NOT NULL,"
    	"  `cholesterolContent` varchar(16) NOT NULL,"
    	"  `proteinContent` varchar(16) NOT NULL,"
    	"  `url` varchar(255) NOT NULL,"
    	"  `thumbnail` varchar(255) NOT NULL,"
    	"  `title` varchar(255) NOT NULL,"
    	"  PRIMARY KEY (`id`)"
    	") ENGINE=InnoDB")

	recipes_insert_command = (
		"INSERT INTO Recipes "
		"(rating, n_ratings, calories, fatContent,"
		" carbsContent, cholesterolContent, proteinContent,"
		" url, thumbnail, title) "
		"VALUES (%(rating)s, %(n_ratings)s, %(calories)s, %(fatContent)s,"
		" %(carbohydrateContent)s, %(cholesterolContent)s, %(proteinContent)s,"
		" %(href)s, %(thumbnail)s, %(title)s)")

	recipes_ingredients_lists_table_command = (	# Many-to-many recipes <-> ingredients
    	"CREATE TABLE `RecipesIngredientsLists` ("
    	"  `id` int(11) NOT NULL AUTO_INCREMENT,"
    	"  `ingredient_id` int(11) NULL DEFAULT NULL,"
    	"  `recipe_id` int(11) NULL DEFAULT NULL,"
    	"  PRIMARY KEY (`id`)"
    	") ENGINE=InnoDB")

	ingredient_id_foreign_key_command = (
		"ALTER TABLE `RecipesIngredientsLists` ADD FOREIGN KEY (ingredient_id)\
		REFERENCES `Ingredients` (`id`)")

	recipe_id_foreign_key_command = (
		"ALTER TABLE `RecipesIngredientsLists` ADD FOREIGN KEY (recipe_id)\
		REFERENCES `Recipes` (`id`)")

	recipes_ingredients_binding_command = (
		"INSERT INTO RecipesIngredientsLists"
		" (ingredient_id, recipe_id) VALUES ")

	recipes_ingredients_ids = "((SELECT id from Ingredients WHERE name=%(ingredient_name)s), (SELECT id from Recipes WHERE url=%(recipe_url)s))"


	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()

	# Create Recipes table
	sys.stdout.write("Creating " + MAGENTA + "Recipes" + RESET + " table... ")
	try:
		cursor.execute(recipes_table_command)
		sys.stdout.write(GREEN + "OK" + RESET + "\n")
	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")

	# Create RecipesIngredientsLists table
	sys.stdout.write("Creating " + MAGENTA + "RecipesIngredientsLists" + RESET + " table... ")
	try:
		cursor.execute(recipes_ingredients_lists_table_command)
		cursor.execute(recipe_id_foreign_key_command)
		cursor.execute(ingredient_id_foreign_key_command)
		sys.stdout.write(GREEN + "OK" + RESET + "\n")
	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")

	# Insert Recipes into table
	recipes_file = open(recipes_filename, "r")
	recipes_list = ast.literal_eval(recipes_file.read())
	recipes_file.close()
	i = 1
	for recipe in recipes_list:
		raw_ingredients_list = recipe.pop("ingredients", None).split(", ") # Remove ingredients key to use it in RecipesIngredientsLists insert
		unique_ingredients_list = []
		for ingredient in raw_ingredients_list:
			if ingredient not in unique_ingredients_list:
				unique_ingredients_list.append(ingredient)

		index_format = "%" + str(len(str(len(recipes_list)))) + "d"
		indicator = "["+index_format+"/"+index_format+"]"
		sys.stdout.write(indicator%(i, len(recipes_list)) + " Inserting " + YELLOW + recipe["title"] + RESET + " into " + MAGENTA + "Recipes" + RESET + "... ")
		try:
			cursor.execute(recipes_insert_command, recipe)
			preparedBindingInsert = recipes_ingredients_binding_command 
			for ingredient in unique_ingredients_list:
				preparedBindingInsert += recipes_ingredients_ids%{"ingredient_name" : ingredient, "recipe_url" : recipe["href"]}
				preparedBindingInsert += ", "
			cursor.execute(preparedBindingInsert[:-2],multi=True)
			sys.stdout.write(GREEN + "OK" + RESET + "\n")
		except mysql.connector.Error as err:
			sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")
		i += 1


	cnx.commit()
	cursor.close()
	cnx.close()

if __name__ == '__main__':
	drop_added_tables_in_db()
	add_ingredients_in_db()
	add_recipes_in_db()

