#!/usr/bin/python
# -*- coding: UTF-8 -*-

import mysql.connector
import ast
import sys

from mysql.connector import errorcode

YELLOW = "\033[33m"
MAGENTA = "\033[35m"
GREEN = "\033[32m"
RED = "\033[31m"
RESET = "\033[0m"

db_name = "db_colruyt"
db_properties_filename = "../../src/main/resources/dao.properties"

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

def addCheckedColumnToUsers_history():
	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()

	command = "ALTER TABLE `Users_history` ADD checked INT(1) NOT NULL"
	
	sys.stdout.write("Creating " + MAGENTA + "checked column" + RESET + " for Users_history... ")
	try:
		cursor.execute(command)
		sys.stdout.write(GREEN + "OK" + RESET + "\n")

	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")
	cnx.commit()
	cursor.close()
	cnx.close()



if __name__ == "__main__":
	addCheckedColumnToUsers_history()