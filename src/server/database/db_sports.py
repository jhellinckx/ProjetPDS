#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys

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
sports_filename = "raw/sports_data.txt"
data_char = "@"
data_start = 2
delimiter = ";"

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

def drop_added_table_in_db(table=None):
	if table == None : table = "Sports"
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

def read_data_from_file():
	sports_data = []
	with open(sports_filename, 'r') as f:
		for i in f:
			line = i.strip()
			if (line[0] == data_char):
				line = line[data_start:]
				sports_data.append(line.split(delimiter))
	return sports_data



	
def add_sports_from_file():
	sports_table_command = (
		"CREATE TABLE `Sports` ("
    	"  `name` varchar(100) NOT NULL,"
    	"  `joule_60kg` varchar(25) NOT NULL,"
    	"  `joule_70kg` varchar(25) NOT NULL,"
    	"  `joule_85kg` varchar(25) NOT NULL,"
    	"  PRIMARY KEY (`name`)"
    	") ENGINE=InnoDB")

	sport_insert_command = (
		"INSERT INTO Sports "
		"(name, joule_60kg, joule_70kg, joule_85kg) "
		"VALUES (%(name)s, %(joule_60kg)s, %(joule_70kg)s, %(joule_85kg)s)")

	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()

	# Create RecipesIngredients table
	sys.stdout.write("Creating " + MAGENTA + "Sports" + RESET + " table... ")
	try:
		cursor.execute(sports_table_command)
		sys.stdout.write(GREEN + "OK" + RESET + "\n")
	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")

	# Insert sports from file

	sports_data = read_data_from_file()
	i = 1
	for data_list in sports_data:
		index_format = "%" + str(len(str(len(sports_data)))) + "d"
		indicator = "["+index_format+"/"+index_format+"]"
		sys.stdout.write(indicator%(i, len(sports_data)) + " Inserting " + YELLOW + str(data_list) + RESET + " into " + MAGENTA + "Sports" + RESET + "... ")
		try:
			sport_data = {'name': data_list[0], 'joule_60kg': data_list[1], 'joule_70kg': data_list[2],'joule_85kg': data_list[3],}
			cursor.execute(sport_insert_command, sport_data)
			sys.stdout.write(GREEN + "OK" + RESET + "\n")
		except mysql.connector.Error as err:
			sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")
		i += 1

	cnx.commit()
	cursor.close()
	cnx.close()







if __name__ == '__main__':
	drop_added_table_in_db()
	add_sports_from_file()