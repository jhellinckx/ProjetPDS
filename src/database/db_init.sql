DROP DATABASE db_appli;
CREATE DATABASE db_appli;
USE db_appli;


CREATE TABLE db_appli.User(
	id_user INT UNSIGNED NOT NULL AUTO_INCREMENT,
	username VARCHAR(60) NOT NULL,
	gender VARCHAR(1) NOT NULL,
	PRIMARY KEY(id_user)
) ENGINE=INNODB;

ALTER TABLE User
ADD INDEX ind_username (username);



CREATE TABLE db_appli.Food(
	id_food INT UNSIGNED NOT NULL AUTO_INCREMENT,
	code VARCHAR(255) NOT NULL,
	url VARCHAR(255) NOT NULL,
	creator VARCHAR(255),
	created_t VARCHAR(255),
	created_datetime VARCHAR(50),
	last_modified_t VARCHAR(255),
	last_modified_datetime VARCHAR(50),
	product_name VARCHAR(255),
	generic_name VARCHAR(255),
	quantity VARCHAR(255),
	packaging VARCHAR(255),
	packaging_tags VARCHAR(255),
	brands VARCHAR(255),
	brands_tags VARCHAR(255),
	categories TEXT,
	categories_tags TEXT,
	categories_en TEXT,
	origins TEXT,
	origins_tags TEXT,
	manufacturing_places TEXT,
	manufacturing_places_tags TEXT,
	labels TEXT,
	labels_tags TEXT,
	labels_en TEXT,
	emb_codes TEXT,
	emb_codes_tags TEXT,
	first_packaging_code_geo VARCHAR(255),
	cities VARCHAR(255),
	cities_tags VARCHAR(255),
	purchase_places VARCHAR(255),
	stores VARCHAR(255),
	countries VARCHAR(255),
	countries_tags VARCHAR(255),
	countries_en VARCHAR(255),
	ingredients_text TEXT,
	allergens TEXT,
	allergens_en TEXT,
	traces VARCHAR(255),
	traces_tags VARCHAR(255),
	traces_en VARCHAR(255),
	serving_size VARCHAR(255),
	no_nutriments VARCHAR(255),
	additives_n VARCHAR(255),
	additives VARCHAR(255),
	additives_tags VARCHAR(255),
	additives_en TEXT,
	ingredients_from_palm_oil_n VARCHAR(255),
	ingredients_from_palm_oil VARCHAR(255),
	ingredients_from_palm_oil_tags VARCHAR(255),
	ingredients_that_may_be_from_palm_oil_n VARCHAR(255),
	ingredients_that_may_be_from_palm_oil VARCHAR(255),
	ingredients_that_may_be_from_palm_oil_tags TEXT,
	nutrition_grade_uk VARCHAR(20),
	nutrition_grade_fr VARCHAR(20),
	pnns_groups_1 VARCHAR(255),
	pnns_groups_2 VARCHAR(255),
	states TEXT,
	states_tags TEXT,
	states_en TEXT,
	main_category VARCHAR(255),
	main_category_en VARCHAR(255),
	image_url VARCHAR(255),
	image_small_url VARCHAR(255),
	energy_100g VARCHAR(10),
	energy_from_fat_100g VARCHAR(10),
	fat_100g VARCHAR(10),
	saturated_fat_100g VARCHAR(10),
	butyric_acid_100g VARCHAR(10),
	caproic_acid_100g VARCHAR(10),
	caprylic_acid_100g VARCHAR(10),
	capric_acid_100g VARCHAR(10),
	lauric_acid_100g VARCHAR(10),
	myristic_acid_100g VARCHAR(10),
	palmitic_acid_100g VARCHAR(10),
	stearic_acid_100g VARCHAR(10),
	arachidic_acid_100g VARCHAR(10),
	behenic_acid_100g VARCHAR(10),
	lignoceric_acid_100g VARCHAR(10),
	cerotic_acid_100g VARCHAR(10),
	montanic_acid_100g VARCHAR(10),
	melissic_acid_100g VARCHAR(10),
	monounsaturated_fat_100g VARCHAR(10),
	polyunsaturated_fat_100g VARCHAR(10),
	omega_3_fat_100g VARCHAR(10),
	alpha_linolenic_acid_100g VARCHAR(10),
	eicosapentaenoic_acid_100g VARCHAR(10),
	docosahexaenoic_acid_100g VARCHAR(10),
	omega_6_fat_100g VARCHAR(10),
	linoleic_acid_100g VARCHAR(10),
	arachidonic_acid_100g VARCHAR(10),
	gamma_linolenic_acid_100g VARCHAR(10),
	dihomo_gamma_linolenic_acid_100g VARCHAR(10),
	omega_9_fat_100g VARCHAR(10),
	oleic_acid_100g VARCHAR(10),
	elaidic_acid_100g VARCHAR(10),
	gondoic_acid_100g VARCHAR(10),
	mead_acid_100g VARCHAR(10),
	erucic_acid_100g VARCHAR(10),
	nervonic_acid_100g VARCHAR(10),
	trans_fat_100g VARCHAR(10),
	cholesterol_100g VARCHAR(10),
	carbohydrates_100g VARCHAR(10),
	sugars_100g VARCHAR(10),
	sucrose_100g VARCHAR(10),
	glucose_100g VARCHAR(10),
	fructose_100g VARCHAR(10),
	lactose_100g VARCHAR(10),
	maltose_100g VARCHAR(10),
	maltodextrins_100g VARCHAR(10),
	starch_100g VARCHAR(10),
	polyols_100g VARCHAR(10),
	fiber_100g VARCHAR(10),
	proteins_100g VARCHAR(10),
	casein_100g VARCHAR(10),
	serum_proteins_100g VARCHAR(10),
	nucleotides_100g VARCHAR(10),
	salt_100g VARCHAR(255),
	sodium_100g VARCHAR(255),
	alcohol_100g VARCHAR(10),
	vitamin_a_100g VARCHAR(10),
	vitamin_d_100g VARCHAR(10),
	vitamin_e_100g VARCHAR(10),
	vitamin_k_100g VARCHAR(10),
	vitamin_c_100g VARCHAR(10),
	vitamin_b1_100g VARCHAR(10),
	vitamin_b2_100g VARCHAR(10),
	vitamin_pp_100g VARCHAR(10),
	vitamin_b6_100g VARCHAR(10),
	vitamin_b9_100g VARCHAR(10),
	vitamin_b12_100g VARCHAR(10),
	biotin_100g VARCHAR(10),
	pantothenic_acid_100g VARCHAR(10),
	silica_100g VARCHAR(10),
	bicarbonate_100g VARCHAR(10),
	potassium_100g VARCHAR(10),
	chloride_100g VARCHAR(10),
	calcium_100g VARCHAR(10),
	phosphorus_100g VARCHAR(10),
	iron_100g VARCHAR(10),
	magnesium_100g VARCHAR(10),
	zinc_100g VARCHAR(10),
	copper_100g VARCHAR(10),
	manganese_100g VARCHAR(10),
	fluoride_100g VARCHAR(10),
	selenium_100g VARCHAR(10),
	chromium_100g VARCHAR(10),
	molybdenum_100g VARCHAR(10),
	iodine_100g VARCHAR(10),
	caffeine_100g VARCHAR(10),
	taurine_100g VARCHAR(10),
	ph_100g VARCHAR(10),
	fruits_vegetables_nuts_100g VARCHAR(10),
	collagen_meat_protein_ratio_100g VARCHAR(10),
	cocoa_100g VARCHAR(10),
	chlorophyl_100g VARCHAR(10),
	carbon_footprint_100g VARCHAR(10),
	nutrition_score_fr_100g VARCHAR(10),
	nutrition_score_uk_100g VARCHAR(10),
	PRIMARY KEY (id_food)
)ENGINE=INNODB;


