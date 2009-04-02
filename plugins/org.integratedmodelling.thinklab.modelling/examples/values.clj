(defmodel esdval-2 '(eserv:GroupAssignedValue (hasBenefit 'eserv:NutrientRegulation))

  "Estimated value of Nutrient regulation in Open Ocean
   138.890 USD-2001 using Direct Non-Market Group Valuation "
   
	(measurement (currency:MonetaryValue) "USD@2002/(ha yr)") 
  	:state 138.890					
	  :context
		 ((classification 'esval:biome)  :as biome  :state 'biomes:OpenOcean,
	 	  (identification 'metadata:JournalArticle) :state 'kbox://esd-bibliodata#refer-221,
	 	  (temporal-location) :state 1998,
	 	  (areal-location)    :refer 'kbox://admin-98#WholeWorld))
	 	
