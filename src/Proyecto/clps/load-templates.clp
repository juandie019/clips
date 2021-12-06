(deftemplate seller
  (slot seller-id)
)

(deftemplate product
  (slot product-id)
  (multislot name)
  (slot category)
)

(deftemplate product-seller
  (slot seller-id)
  (slot product-id)
  (slot price)
  (slot quantity (default 0))
)

(deftemplate order
  (slot order-id)
  (slot seller-id)
  (slot card)
)

(deftemplate order-description
  (slot order-id)
  (multislot product)
)

(deftemplate purchase-description
  (slot order-id)
  (multislot product)
)

(deftemplate order-description
  (slot order-id)
  (multislot product)
)

(deftemplate offer
  (slot order-id)
  (slot product-id)
  (multislot product)
  (slot price)
)

(deftemplate offer-msg
  (slot order-id)
  (multislot message)
)
