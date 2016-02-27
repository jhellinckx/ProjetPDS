#!/usr/bin/python
import sys
import requests
import ast
from bs4 import BeautifulSoup

domain = "https://colruyt.collectandgo.be"
cookie_setup_url = domain + "/cogo/homepage"
last_cookies_filename = "last_cookies.txt"

search_by_branch_url = domain + "/cogo/fr/branch/"

def all_items_from_branch(branch_index):
	# Always get cookies
	cookies = requests.get(cookie_setup_url).cookies
	structured_response = BeautifulSoup(requests.get(search_by_branch_url + str(branch_index), cookies=cookies).content, "lxml")
	articles = []
	article_repr = {
		"image_url"	:	None,
		"prix_unit" : 	None,
		"prix_kg"	: 	None,
		"details_url" :	None,
		
	}
	#sys.stdout.write(structured_response.body.script.string + "\n")
	for article in structured_response.find("div",attrs={"id":"articles"}).children:


	with open(last_cookies_filename,"w+") as f:
		f.write(str(structured_response.cookies))


if __name__ == "__main__" : 
	all_items_from_branch(2)


