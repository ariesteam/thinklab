;; ================================================================================================
;; simplified builders for object of common use
;; ================================================================================================

(ns corescience)

;; ================================================================================================
;; public functions
;; ================================================================================================

(defn make-discretizer
   "Builds the list definition of an instance of a discrete ranking conceptual model. Alternate 
   concepts and literal intervals."
   [spec & specs]
   (org.integratedmodelling.corescience.clojure.ClojureBridge/makeDiscretizer 
		(tl/listp (conj specs spec))))

(defn make-unit
   "Builds the list definition of an instance of a Measurement conceptual model from the 
   textual specification of a measurement unit."
   [unitspec]
   (org.integratedmodelling.corescience.clojure.ClojureBridge/makeUnit unitspec))