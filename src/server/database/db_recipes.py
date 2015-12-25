#!/usr/bin/python

import sys
import ast

try:
	import mysql.connector
	from mysql.connector import errorcode

except: # no mysql.connector
	print "Error : need MySQL python connector (provided by Oracle at https://dev.mysql.com/downloads/connector/python/)"
	sys.exit()

BLUE = "\033[34m"
CYAN = "\033[36m"
GREEN = "\033[32m"
RED = "\033[31m"
RESET = "\033[0m"

db_name = "db_appli"
db_properties_filename = "../dao/dao.properties"
ingredients_filename = "db_ingredients.txt"
recipes_filename = "db_recipes.txt"

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
	if tables == None : tables = ["RecipesIngredients"]
	drop_command = ("DROP TABLE `%s`")

	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()
	for table in tables:
		cursor.execute(drop_command%table)

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
	sys.stdout.write("Creating " + BLUE + "RecipesIngredients" + RESET + " table... ")
	try:
		cursor.execute(ingredients_table_command)
		sys.stdout.write(GREEN + "OK" + RESET + "\n")
	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")

	# Insert ingredients from file
	ingredients_file = open(ingredients_filename, "r")
	ingredients_list = ast.literal_eval(ingredients_file.read())
	ingredients_file.close()
	for ingredient in ingredients_list:
		sys.stdout.write("Inserting " + CYAN + ingredient + RESET + " into " + BLUE + "RecipesIngredients" + RESET + "... ")
		try:
			cursor.execute(ingredients_insert_command, {"name" : ingredient})
			sys.stdout.write(GREEN + "OK" + RESET + "\n")
		except mysql.connector.Error as err:
			sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")

	cnx.commit()
	cursor.close()
	cnx.close()

def add_recipes_in_db():
	recipes_table_command = (
    	"CREATE TABLE `Recipes` ("
    	"  `id` int(11) NOT NULL AUTO_INCREMENT,"
    	"  `rating` decimal(3,2) NOT NULL,"
    	"  `n_ratings` int(11) NOT NULL,"
    	"  `calories` varchar(16) NOT NULL,"
    	"  `fatContent` varchar(16) NOT NULL,"
    	"  `carbsContent` varchar(16) NOT NULL,"
    	"  `cholesterolContent` varchar(16) NOT NULL,"
    	"  `proteinContent` varchar(16) NOT NULL,"
    	"  `url` varchar(255) NOT NULL,"
    	"  `thumbnail` varchar(255) NOT NULL,"
    	"  `title` varchar(100) NOT NULL,"
    	"  PRIMARY KEY (`id`)"
    	") ENGINE=InnoDB")

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

	# Create Recipes table
	sys.stdout.write("Creating " + BLUE + "Recipes" + RESET + " table... ")
	try:
		cursor.execute(recipes_table_command)
		sys.stdout.write(GREEN + "OK" + RESET + "\n")
	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")


drop_added_tables_in_db()
add_ingredients_in_db()

