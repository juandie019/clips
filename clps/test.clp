(deftemplate book 
    (multislot surname)(slot name)(multislot title)
)

(deffacts initial 
    (book (surname J.P.)(name Dubreuil)(title history of Humanity))
    (book (surname J.P.)(name Eker)(title history of Dogs))
)

(defrule find_title
    ?book<-(book(name Eker))
    =>
    (printout t ?book crlf)
)

