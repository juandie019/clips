; ;;Define a rule for finding those customers who have not bought nothing at all... so far

; (defrule cust-not-buying
;      (customer (customer-id ?id) (name ?name))
;      (not (order (order-number ?order) (customer-id ?id)))
;    =>
;    (printout t ?name " no ha comprado... nada!" crlf))


; ;;Define a rule for finding which products have been bought

; (defrule prods-bought
;    (order (order-number ?order))
;    (line-item (order-number ?order) (part-number ?part))
;    (product (part-number ?part) (name ?pn))
;    =>
;    (printout t ?pn " was bought " crlf))


; ;;Define a rule for finding which products have been bought AND their quantity

; (defrule prods-qty-bgt
;    (order (order-number ?order))
;    (line-item (order-number ?order) (part-number ?part) (quantity ?q))
;    (product (part-number ?part) (name ?p) )
;    =>
;    (printout t ?q " " ?p " was/were bought " crlf))

; ;;Define a rule for finding customers and their shopping info

; (defrule customer-shopping
;    (customer (customer-id ?id) (name ?cn))
;    (order (order-number ?order) (customer-id ?id))
;    (line-item (order-number ?order) (part-number ?part))
;    (product (part-number ?part) (name ?pn))
;    =>
;    (printout t ?cn " bought  " ?pn crlf))

;;Define a rule for finding sellers and their products

; (defrule seller-stock
;    (seller (seller-id ?id))
;    (product-seller (seller-id ?id) (product-id ?productId) (price ?price))
;    (product (product-id ?productId) (name $?d))
;    =>
;    (printout t ?id " has  " ?d " at " ?price crlf))

(defrule can-sell
   (seller (seller-id ?id))
   (order (seller-id ?id) (order-id ?orderId))
   (order-description (order-id ?orderId) (product $?product))
   (product (name $?product) (product-id ?productId))
   (product-seller (seller-id ?id) (product-id ?productId) (price ?price))
   =>
   ; (printout t ?id " Can sell " ?product " at " ?price crlf)
   (assert (offer (order-id ?orderId) (product-id ?productId) (product ?product) (price ?price)))
)

;; Define a rule for a order of iphone 13 and Banamex card
(defrule iPhone13-Banamex 
   (order (order-id ?orderId) (card banamex))
   (purchase-description (order-id ?orderId) (product $? iphone 13 $?))
=>
(assert (offer-msg (order-id ?orderId) (message Tu compra de ha sido diferida a 24 meses sin intereses))))

;; Define a rule for a order of note 12 and Liverpool card
(defrule note-liverpool 
   (order (order-id ?orderId) (card liverpool))
   (purchase-description (order-id ?orderId) (product $? samsung note 12 $?))
=>
   (assert (offer-msg (order-id ?orderId) (message Tu compra de ha sido diferida a 12 meses sin intereses))))

;; Define a rule for a purchase of an iphone and macbook air with card payment method 
(defrule mac-iphone 
   (order (order-id ?orderId) (card contado))
   (purchase-description (order-id ?orderId) (product $? iphone 13 $?))  
   (purchase-description (order-id ?orderId) (product $? macbook air $?))  
   => 
   (assert (offer-msg (order-id ?orderId) (message Por tu compra puedes obtener una funda y mica con 15% de descuento))))

; (defrule all-lamps-are-on 
;   (lamp (state on)) 
;   (test (>= (length$ (find-all-facts ((?l lamp)) (eq ?l:state on))) 3)) 
;   => 
;   (printout t "All lamps are on" crlf)) 

; (assert (call-customer ?name ?phone "tienes 25% desc prox compra"))

; (defrule can-sell
;    (seller (seller-id ?id))
;    (order (seller-id ?id) (order-id ?orderId))
;    (order-description (order-id ?orderId) (product $?product))
;    (product (name $?product) (product-id ?productId))
;    (product-seller (seller-id ?id) (product-id ?productId) (price ?price))
;    =>
;    (printout t ?id " Can sell " ?product " at " ?price crlf))

; ;;Define a rule for finding those customers who bought more than 5 products

; (defrule cust-5-prods
;    (customer (customer-id ?id) (name ?cn))
;    (order (order-number ?order) (customer-id ?id))
;    (line-item (order-number ?order) (part-number ?part) (quantity ?q>5))
;    (product (part-number ?part) (name ?pn))
;    =>
;    (printout t ?cn " bought more than 5 products (" ?pn ")" crlf))

; ;; Define a rule for texting custormers who have not bought ...

; (defrule text-cust (customer (customer-id ?cid) (name ?name) (phone ?phone))
;                    (not (order (order-number ?order) (customer-id ?cid)))
; =>
; (assert (text-customer ?name ?phone "tienes 25% desc prox compra"))
; (printout t ?name " 3313073905 tienes 25% desc prox compra" ))


