SELECT o.product_name
FROM ORDERS o
JOIN CUSTOMERS c ON c.id = o.customer_id
WHERE LOWER(c.name) = LOWER(:name)
ORDER BY o.id;

