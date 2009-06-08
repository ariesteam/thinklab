(modelling/with-kbox 
	
	(modelling/kbox test "postgres://postgres:rnbh304@localhost:5432" 
				:protocol "pg" 
				:schema   "postgis" 
				:sql.use.pooling "false" 
				:sql.log.queries "true") 
		:storage-policy :kbox-empty
					
	(modelling/kbox-metadata observation:Observation
				(:latitude  '(...)
	 			 :longitude '(...)
	 			 :value     '(...)))
	 			 
	(object esval:Value 
		"this is a nice comment"
		(esval:hasValue 120)
		(esval:hasReference (object esval-10) :in biblio-esd)) :as esval-100  			 

	(modelling/model '(esval:GroupAssignedValue (hasBenefit 'eserv:NutrientRegulation))

  	"Estimated value of Nutrient regulation in Open Ocean
  	 138.890 USD-2001 using Direct Non-Market Group Valuation"
  	 
		(measurement 'currency:MonetaryValue "USD@2002/(ha yr)") 
  		:state 138.890					
	 	 	:context
			 ((classification 'ecology:Biome)  :as biome :state 'biomes:OpenOcean,
	 	  	(identification 'metadata:JournalArticle)  :state 'kbox://esd-bibliodata#refer-221,
	 	  	(temporal-location) :state 1998,
	 	 	  (areal-location)    :refer 'kbox://admin-98#WholeWorld)) :as esdval-2 

)
	 	