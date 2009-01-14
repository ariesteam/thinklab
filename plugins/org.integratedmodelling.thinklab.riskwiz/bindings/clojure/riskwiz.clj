;; ---------------------------------------------------------------------------------------------------
;; Clojure bindings for RiskWiz in thinklab
;; fv 1/14/2009
;; ---------------------------------------------------------------------------------------------------

(ns riskwiz)

(defn make-bn 
	""
	([source] nil)
	([name nodelist] nil))
	
(defn make-node
	""
	([id] nil) 
	([id parents] nil)
	([id parents cpt] nil))
	
