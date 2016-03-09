#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys
import ast
import requests
import requests.exceptions

try:
	import mysql.connector
	from mysql.connector import errorcode

except: # no mysql.connector
	print "Error : import impossible (MySQL python connector). Sur ubuntu, suivre \
	'Installing Connector/Python on Linux Using a Debian Package' sur \
	https://dev.mysql.com/doc/connector-python/en/connector-python-installation-binary.html"
	sys.exit()

MAGENTA = "\033[35m"
GREEN = "\033[32m"
RED = "\033[31m"
RESET = "\033[0m"

db_name = "db_colruyt"
SQL_FILTER_WITH_PRODUCT_NAME = (
		"SELECT `id_food`"
		"FROM `Food`"
		"WHERE `product_name` LIKE "
		)

class PriceCalculator:

	def __init__(self):
		self.


	@staticmethod
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
