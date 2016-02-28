#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys
import requests
import ast
import re
import codecs
import base64
import threading
from time import sleep
import traceback

from bs4 import BeautifulSoup

domain = "https://colruyt.collectandgo.be"
cookie_setup_url = domain + "/cogo/homepage"
search_by_branch_url = domain + "/cogo/fr/branch/"

last_cookies_filename = "last_cookies.txt"
results_filename = "results_branch_articles.txt"
branch_status_filename = "branch_status.txt"
error_messages_filename = "errors.txt"

init_branch_index = 2
total_threads = 1


class Article:
	BRANCH_INDEX_KEY = "branch_index"
	IMAGE_KEY = "image_url"
	PRICE_UNIT_KEY = "price_unit"
	PRICE_KG_KEY = "price_kg"
	DETAILS_KEY = "details_url"
	CATEGORIES_KEY = "categories"
	WEIRD_NAME_KEY = "weird_name"
	SHORT_DESCRIPTION_KEY = "short_description"
	FULL_DESCRIPTION_KEY = "full_description"

	def __init__(self, branch, image, price_u, price_kg, details_url, categories, name, short, full):
		self.branch_index = branch
		self.image_url = image
		self.price_unit = price_u
		self.price_kg = price_kg
		self.details_url = details_url
		self.categories = categories
		self.name = name
		self.short_description = short
		self.full_description = full

	def __repr__(self):
		return self.toDict().__repr__()

	def toDict(self):
		return {
			Article.BRANCH_INDEX_KEY : self.branch_index,
			Article.IMAGE_KEY : self.image_url,
			Article.PRICE_UNIT_KEY : self.price_unit,
			Article.PRICE_KG_KEY : self.price_kg,
			Article.DETAILS_KEY : self.details_url,
			Article.CATEGORIES_KEY : self.categories,
			Article.WEIRD_NAME_KEY : self.name,
			Article.SHORT_DESCRIPTION_KEY : self.short_description,
			Article.FULL_DESCRIPTION_KEY : self.full_description

		}

	@classmethod
	def fromDict(cls, data_dict):
		return cls(data_dict[BRANCH_INDEX_KEY], data_dict[Article.IMAGE_KEY], \
			data_dict[Article.PRICE_UNIT_KEY], \
			data_dict[Article.PRICE_KG_KEY], data_dict[Article.DETAILS_KEY], \
			data_dict[Article.CATEGORIES_KEY], data_dict[Article.WEIRD_NAME_KEY], \
			data_dict[Article.SHORT_DESCRIPTION_KEY], data_dict[Article.FULL_DESCRIPTION_KEY])


class ParserWorker(threading.Thread):
	index_lock = threading.Lock()
	file_lock = threading.Lock()
	branch_status_lock = threading.Lock()
	error_write_lock = threading.Lock()

	branch_index = init_branch_index
	status_OK = "ok_index"
	status_FAIL = "fail_index"
	branch_status = { status_OK : [], status_FAIL : [] }

	def __init__(self):
		threading.Thread.__init__(self)
		# Need cookies else Colruyt sends a 'Service Unavailable' reponse
		self.cookies = requests.get(cookie_setup_url).cookies 

	@staticmethod
	def nextBranchIndex():
		ParserWorker.index_lock.acquire()
		acquired_index = ParserWorker.branch_index
		ParserWorker.branch_index += 1
		ParserWorker.index_lock.release()
		return acquired_index

	@staticmethod
	def branchIndex():
		ParserWorker.index_lock.acquire()
		index = ParserWorker.branch_index
		ParserWorker.index_lock.release()
		return index

	@staticmethod
	def setBranchIndex(new_index):
		ParserWorker.index_lock.acquire()
		try:
			ParserWorker.branch_index = new_index
		except Exception as e:
			raise e
		finally:
			ParserWorker.index_lock.release()

	@staticmethod
	def addBranchStatus(index, status_code):
		ParserWorker.branch_status_lock.acquire()
		try:
			ParserWorker.branch_status[status_code].append(index)
		except Exception as e:
			raise e
		finally:
			ParserWorker.branch_status_lock.release()

	@staticmethod
	def saveBranchArticles(articles):
		ParserWorker.file_lock.acquire()
		try:
			with open(results_filename,"a") as f:
				for article in articles:
					f.write(repr(article))
					f.write("\n")
		except Exception as e:
			raise e
		finally:
			ParserWorker.file_lock.release()

	@staticmethod
	def writeError(e, branch):
		ParserWorker.error_write_lock.acquire()
		try :
			with open(error_messages_filename,"a") as f:
				f.write(\
					"-" * 40 \
					+ "\n" + str(type(e)) + " : " + str(e) + \
					"\nFOR ID -> " + str(branch)+"\n")
				f.write(">>> FORMAT <<<\n")
				traceback.print_exc(file=f)
				f.write(">>> END FORMAT <<<\n" + "-" *40)
		except Exception as e:
			raise e 
		finally:
			ParserWorker.error_write_lock.release()

	def run(self):
		self.running = True
		while(self.running):
			
			branch_index = ParserWorker.nextBranchIndex()
			print "Trying -> " + str(branch_index)
			try:
				self.parse_all_articles_from_branch(branch_index)
			except Exception as e:
				ParserWorker.writeError(e, branch_index)

	def stop(self):
		self.running = False

	def parse_all_articles_from_branch(self, branch_index):
		# HTTP GET and use BeautifulSoup to structure HTML tree
		raw_response = requests.get(search_by_branch_url + str(branch_index), cookies=self.cookies)
		structured_response = BeautifulSoup(raw_response.content, "lxml")

		# Check first if response is not empty
		if structured_response.find("Pas de produits"):
			ParserWorker.addBranchStatus(branch_index, ParserWorker.status_FAIL)
			return 
		ParserWorker.addBranchStatus(branch_index, ParserWorker.status_OK)
		articles = []

		# All attributes
		image_url = None; price_unit = None; price_kg = None; details_url = None; categories=[];
		weird_name = None; short_description = None; full_description = None;

		# Find categories
		script_container = structured_response.body.script.string
		if "utag_data" in script_container :
			open_bracket_index = script_container.find("{")
			close_bracket_index = script_container.find("}")
			script_dict = script_container[open_bracket_index:close_bracket_index+1]
			for elem in script_dict.split(","):
				if "page_cat" in elem:
					categories.append(elem.split(":")[1].strip(" ").strip("'"))

		# Iterate on each article/product
		for article in structured_response.find("div",attrs={"id":"articles"}).children:
			# Image url
			article_image_wrap = article.find("div",attrs={"class":"imageWrap"})
			if(article_image_wrap != None):
				image_url = domain + str(article_image_wrap.find("img").attrs["src"]) 


			# Prod infos
			articles_infos_wrap = article.find("a", attrs={"class":"prodInfo"})
			if(articles_infos_wrap != None):
				if("href" in articles_infos_wrap.attrs) : details_url = domain + articles_infos_wrap.attrs["href"]
				# Weird name
				weird_name_tag = articles_infos_wrap.find("span", attrs={"name"})
				if weird_name_tag != None : weird_name = weird_name_tag.string
				# Short description
				short_description_tag = articles_infos_wrap.find("span", attrs={"description"})
				if short_description_tag != None : short_description = short_description_tag.string
				# Full description
				full_description_tag = articles_infos_wrap.find("p", attrs={"fullDescription"})
				if full_description_tag != None : full_description = full_description_tag.string

			# Price 
			article_price_div = article.find("div", attrs={"class":"price"})
			if article_price_div != None:
				article_price_unit = article_price_div.find("p", attrs={"class":"piece"})
				if article_price_unit != None:
					script_tag = article_price_unit.find("script")
					if script_tag != None:
						try :
							price_unit = base64.b64decode(script_tag.string.split(",")[0].split("(")[1].strip("'"))
						except :
							sys.stdout.write("Price Unit not parsed\n")
					

				article_price_kg = article_price_div.find("p", attrs={"class":"unit"})
				if article_price_kg != None:
					script_tag = article_price_kg.find("script")
					if script_tag != None:
						try :
							price_kg = base64.b64decode(script_tag.string.split(",")[0].split("(")[1].strip("'"))
						except :
							sys.stdout.write("Price kg not parsed\n")
			parsed_article = Article(branch_index, image_url, price_unit, price_kg, details_url, categories, weird_name, short_description, full_description)
			articles.append(parsed_article)

		ParserWorker.saveBranchArticles(articles)

