# BudgetBuddy-Expense-Management-System

BudgetBuddy is a robust **Desktop Application** designed for efficient personal financial management.  
It is developed using **Java (backend logic), JavaFX (frontend UI)** and **MySQL database**.  
The system provides powerful tools to **track expenses, manage budgets, and view insightful financial analysis**.

---

## Core Highlights
- Secure authentication with email confirmation
- Smart budget monitoring with automated alert emails
- Expense insights using Bar & Pie charts
- Monthly & yearly automated financial summary emails
- User and Admin panels with different access controls
- Advanced search filters across data tables

---

## 1. Authentication & Security
- Users can **Log In** with existing accounts.
- **Sign Up** creates a new account and sends an **email confirmation**.
- Password update sends an **email notification**.
- Secure session **Logout**.

---

## 2. User Dashboard – Main Modules
The dashboard contains a left-side navigation bar to access the following screens:

### I. Category Screen
- Add / Delete expense categories (Food, Travel, Utilities, etc.)
- Categories are used for both budgets and expenses.

### II. Manage Budget Screen
- Set and update budgets with:
  - Category
  - Amount
  - Start and End Dates (flexible duration)
  - Active / Inactive status
- View **remaining vs used budget**
- All active budgets are shown via **Pie Chart**
- **Email Alert Trigger**:
  - When spending reaches **90% or more of a budget**
  - Helps prevent overspending before budget exhausts

### III. Expenses Screen
- Add expenses with Category, Amount, Description, Date
- If the expense date is within an active budget’s period, user can **link the budget**
- User may also select **No Budget**
- Update and Delete existing expense records

### IV. Expense Analysis Screen
Graphical insights into spending patterns:
| Analysis Type | Description |
|---------------|-------------|
| Category-wise (Date Range) | Bar chart showing total expenses for each category |
| Day-of-Month | Bar chart showing expense per day for a selected month/year |

### V. Profile Screen
- View/Update username and email
- Change password (Current → New → Confirm New)
- Successful profile update sends confirmation email

### VI. Logout
- Securely ends the user session

---

## 3. Automated Email Alerts & Summaries
| Email Type | Trigger |
|-----------|-----------|
| Account Confirmation | After successful signup |
| Budget Threshold Warning | When budget usage reaches/exceeds 90% |
| Profile Update Notification | After editing profile/password |
| Monthly Expense Summary | Automatically every month |
| Yearly Expense Summary | Automatically every year |

---

## 4. Search Functionality (Client Side)
Two dedicated search bars:
- **Global Search Bar** → Searches across all data columns
- **Column-based Search** → Activated via right-click on column header

---

## 5. Admin Panel (Administrator)
The Admin can monitor and manage users and their financial records.

### Admin Dashboard Screens
| Screen | Description |
|--------|-------------|
| Manage User | View and delete user accounts |
| User Expenses | View-only mode for selected user’s expenses |
| User Budgets | View-only mode for selected user’s budgets |
| Analysis | Generate expense charts for any selected user |
| Profile | Update admin details & password (email notification) |
| Logout | End admin session |

### Search System for Admin
Available in:
- Manage User
- User Expenses
- User Budgets
- Analysis

Supports:
- **Global Search Bar**
- **Column-wise Search Bar**

---

## Tech Stack
| Layer | Technology |
|-------|-------------|
| Frontend | JavaFX |
| Backend | Java |
| Database | MySQL |
| Email API | JavaMail API |
| IDE | IntelliJ / VS Code / Eclipse / NetBeans |

---

## Database Schema Overview

The database consists of 7 tables that store user, expense, and admin-related information for the Expense Management System.

| Table Name         | Description |
|--------------------|-------------|
| admins             | Stores admin login credentials and profile details. |
| budgets            | Stores user budgets including category, amount, start date, end date, status, and usage tracking. |
| category           | Stores expense categories created by users. |
| email_logs         | Stores logs of automated emails sent to users (alerts, summaries, etc.). |
| expenses           | Stores expense transactions recorded by users, including category, amount, description, and date. |
| generated_reports  | Stores generated monthly and yearly summary reports for users. |
| users              | Stores user account and profile data (name, email, password, etc.). |

---

## 🔮 Future Enhancements

The following improvements can be integrated into future releases of BudgetBuddy to enhance functionality, usability, and performance:

### 1. AI-Based Expense Prediction
Automatically predict upcoming expenses and suggest optimal budget allocation using machine learning.

### 2. Smart Receipt Scanner (OCR)
Allow users to upload bill/receipt images and auto-extract expense information using OCR technology.

### 3. Multi-Currency Support
Support for different currencies with automatic exchange rate conversion for international users.

### 4. Expense Sharing / Split Billing
Enable group expense sharing (e.g., roommates, friends, trips) where expenses can be split among multiple users.

### 5. Cloud Data Sync
Store user accounts and data on cloud services so users can access their dashboard from multiple devices.

### 6. Mobile Application (Android + iOS)
Develop a full mobile app version of BudgetBuddy for portable financial tracking.

### 7. Export Reports
Allow users to export expense reports in PDF, Excel, and CSV formats.

### 8. Dark and Light Themes
Provide customizable themes for better user experience and accessibility.

### 9. Multi-Language Support
Support for multiple languages so users can access the application in their preferred language.

### 10. Voice Assistant Integration
Enable hands-free expense logging using voice commands (e.g., "Add ₹450 Food expense today").

### 11. Role-Based Access System (Company-Level Authorization)
Implement a multi-role structure similar to corporate systems, where different types of users have different permissions and access levels.  
Example roles: Employee, Manager, Finance Officer, Admin.

- **Employee** → Can add and view own expenses only  
- **Manager** → Can view expenses of team members and approve/reject financial requests  
- **Finance Officer** → Can analyze organization-wide expense data and generate reports  
- **Admin** → Full control over system users, roles, and permissions

---

## Developed By
**Heet Nagapara**

Email: heetnagapara007@gmail.com  
GitHub: https://github.com/heet-nagapara-2005 
LinkedIn: https://www.linkedin.com/in/heet-nagapara-111577374  


