# <div align="center"> E-commerce Application </div>

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Requirements](#requirements)
- [Endpoints](#endpoints)
- [ToDo](#todo)


---

## Overview

This **Spring Boot** project is a backend part for a full-stack e-commerce application.
<br />
<br />
**Frontend part:** https://github.com/galileo680/ecommerce-frontend
---

## Features

1. **User Registration & Activation**
    - Users can sign up with an email and password.
    - An activation link is sent via email to activate the account.

2. **Product Management**
    - Admins can create, update, or delete products.
    - Products have a name, description, price, quantity, and an image stored on AWS.

3. **Shopping Cart**
    - Users can add/update/remove products in their cart, or clear it entirely.

4. **Order Placement**
    - Users provide shipping and billing addresses.
    - Orders are confirmed via email notification, and inventory is updated.

5. **Email Service (HTML Templates)**
    - Thymeleaf templates for account activation emails and order confirmation.

---

## Tech Stack

- **Backend**:
    - **Spring Boot** (Java 17+)
    - **Spring Data JPA** (Hibernate)
    - **Spring Security**

- **Database**: **PostgreSQL** (can be changed if needed)
- **Mail**: `spring-boot-starter-mail` (tested with Mailtrap or any SMTP)
- **Templates**: Thymeleaf (for HTML emails)
- **AWS**: S3 for image/file storage
- **Build**: Maven
- **Testing**: JUnit 5, Mockito

---

## Requirements

- **Java 21+**
- **Maven/Gradle**
- **PostgreSQL** 
- **AWS Account** 
- **SMTP server** 


---


## Endpoints

### Auth
- **`POST /auth/register`**  
  Registers a new user; sends an activation email.

- **`POST /auth/login`**  
  Login existing user.

- **`GET /auth/activate?token=xyz`**  
  Activates the user account.


### Product
- **`POST /product`**   
  Creates a new product (uploads image to AWS S3).

- **`PUT /product/{productId}`** 

  Updates a product.

- **`GET /product`**  
  Retrieves a list of products.

- **`GET /product/{productId}`**  
  Fetches details for a single product.

- **`GET /category/{categoryId}`**  
  Retrieves a list of products with specific category.

- **`DELETE /product/{productId}`**

  Deletes a product.


### Category
- **`POST /category`**   
  Creates a new category.

- **`GET /category`**

  Retrieves a list of categories.

- **`GET /category/{productId}`**

  Retrieve a category with provided category id.

- **`PUT /category/{productId}`**

  Updates a category.

- **`DELETE /category/{productId}`**

  Deletes a category.

### Cart
- **`GET /cart`**  
  Retrieves the current user's cart.

- **`POST /cart/items`**  
  Adds a product to the cart.

- **`PUT /cart/items/{itemId}`**  
  Updates quantity for a cart item.

- **`DELETE /cart/items/{itemId}`**  
  Removes a cart item.

- **`DELETE /cart/clear`**  
  Clears the cart.


### Order
- **`POST /orders/checkout`**  
  Places an order from the user's cart.

- **`GET /orders`**  
  Lists the user's orders.

- **`GET /orders/{id}`**  
  Retrieves order details.

## ToDo
1. **Promotions & Coupons**
2. **Wishlist / Favorites**
3. **Payment Provider (Stripe or PayPal)**
4. **Admin Panel**
5. **Monitoring & Logging**
