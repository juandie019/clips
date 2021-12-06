(deffacts products 
 	(product (name iphone 7) (category smartphones) (product-id 1234))
 	(product (name iphone 13) (category smartphones) (product-id 1235))
 	(product (name macbook air) (category computers) (product-id 1236))
 	(product (name macbook pro) (category computers) (product-id 1238))
 	(product (name samsung note 12) (category computers) (product-id 1237))
)

(deffacts sellers
  (seller (seller-id amazon1))
  (seller (seller-id amazon2))
  (seller (seller-id alibaba)) 
  (seller (seller-id alibaba3))
)  	 


; (deffacts orders 
; 	(order (order-number 300) (customer-id 102))
; 	(order (order-number 301) (customer-id 103))
; )

(deffacts product-sellers
	(product-seller (product-id 1234) (seller-id amazon1) (price 1000))
	(product-seller (product-id 1234) (seller-id alibaba) (price 1100))
	(product-seller (product-id 1235) (seller-id amazon1) (price 1500))
	(product-seller (product-id 1235) (seller-id alibaba) (price 1450))
	(product-seller (product-id 1236) (seller-id amazon2) (price 2000))
	(product-seller (product-id 1236) (seller-id alibaba) (price 1900))
	(product-seller (product-id 1238) (seller-id amazon2) (price 2500))
	(product-seller (product-id 1238) (seller-id alibaba) (price 2700))
	(product-seller (product-id 1237) (seller-id amazon1) (price 1010))
	(product-seller (product-id 1237) (seller-id alibaba) (price 1020))
)

; (deffacts orders 
; 	(order (seller-id alibaba) (order-id 1) (card contado)) 
; 	(order (seller-id alibaba) (order-id 2) (card contado)) 
; 	; (order (seller-id alibaba) (order-id 2) (card contado)) 
; 	; (order (seller-id alibaba) (order-id 1) (card contado)) 
; 	; (order (seller-id amazon1) (order-id 2)) 
; 	; (order (seller-id amazon2) (order-id 3)) 
; )

; (deffacts items-list
;   (order-description (order-id 1) (product iphone 13))
;   (order-description (order-id 2) (product macbook air))
; ;   (order-description (order-id 1) (product iphone 13))
; ;   (order-description (order-id 1) (product macbook air))
; ;   (order-description (order-id 2) (product iphone 13))
; ;   (order-description (order-id 3) (product macbook air))
; )
