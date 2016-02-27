#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys
import requests
import ast
import re
import codecs
import base64

from time import sleep

from bs4 import BeautifulSoup

domain = "https://colruyt.collectandgo.be"
cookie_setup_url = domain + "/cogo/homepage"
last_cookies_filename = "last_cookies.txt"

search_by_branch_url = domain + "/cogo/fr/branch/"

class Article:
	IMAGE_KEY = "image_url"
	PRICE_UNIT_KEY = "price_unit"
	PRICE_KG_KEY = "price_kg"
	DETAILS_KEY = "details_url"
	CATEGORIES_KEY = "categories"

	def __init__(self, image, price_u, price_kg, details_url, categories):
		self.image_url = image
		self.price_unit = price_u
		self.price_kg = price_kg
		self.details_url = details_url
		self.categories = categories

	def toDict():
		return {
			IMAGE_KEY : self.image_url,
			PRICE_UNIT_KEY : self.price_unit,
			PRICE_KG_KEY : self.price_kg,
			DETAILS_KEY : self.details_url,
			CATEGORIES_KEY : self.categories
		}

	@classmethod
	def fromDict(cls, data_dict):
		return cls(data_dict[IMAGE_KEY], data_dict[PRICE_UNIT_KEY], \
			data_dict[PRICE_KG_KEY], data_dict[DETAILS_KEY], data_dict[CATEGORIES_KEY])

def all_items_from_branch(branch_index):
	# Always get cookies
	cookies = requests.get(cookie_setup_url).cookies
	raw_response = requests.get(search_by_branch_url + str(branch_index), cookies=cookies)
	structured_response = BeautifulSoup(raw_response.content, "lxml")
	articles = []

	# All attributes
	image_url = None; price_unit = None; price_kg = None; details_url = None; categories=[];

	# Find categories
	script_container = structured_response.body.script.string
	if "utag_data" in script_container :
		open_bracket_index = script_container.find("{")
		close_bracket_index = script_container.find("}")
		script_dict = script_container[open_bracket_index:close_bracket_index+1]
		for elem in script_dict.split(","):
			if "page_cat" in elem:
				categories.append(elem.split(":")[1].strip(" ").strip("'"))
	# Find rest
	for article in structured_response.find("div",attrs={"id":"articles"}).children:
		# Image url
		article_image_wrap = article.find("div",attrs={"class":"imageWrap"})
		if(article_image_wrap != None):
			image_url = domain + str(article_image_wrap.find("img").attrs["src"]) 

		# Price 
		article_price_div = article.find("div", attrs={"class":"price"})
		if article_price_div != None:
			article_price_unit = article_price_div.find("p", attrs={"class":"piece"})
			if article_price_unit != None:
				script_tag = article_price_unit.find("script")
				if script_tag != None:
					print base64.b64decode(script_tag.string.split(",")[0].split("(")[1].strip("'"))
				

			article_price_kg = article_price_div.find("p", attrs={"class":"unit"})
			if article_price_kg != None:
				script_tag = article_price_kg.find("script")
				if script_tag != None:
					print base64.b64decode(script_tag.string.split(",")[0].split("(")[1].strip("'"))
				
		print("-"*5)
	with open(last_cookies_filename,"w+") as f:
		f.write(str(cookies))


if __name__ == "__main__" : 
	all_items_from_branch(2)


