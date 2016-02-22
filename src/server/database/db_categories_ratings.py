#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys
import ast

try:
	import mysql.connector
	from mysql.connector import errorcode

except: # no mysql.connector
	print "Error : import impossible (MySQL python connector). Sur ubuntu, suivre \
	'Installing Connector/Python on Linux Using a Debian Package' sur \
	https://dev.mysql.com/doc/connector-python/en/connector-python-installation-binary.html"
	sys.exit()

YELLOW = "\033[33m"
MAGENTA = "\033[35m"
GREEN = "\033[32m"
RED = "\033[31m"
RESET = "\033[0m"

db_name = "db_appli"
db_properties_filename = "../dao/dao.properties"

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
	if tables == None : tables = ["CategoriesRatings"]
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

def add_categories_ratings_in_db():
	categories_ratings_tabe_command = (
    	"CREATE TABLE `CategoriesRatings` ("
    	"  `id` int(11) NOT NULL AUTO_INCREMENT,"
    	"  `category_name` varchar(100) NOT NULL,"
    	"  `rating` DECIMAL(2,1) NOT NULL,"
    	"  `n_ratings` int(11) NULL DEFAULT NULL,"
    	"  `user_id` INT UNSIGNED NOT NULL,"
    	"  PRIMARY KEY (`id`)"
    	") ENGINE=InnoDB")

	user_id_foreign_key_command = (
		"ALTER TABLE `CategoriesRatings` ADD FOREIGN KEY (user_id)\
		REFERENCES `User` (`id_user`)")

	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()

	# Create CategoriesRatings table
	sys.stdout.write("Creating " + MAGENTA + "CategoriesRatings" + RESET + " table... ")
	try:
		cursor.execute(categories_ratings_tabe_command)
		cursor.execute(user_id_foreign_key_command)
		sys.stdout.write(GREEN + "OK" + RESET + "\n")
	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")

	cnx.commit()
	cursor.close()
	cnx.close()


if __name__ == '__main__':
	drop_added_tables_in_db()
	add_categories_ratings_in_db()





