
(deftemplate Patient (slot temperature) 
                     (slot spots)
                     (slot rash)
                     (slot sore_throat)
                     (slot innoculated)
                     (slot fever))

(deffacts Initial
   (Patient ))

(defrule GetTemperature
   (declare (salience 500))
   ?p <- (Patient (temperature nil)) 
   =>
   (printout t "Enter patient temperature: ")
   (modify ?p (temperature (read))))

(defrule GetSpots
   (declare (salience 500))
   ?p <- (Patient (spots nil)) 
   =>
   (printout t "Does the patient have spots (yes or no): ")
   (modify ?p (spots (read))))

(defrule GetRash
   (declare (salience 500))
   ?p <- (Patient (rash nil)) 
   =>
   (printout t "Does the patient have a rash (yes or no): ")
   (modify ?p (rash (read))))

(defrule GetSoreThroat
   (declare (salience 500))
   ?p <- (Patient (sore_throat nil)) 
   =>
   (printout t "Does the patient have a sore throat (yes or no): ")
   (modify ?p (sore_throat (read))))

(defrule GetInnoculated "ask if the patient has been innoculated only if necessary"
   (declare (salience 500))
   ?p <- (Patient (fever high) (spots yes) (innoculated nil))
   =>
   (printout t "Has the patient been innoculated for measles (yes or no): ")
   (modify ?p (innoculated (read))))


;Rules for the fever

(defrule Fever1
   ?p <- (Patient (temperature ?t) (fever nil))
   (test (numberp ?t))
   (test (>= ?t 101))
   =>
   (modify ?p (fever high))
   (printout t "High fever diagnosed" crlf))

(defrule Fever2
   ?p <- (Patient (temperature ?t) (fever nil))
   (test (numberp ?t))
   (test (and (< ?t 101) (> ?t 98.6)))
   =>
   (modify ?p (fever mild))
   (printout t "Mild fever diagnosed" crlf))

; Rules for determining diagnosis on the basis of patient symptoms

(defrule Measles
   (declare (salience 100))
   (Patient (spots yes) (innoculated no) (fever high))
   =>
   (assert (diagnosis measles))
   (printout t "Measles diagnosed" crlf))

(defrule Allergy1
   (declare (salience -100))
   (and (Patient (spots yes))
        (not (diagnosis measles)))      
   =>
   (assert (diagnosis allergy))
   (printout t "Allergy diagnosed from spots and lack of measles" crlf))   

(defrule Allergy2
   (Patient (rash yes))
   =>
   (assert (diagnosis allergy))
   (printout t "Allergy diagnosed from rash" crlf))

(defrule Flu
   (or(Patient (sore_throat yes) (fever mild))
   (Patient (sore_throat yes) (fever high)))
   =>
   (assert (diagnosis flu))
   (printout t "Flu diagnosed" crlf))


(defrule Penicillin "Rule for recommedating treatment"
   (diagnosis measles)
   =>
   (assert (treatment pennicillin))
   (printout t "Penicillin prescribed" crlf))

(defrule Allergy_pills "Rule for recommedating treatment"
   (diagnosis allergy)
   =>
   (assert (treatment allergy_shot))
   (printout t "Allergy shot prescribed" crlf))

(defrule Bed_rest "Rule for recommedating treatment"
   (diagnosis flu)
   =>
   (assert (treatment bed_rest))
   (printout t "Bed rest prescribed" crlf))


(defrule None "If there are no diagnosis facts consult human expert"
   (declare (salience -100))
   (not (diagnosis ?))
   =>
   (printout t "No diagnosis possible -- consult human expert" crlf))