;DEFTEMPLATE
(deftemplate PatientRoom
	(slot temperature-outside)
	(slot temperature-inside)
	(slot wanted-temperature (default 23))
	(slot start)
	(slot answer)
)

(defrule Window
   (declare (salience 100))
 ?pr<-(PatientRoom (answer nil) (start YES)(temperature-outside ?out) (temperature-inside ?in) (wanted-temperature ?want))
 (test (numberp ?out))
 (test (numberp ?in))
 (test (numberp ?want))
 (test (> ?out ?in))
 (test (> ?want ?in)) 
  =>
   (modify ?pr (answer "Open Window")))

   (defrule Window2
   (declare (salience 100))
 ?pr<-(PatientRoom (answer nil) (start YES)(temperature-outside ?out) (temperature-inside ?in) (wanted-temperature ?want))
 (test (numberp ?out))
 (test (numberp ?in))
 (test (numberp ?want))
 (test (< ?out ?in))
 (test (< ?want ?in)) 
  =>
   (modify ?pr (answer "Open Window")))

(defrule Heater
(declare (salience 90))
 ?pr<-(PatientRoom (answer nil) (start YES)(temperature-outside ?out) (temperature-inside ?in) (wanted-temperature ?want))
 (test (numberp ?in))
 (test (numberp ?want))
 (test (> ?want ?in)) 
  =>
   (modify ?pr (answer "Turn on heater")))
   
(defrule AC
(declare (salience 90))
 ?pr<-(PatientRoom (answer nil) (start YES)(temperature-outside ?out) (temperature-inside ?in) (wanted-temperature ?want))
 (test (numberp ?in))
 (test (numberp ?want))
 (test (< ?want ?in)) 
  =>
   (modify ?pr (answer "Turn on AC")))
  
(defrule Nothing
(declare (salience 80))
 ?pr<-(PatientRoom (answer nil) (start YES)(temperature-outside ?out) (temperature-inside ?in) (wanted-temperature ?want))
  =>
   (modify ?pr (answer "Do Nothing")))
     
