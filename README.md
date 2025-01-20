# 🌟 Story Dicoding App  
**Story Dicoding App** is an Android application designed to showcase user stories through an engaging and interactive interface. It includes authentication features, story listing, map integration, animations, and the ability to add new stories. Built using Kotlin, MVVM architecture, and modern Android development practices, this app ensures seamless performance and usability.  

---

## ✨ Features  
### 1. Authentication  
- **Login Page**:  
  - Allows users to log in with email and password.  
  - Real-time password validation (minimum 8 characters).  
- **Register Page**:  
  - New users can register with a name, email, and password.  
  - Custom `EditText` validates inputs and displays error messages directly.  
- **Session Management**:  
  - Stores session data and token in `SharedPreferences`.  
  - Redirects to the main page if logged in; otherwise, displays the login page.  
- **Logout**:  
  - Clears session and token upon logout.  

### 2. List Story  
- Displays a list of stories fetched from the API with:  
  - **User Name** (R.id.tv_item_name).  
  - **User Photo** (R.id.iv_item_photo).  
- Detailed story view with:  
  - **Name** (R.id.tv_detail_name).  
  - **Photo** (R.id.iv_detail_photo).  
  - **Description** (R.id.tv_detail_description).  

### 3. Add Story  
- Users can upload stories with:  
  - **Photo**: Supports file selection from the gallery.  
  - **Description** (R.id.ed_add_description).  
- After upload, the new story appears at the top of the list.  

### 4. Animation  
- Enhances user experience with **Shared Element Animation** during transitions.  

### 5. Maps Integration  
- Displays a map showing all stories with valid geolocation data using **Google Maps API**.  
- Markers or icons represent the story's location.  

### 6. Paging List  
- Implements **Paging 3** for seamless and efficient story list scrolling.  

### 7. Testing  
- Includes unit tests for ViewModel functions to validate:  
  - Successful story data loading.  
  - Non-null data and correct item count.  
  - Data alignment with expected results.  
  - Handling of empty data cases.  

---

## 🌟 Journey  
The journey of creating this project was deeply enriching and full of challenges. From understanding the nuances of authentication and session management to integrating modern libraries like Paging 3, every step taught valuable lessons. The most fulfilling moment was implementing **map integration** and seeing user stories come to life on a map, bridging the digital and real-world experiences.  

Crafting animations using **Shared Element Transitions** added an artistic touch, while working on **unit testing** reinforced the importance of ensuring app reliability. Through this project, the fusion of creativity and technical expertise led to a product that stands as a testament to the power of well-structured development.  

---

## 🚀 Get Started  
### Prerequisites  
- Android Studio Flamingo or newer.  
- Minimum SDK: 21.  
- API Key for Google Maps (refer to [Google Maps Documentation](https://developers.google.com/maps/documentation/android-sdk/start)).  

### Installation  
1. Clone the repository:  
   ```bash
   git clone https://github.com/username/story-dicoding-app.git
   cd story-dicoding-app
   ```
2. Open the project in Android Studio.
3. Add your Google Maps API Key in the local.properties file:
  ```bash
   MAPS_API_KEY=YOUR_API_KEY_HERE
```
4. Sync the project and build.
5. Run the application on an emulator or a physical device.

---
🛠️ Technologies Used
- Kotlin
- MVVM Architecture
- Retrofit
- Paging 3
- Google Maps API
- SharedPreferences
- Unit Testing with JUnit
---

📚 References
- Dicoding Starter Project
- Google Maps API Documentation
- Paging 3 Official Guide


Feel free to replace placeholders (e.g., API keys, repository link, and screenshots) with the actual details from your project.
