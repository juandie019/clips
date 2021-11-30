(deftemplate seller
  (slot seller-id)
)

(deftemplate product
  (slot product-id)
  (multislot name)
  (slot category)
)

(deftemplate order
  (slot order-id)
  (slot seller-id) 
)

(deftemplate order-description
  (slot order-id)
  (multislot product)
)

(deftemplate product-seller
  (slot seller-id)
  (slot product-id)
  (slot price)
  (slot quantity (default 0))
)

(deftemplate offer
  (slot order-id)
  (slot price)
  (multislot product)
)