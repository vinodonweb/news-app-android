# News of the World App 📰  

## Overview  
This is an Android news application that retrieves and displays the latest news articles from multiple sources using the **NewsAPI.org**. Users can filter news sources by **topic, country, and language** and browse articles by swiping through them.  

## Features  
✅ **News Sources & Filtering**  
- Displays **top 10 news articles** from a selected source.  
- Users can **filter sources** based on topic (e.g., Business, Sports), country, and language.  
- **Clear All** option to reset filters.  

✅ **User Experience & UI**  
- **Drawer Layout** for selecting news sources.  
- **ViewPager2** for seamless left/right navigation between articles.  
- **Dynamic Menus** that update based on API data.  
- **Professional launcher icon**.  

✅ **News Details & Interactions**  
- Clicking an article **opens the full story** in a browser.  
- Displays **author, publication date, title, and description**.  
- Handles missing images by showing a **default placeholder**.  
- Converts ISO 8601 dates into a **user-friendly format**.  

## Tech Stack 🛠️  
- **Android Studio** (Java/Kotlin)  
- **NewsAPI.org** for news data  
- **Android Volley** for API requests  
- **ViewPager2 & RecyclerView** for UI display  
- **Drawer Layout & Navigation Components** for app navigation  
- **Implicit Intents** for opening full articles in a browser  

## How to Run the App 🚀  
1. Clone the repository:  
   ```sh  
   git clone git@github.com:vinodonweb/news-app-android.git
   cd news-app  
   ```  
2. Open the project in **Android Studio**.  
3. Obtain a free **API Key** from [NewsAPI.org](https://newsapi.org/) and add it to `strings.xml`:  
   ```xml  
   <string name="news_api_key">YOUR_API_KEY_HERE</string>  
   ```  
4. Run the app on an emulator or physical device.  

## Screenshots 📸  

 ![Screenshot 2025-03-11 113828](https://github.com/user-attachments/assets/dc6c6c47-ec87-4a7c-9e57-c52de47d5ce1)
![Screenshot 2025-03-11 113859](https://github.com/user-attachments/assets/177af930-88bf-4086-bd1e-8000e1ab6bf2)
![Screenshot 2025-03-11 113924](https://github.com/user-attachments/assets/195d6ae0-b578-414a-b31e-d37d544d0d84)
![Screenshot 2025-03-11 113818](https://github.com/user-attachments/assets/42281968-5954-424e-96b5-c58da41fdeae)

## License 📜  
This project is for educational purposes only.  
