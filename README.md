# GroceryList App

## Overview

The GroceryList App is an Android application designed to simplify grocery shopping by enabling users to create, manage, and track their grocery lists. The app incorporates several modules, including:

- **User Authentication:** Secure login and registration.
- **Home Screen:** A central hub with navigation to various functionalities.
- **List Management:** Create, update, and delete grocery lists.
- **Item Management:** Add and manage items within each grocery list.
- **Shopping Mode:**  Display unpurchased items, compare various store prices, track total spendings
  and reset option for budget and total spent amount.
- **Settings:** Customize themes, toggle notifications, and manage spending insights.

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
  *Enhancements include scheduled reminders and dynamic notifications based on user activity.*

- **Log out:**  
  Clears session data and navigates back to the login screen.

- **Data Persistence:**  
  Uses the Room database for reliable local storage of user data, lists, and items and settings.
  Budget data is stored per list using SharedPreferences.

## Architecture & Technology Stack

- **Modular Design:**  
  Project is structured into layers for models, DAOs, repositories, activities, adapters, and
  background workers.

- **Key Components:**
    - **Models:**:
      User, Item, GroceryList, Settings, and new entities:
        - Store: for comparing prices between stores
        - StoreSection: for grouping items in the UI
        - ItemPriceHistory: for showing historical price trends per store

    - **Data Access Objects (DAOs):** Interfaces that define methods for database interactions. New
      Daos: ItemPriceHistoryDao (for price tracking) and SettingsDao (for user preferences).
    - **Services/Repositories:** Abstract business logic (e.g., ItemRepository, UserRepository) and
      support clean Room access.
    - **Activities:** Different screens (e.g., LoginActivity, MainActivity, ListManagementActivity)
      handle user interactions.
    - **Adapters:** ItemAdapter: Custom adapter used in Shopping Mode with purchase toggle logic.
    - **Background Services:**
        - ReminderWorker: Sends daily notifications about unpurchased items.
        - NotificationHelper: Handles channel creation and Android 13+ permission logic.

- **Technology Stack:**
    - **Programming Language:** Java 17
    - **Android SDK** (compileSdkVersion 35, targetSdkVersion 34, minSdkVersion 26)
  - **Room Database:** For local data persistence.
  - **Gradle:** For build automation and dependency management.
  - **Device** Medium Phone API 35

## Installation

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/liuStacy/CST8319Project3
