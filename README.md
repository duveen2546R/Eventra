# Eventra - Event Management Platform

Eventra is a full-stack web application designed to streamline event management. It provides a platform for users to create, discover, and manage events, with features for organizers and participants, including event creation, registration, and payment processing.

## Features

*   **User Authentication:** Secure user registration and login system using JWT.
*   **Event Creation & Management:** Organizers can create detailed events, including name, date, location, and description.
*   **Event Discovery:** Users can browse and search for upcoming events.
*   **Event Registration:** Participants can easily register for events.
*   **Payment Processing:** Integration with Razorpay for handling event payments.
*   **My Events:** Users can view a list of events they have created or registered for.
*   **Email Notifications:** Automated emails for events like registration confirmation.
*   **Event Check-in:** Functionality to manage participant check-ins at the event.
*   **Analytics Insights:** A dedicated page to provide insights into event performance (for organizers).

## How It Works

The application follows a client-server architecture:

*   **Frontend (Client):** A responsive single-page application (SPA) built with **Vue.js**. It handles the user interface and interacts with the backend via REST APIs.
*   **Backend (Server):** A robust RESTful API server built with **Java and Spring Boot**. It manages business logic, data persistence (using a relational database), and communication with third-party services like Razorpay for payments.

The typical user flow is:
1.  A user registers or logs into their account.
2.  They can browse the list of available events.
3.  An organizer can create a new event, providing all the necessary details.
4.  A participant can view event details and register for it, which may involve a payment step.
5.  Upon successful registration, the user receives a confirmation email.
6.  Users can see the events they are organizing or attending in the "My Events" section.

## Tech Stack

*   **Frontend:** Vue.js, Tailwind CSS, Axios
*   **Backend:** Java, Spring Boot, Spring Security, JPA (Hibernate)
*   **Database:** (Please specify your database, e.g., PostgreSQL, MySQL)
*   **Payments:** Razorpay
*   **Authentication:** JWT (JSON Web Tokens)

## Screenshots

Here is a short video showcasing the authentication flow:

*(You can include the `auth.webm` here if your Markdown viewer supports it)*

Please add more screenshots of the application to showcase its features.

**Landing Page:**
*(Add screenshot of LandingPage.vue)*

**Events Page:**
*(Add screenshot of EventsPage.vue)*

**Event Creation:**
*(Add screenshot of CreateEventPage.vue)*

## Getting Started

### Prerequisites

*   Java 21 or later
*   Node.js and npm
*   A configured database (e.g., PostgreSQL)
*   A Razorpay account for payment processing

### Backend Setup

1.  Navigate to the `evantra` directory.
2.  Update the `application.properties` file in `src/main/resources/` with your database and Razorpay credentials.
3.  Run the application:
    ```bash
    ./mvnw spring-boot:run
    ```

### Frontend Setup

1.  Navigate to the `client` directory.
2.  Install the dependencies:
    ```bash
    npm install
    npm install @motionone/vue
    npm install @fortawesome/vue-fontawesome @fortawesome/fontawesome-svg-core @fortawesome/free-solid-svg-icons
    npm install -D tailwindcss@3.4.13 postcss autoprefixer
    npx tailwindcss init -p
    npm install axios
    npm install jwt-decode
    ```
3.  Run the development server:
    ```bash
    npm run dev
    ```
The application will be accessible at `http://localhost:5173`.
