(defmodel preference 'policy:PreferenceStructure
  (mca 'policy:Concordance
    (preference ('flood:ProtectionValue 10) ('carbon:SequestrationValue 20)) 
    :scenarios (
         (apply-scenario dmodel scenario1) :as sc1
         (apply-scenario dmodel scenario2) :as sc2)
    :method electre3))