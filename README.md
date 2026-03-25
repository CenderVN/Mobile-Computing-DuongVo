# CatConnect: Android Meme Messenger

A lightweight Android messaging application built for creating and exchanging custom memes. This project explores mobile-to-server communication, local data persistence, and background task management.

## 🚀 Overview
CatConnect allows users to design and share personalized memes featuring custom text and imagery. The application follows an "offline-first" approach, utilizing a local SQLite database to manage chat history while relying on a custom Python-Flask backend to facilitate message delivery between users.

## 🛠 Technical Highlights

### Android Client
*   **Offline Persistence:** Uses **Room Database** to store local user profiles, contacts, and message history, ensuring the app remains functional without an active network connection.
*   **Background Synchronization:** Leverages **WorkManager** to handle periodic background tasks, automatically fetching new memes from the server every 15 minutes.
*   **Dynamic Media:** Implements **Glide** for efficient image loading and rendering within the message feed and meme creation interface.
*   **Asynchronous Processing:** Network operations and database transactions are offloaded to background threads to ensure a smooth, responsive UI.

### Backend (Python/Flask)
*   **RESTful API:** A Flask-based server acts as a message broker, queuing incoming memes and delivering them to the intended recipient.
*   **Media Handling:** Processes base64-encoded images from the app, saving them to a local directory and generating public URLs for delivery.
*   **Debug Utilities:** Includes a manual trigger route for testing system-wide meme broadcasting.

## ⚙️ Configuration
To run the application in your local development environment:
1.  **Backend:** Run `python server.py`. Ensure your machine is reachable on your local network.
2.  **Android Client:** Update the `SERVER_URL` in `MemeCreatorActivity.java` and `MemeWorker.java` to match your development machine's IP address (currently configured for `192.168.0.150`).

## 📋 Key Features
*   **Meme Creator:** Design custom memes with top/bottom text captions and choose between local gallery images or remote URLs.
*   **Chat Interface:** View organized message history with persistent storage via Room.
*   **Automated Sync:** Periodic checks for new messages in the background.
*   **User Profiles:** Personalize your account with custom usernames and profile photos.

***

### Technologies Used
*   **Mobile:** Java, Android SDK, Room, WorkManager, Glide, GSON
*   **Backend:** Python, Flask, RESTful API (JSON)
