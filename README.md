# Recipe App

A feature-rich Android recipe application that helps users discover, save, and organize recipes.

## Features

- Browse and search recipes
- View recipe details with ingredients and instructions
- Save favorite recipes
- Track recipe viewing history
- Shopping list management
- Location-based features for finding ingredients
- User authentication and cloud sync

## Code Attribution & References

This project includes code and patterns adapted from the following sources:

1. Performance & Data Management:
   - [Android Vitals and Performance Optimization](https://developer.android.com/topic/performance/vitals)
   - [RecyclerView Optimization Tips](https://www.youtube.com/watch?v=PamhELVWYY0)
   - [RecyclerView Performance Tips](https://www.youtube.com/watch?v=hVJpWSalzbo)
   - [LiveData Overview](https://developer.android.com/topic/libraries/architecture/livedata)
   - [Room Database](https://developer.android.com/training/data-storage/room)

2. Firebase & Analytics:
   - [Firebase Android Codelab](https://firebase.google.com/codelabs/firebase-android)
   - [Firebase Analytics](https://firebase.google.com/docs/analytics/android/start)
   - Used for user authentication, cloud storage, and data synchronization

3. UI Components & Design:
   - [Material Components for Android](https://github.com/material-components/material-components-android)
   - [Android Asset Studio - Icon Generator](https://romannurik.github.io/AndroidAssetStudio/)
   - [Menu/User Panel Tutorial 1](https://www.youtube.com/watch?v=dJm7LACOn80)
   - [Menu/User Panel Tutorial 2](https://www.youtube.com/watch?v=k1RUOexThGs)

4. Image & Camera:
   - [Glide Image Loading Library](https://github.com/bumptech/glide)
   - [CameraX Overview](https://developer.android.com/training/camerax)
   - [ML Kit Text Recognition](https://developers.google.com/ml-kit/vision/text-recognition/android)

5. Location Services:
   - [Android Location API Tutorial](https://www.digitalocean.com/community/tutorials/android-location-api-tracking-gps)
   - Used for GPS location tracking and nearby store finding

6. Utilities & Data Processing:
   - [Java Concurrency in Android](https://developer.android.com/reference/java/util/concurrent/package-summary)
   - [Gson Library](https://github.com/google/gson)
   - [SearchRecentSuggestions](https://developer.android.com/reference/android/provider/SearchRecentSuggestions)

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Setup

1. Clone the repository
2. Open in Android Studio
3. Configure Firebase:
   - Create a Firebase project
   - Add your `google-services.json`
   - Enable Authentication and Firestore
4. Build and run

## Dependencies

- AndroidX
- Firebase Authentication
- Firebase Firestore
- Google Material Design
- Gson
- Glide for image loading

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/recipe_app/
│   │   │   ├── activities/
│   │   │   ├── adapters/
│   │   │   ├── models/
│   │   │   └── utils/
│   │   └── res/
│   │       ├── layout/
│   │       ├── values/
│   │       └── drawable/
│   └── test/
└── build.gradle
```

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 