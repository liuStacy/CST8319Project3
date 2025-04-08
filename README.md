# GroceryList App

## Overview

The GroceryList App is an Android application designed to simplify grocery shopping by enabling users to create, manage, and track their grocery lists. The app incorporates several modules, including:

- **User Authentication:** Secure login and registration.
- **Home Screen:** A central hub with navigation to various functionalities.
- **List Management:** Create, update, and delete grocery lists.
- **Shopping Mode:**  Enter item prices and calculate spending.
- **Item Management:** Add and manage items within each grocery list.
- **Settings:** Customize themes, toggle notifications, and manage spending tracking.

This project transitions from the conceptual design phase to active implementation, following agile methodologies and coding standards to deliver a functional software product.

## Features

- **User Authentication:**  
  Allows users to register and log in using their email and password.

- **Home Screen:**  
  Provides navigation buttons to access list management, shopping mode, settings and logout.

- **List Management:**  
  Enables creation, editing, and deletion of grocery lists with intuitive short-click and long-press actions.

- **Item Management:**  
  Supports adding items to lists with details such as name, brand, price, quantity, and purchase status.

  - **Shopping Mode:**  
  Provides functionality to enter item prices, calculate the total spending in real time, and complete the shopping transaction while monitoring against a predefined budget limit.


- **Settings Module:**  
  Permits customization of themes (light/dark), notifications, and spending tracker settings.  
  *Future enhancements include scheduled reminders and dynamic notifications based on user activity.*

- **Log out:**  
  Clears stored login data and navigates the user back to the login page, ensuring a secure session termination.

- **Data Persistence:**  
  Uses the Room database for reliable local storage of user data, lists, and items.

## Architecture & Technology Stack

- **Modular Design:**  
  The project follows a modular architecture where models, DAOs, services, and activities are clearly separated.

- **Key Components:**
  - **Models:** Entities (e.g., User, GroceryItem, Settings) defined using Room annotations.
  - **Data Access Objects (DAOs):** Interfaces that define methods for database interactions.
  - **Services/Repositories:** Classes like `UserRepository` manage authentication and business logic.
  - **Activities:** Different screens (e.g., LoginActivity, MainActivity, ListManagementActivity) handle user interactions.

- **Technology Stack:**
  - **Programming Language:** Java
  - **Android SDK**
  - **Room Database:** For local data persistence.
  - **Gradle:** For build automation and dependency management.
  - **Device** Medium Phone API 35

## Installation

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/liuStacy/CST8319Project3
