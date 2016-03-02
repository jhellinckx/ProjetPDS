#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys
import ast
import mysql.connector
from mysql.connector import errorcode
from colruyt_parser import *


YELLOW = "\033[33m"
MAGENTA = "\033[35m"
GREEN = "\033[32m"
RED = "\033[31m"
RESET = "\033[0m"

db_name = "db_colruyt"
db_encoding = "utf8"
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

def create_db():
	(username, password) = db_params()
	drop_command = "DROP DATABASE " + db_name + ";"
	create_command = "CREATE DATABASE " + db_name + " CHARACTER SET " + db_encoding + ";"
	cnx = mysql.connector.connect(user=username, password=password)
	cursor = cnx.cursor()
	try:
		cursor.execute(drop_command)
	except mysql.connector.Error as err:
	    if err.errno == errorcode.ER_BAD_DB_ERROR:
	        sys.stdout.write("Could not drop database " + MAGENTA + db_name + RESET + "\n")
	    else:
	        print(err)
	        exit(1)
	finally:
		sys.stdout.write("Creating database " + MAGENTA + db_name + RESET + "... ")
		cursor.execute(create_command)
		sys.stdout.write(GREEN + "OK " + RESET)
	cnx.commit()
	cursor.close()
	cnx.close()

if __name__ == "__main__" :
	create_db()