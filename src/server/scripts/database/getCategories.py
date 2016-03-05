#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys

import mysql.connector

YELLOW = "\033[33m"
MAGENTA = "\033[35m"
GREEN = "\033[32m"
RED = "\033[31m"
RESET = "\033[0m"

db_name = "db_colruyt"
db_properties_filename = "../../src/main/resources/dao.properties"
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


def addAllCategoriesTable():
	command = (
		"CREATE TABLE `All_categories` ("
		"id INT UNSIGNED NOT NULL AUTO_INCREMENT,"
		"category_name VARCHAR(255) NOT NULL,"
		"table_name VARCHAR(16) NOT NULL,"
		"UNIQUE(category_name),"
		"PRIMARY KEY (`id`)"
    	") ENGINE=InnoDB")
	(username, password)=db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()
	sys.stdout.write("Creating " + MAGENTA + "All_categories" + RESET + " table... ")
	try:
		cursor.execute(command)
		sys.stdout.write(GREEN + "OK! " + RESET)
	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET)
	sys.stdout.write("\n")
	cnx.commit()
	cursor.close()
	cnx.close()

def addToAllCategoriesTable(category):
	command = (
		"INSERT INTO All_categories"
		"(category_name, table_name)"
		"VALUES (%s , %s)"
		)
	(username, password)=db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()

	try:
		cursor.execute(command, (category, "Food"))
	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET)
	cnx.commit()
	cursor.close()
	cnx.close()

def drop_added_table_in_db(table=None):
	if table == None : table = "All_categories"
	drop_command = ("DROP TABLE `%s`")

	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()
	cursor.execute(("SET FOREIGN_KEY_CHECKS=0"))
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


def writeAllCategories():
	firstCategories = []
	command = " SELECT `categories` FROM `Food`;"
	(username,password) = db_params()
	cnx = mysql.connector.connect(user=username,database=db_name,password=password,charset="utf8",use_unicode=True)
	cursor = cnx.cursor()
	sys.stdout.write("Trying to select info from DB ..")
	try:
		cursor.execute(command)
		sys.stdout.write(GREEN + " -> OK" + RESET + "\n")
		sys.stdout.write("Gettin all the categories and putting them in DB.. ")
		for categoriesInfo in cursor:
			categoriesInfo=categoriesInfo[0].split(",")
			primaryCat=categoriesInfo[0]
			secondaryCat=categoriesInfo[1]
			if primaryCat not in firstCategories:
				firstCategories.append(primaryCat)
				addToAllCategoriesTable(primaryCat)
		sys.stdout.write(GREEN + " -> DONE!" + RESET + "\n")
	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")
	cnx.commit()
	cursor.close()
	cnx.close()

if __name__=="__main__":
	drop_added_table_in_db()
	addAllCategoriesTable()
	writeAllCategories()