def onExit(workers):
	# Stop threads
	print "stopping workers"
	for worker in workers:
		worker.stop()
		worker.join()
	print "stopped workers"

	# Save used branch index
	with open(branch_status_filename, "w+") as f:
		f.write(str(ParserWorker.branch_status)+"\n")

if __name__ == "__main__" :
	last_branch_index = 1
	try:
		with open(branch_status_filename,"r") as f:
			all_branch_status = ast.literal_eval(f.read())
			ParserWorker.branch_status = all_branch_status
			size_OK = len(all_branch_status[ParserWorker.status_OK])
			size_FAIL = len(all_branch_status[ParserWorker.status_FAIL])
			if size_OK == 0 and size_FAIL != 0 :
				last_branch_index = all_branch_status[ParserWorker.status_FAIL][-1]
			elif size_OK != 0 and size_FAIL == 0 :
				last_branch_index = all_branch_status[ParserWorker.status_OK][-1]
			elif size_OK != 0 and size_FAIL != 0 :
				last_branch_index = max([all_branch_status[ParserWorker.status_OK][-1], all_branch_status[ParserWorker.status_FAIL][-1]])
			sys.stdout.write("Last checked branch index was "+str(last_branch_index)+".\nDo you want to continue with this index ? (y/n)")
			user_input = raw_input().lower()
			if user_input == "n" :
				while not user_input.isdigit():
					sys.stdout.write("Enter new index : ")
					user_input = raw_input()
				last_branch_index = int(user_input) - 1
				sys.stdout.write("New branch index set at " + user_input + '\n')
	except IOError:
		pass

	ParserWorker.branch_index = last_branch_index + 1
	workers = [ParserWorker() for i in range(total_threads)]
	try:
		for worker in workers :
			worker.start()

		stop = False
		while not stop :
			sys.stdout.write("Type help for a list of command.\n")
			sys.stdout.write(">> ")
			user_input = raw_input().lower()
			if user_input == "stop" or user_input == "exit":
				stop = True
				onExit(workers)
			elif user_input == "index" :
				sys.stdout.write("Current branch index : " + str(ParserWorker.branchIndex()) + "\n")
			elif user_input == "set" :
				sys.stdout.write("Set new branch index : ")
				new_branch = raw_input()
				if(new_branch.isdigit()):
					previous = ParserWorker.setBranchIndex(int(new_branch))
					sys.stdout.write(str(previous) + " -> " + new_branch + "\n")
				else:
					sys.stdout.write(new_branch + " is not a digit.\n")
			elif user_input == "help" :
				sys.stdout.write("exit/stop : terminate all workers and exit\n")
				sys.stdout.write("index     : get current branch index\n")
				sys.stdout.write("set       : set new branch index\n")
	except:
		sys.stdout.write("Unexpected error : " + str(sys.exc_info()[0]))
		onExit(workers)