LOAD DATA LOCAL INFILE '/CHEMIN/en.openfoodfacts.org.products.csv' #CHEMIN à modif
INTO TABLE Food
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(code,url,creator,created_t,created_datetime,last_modified_t,last_modified_datetime,product_name,generic_name,quantity,packaging,packaging_tags,brands,brands_tags,categories,categories_tags,categories_en,origins,origins_tags,manufacturing_places,manufacturing_places_tags,labels,labels_tags,labels_en,emb_codes,emb_codes_tags,first_packaging_code_geo,cities,cities_tags,purchase_places,stores,countries,countries_tags,countries_en,ingredients_text,allergens,allergens_en,traces,traces_tags,traces_en,serving_size,no_nutriments,additives_n,additives,additives_tags,additives_en,ingredients_from_palm_oil_n,ingredients_from_palm_oil,ingredients_from_palm_oil_tags,ingredients_that_may_be_from_palm_oil_n,ingredients_that_may_be_from_palm_oil,ingredients_that_may_be_from_palm_oil_tags,nutrition_grade_uk,nutrition_grade_fr,pnns_groups_1,pnns_groups_2,states,states_tags,states_en,main_category,main_category_en,image_url,image_small_url,energy_100g,energy_from_fat_100g,fat_100g,saturated_fat_100g,butyric_acid_100g,caproic_acid_100g,caprylic_acid_100g,capric_acid_100g,lauric_acid_100g,myristic_acid_100g,palmitic_acid_100g,stearic_acid_100g,arachidic_acid_100g,behenic_acid_100g,lignoceric_acid_100g,cerotic_acid_100g,montanic_acid_100g,melissic_acid_100g,monounsaturated_fat_100g,polyunsaturated_fat_100g,omega_3_fat_100g,alpha_linolenic_acid_100g,eicosapentaenoic_acid_100g,docosahexaenoic_acid_100g,omega_6_fat_100g,linoleic_acid_100g,arachidonic_acid_100g,gamma_linolenic_acid_100g,dihomo_gamma_linolenic_acid_100g,omega_9_fat_100g,oleic_acid_100g,elaidic_acid_100g,gondoic_acid_100g,mead_acid_100g,erucic_acid_100g,nervonic_acid_100g,trans_fat_100g,cholesterol_100g,carbohydrates_100g,sugars_100g,sucrose_100g,glucose_100g,fructose_100g,lactose_100g,maltose_100g,maltodextrins_100g,starch_100g,polyols_100g,fiber_100g,proteins_100g,casein_100g,serum_proteins_100g,nucleotides_100g,salt_100g,sodium_100g,alcohol_100g,vitamin_a_100g,vitamin_d_100g,vitamin_e_100g,vitamin_k_100g,vitamin_c_100g,vitamin_b1_100g,vitamin_b2_100g,vitamin_pp_100g,vitamin_b6_100g,vitamin_b9_100g,vitamin_b12_100g,biotin_100g,pantothenic_acid_100g,silica_100g,bicarbonate_100g,potassium_100g,chloride_100g,calcium_100g,phosphorus_100g,iron_100g,magnesium_100g,zinc_100g,copper_100g,manganese_100g,fluoride_100g,selenium_100g,chromium_100g,molybdenum_100g,iodine_100g,caffeine_100g,taurine_100g,ph_100g,fruits_vegetables_nuts_100g,collagen_meat_protein_ratio_100g,cocoa_100g,chlorophyl_100g,carbon_footprint_100g,nutrition_score_fr_100g,nutrition_score_uk_100g);


ALTER TABLE Food
ADD INDEX ind_code(code);

ALTER TABLE Food
ADD INDEX ind_url(url);

ALTER TABLE Food
ADD INDEX ind_product_name(product_name);




CREATE TABLE db_appli.User_preferences(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	numUser INT UNSIGNED NOT NULL,
	numFood INT UNSIGNED NOT NULL,
	rank VARCHAR(1) NOT NULL,
	PRIMARY KEY (id)
)ENGINE=INNODB;


ALTER TABLE User_preferences
ADD CONSTRAINT fk_numUser_idUser FOREIGN KEY (numUser) REFERENCES User(id_user);

ALTER TABLE User_preferences
ADD CONSTRAINT fk_numFood_idFood FOREIGN KEY (numFood) REFERENCES Food(id_food);