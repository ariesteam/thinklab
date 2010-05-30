(defconcept myontology:DioPorco
  "This is the concept's description"
  :label "Poppa Dioporco" 
  :subclass-of (defconcept 'habitat:Temperature)
  :all-values-of 'myOntology:Puttanismo :from  'zubba:Poppalessa)

(defproperty zio:hasCaimano
  "Comment here" 
  :transitive false
  :functional true
  :range 'zio:Patroclo
  :domain :string)