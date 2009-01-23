;; -----------------------------------------------------------------------------------------------
;; Sample semantic model of Climate Stability
;; -----------------------------------------------------------------------------------------------
;; each result concept must be a subclass of the target concept; they're all observables for which we
;; lookup observations.
;; 
;; each defvar says: if this is true, then I want my target concept to be seen as the result concept.
;; 
;; Target types have been defined with overridden instances of conceptual models to adopt
;;
;; The system is instructed to lookup transformations to turn the target CM into the result CM
;;
;; Result concepts can use any CM - stochastic, deterministic etc - and specify dependencies on
;; observables that will be filtered through the same rule base to build a final chain of dependencies
;;
;; The contextualization process will build the final model according to the types of CM in the
;; structure: BN,, dynamic model etc...
;; ------------------------------------------------------------------------------------------------

(load-bindings 'geospace)
(load-bindings 'semanticmodelling)

(observe 'habitat:PopulationPerKm2 'population)
(observe 'geospace:Latitude 'lat)
(observe 'geospace:Longitude 'lon)

(defvar 'carbonService:ClimateStability
    (cond 
    	'(and (between lat 12.22 21.22) (between lon 30.22 34.84)) 
    	'carbonService:Discrete6CarbonSequestered)
    	'(and (in-region lat lon (get-region 'Madagascar)) (> population 250))
    	'carbonService:TropicalCarbonSequestered)
    	nil
    	'carbonService:GeneralCarbonSequestered))
    	
(defvar 'habitat:RainfallAmount
    (cond
    	'(in-region (get-region 'Tropics)) 'carbonService:Discrete12RainfallPerMonth)
    	nil
    	'carbonService:GeneralRainfallPerYear))

