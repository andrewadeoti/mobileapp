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

## Code Attribution

This project includes code adapted from the following sources:

1. Location tracking implementation adapted from:
   - [Android Location API Tutorial](https://www.digitalocean.com/community/tutorials/android-location-api-tracking-gps) by DigitalOcean

2. Firebase integration patterns adapted from:
   - [Firebase Android Codelab](https://firebase.google.com/codelabs/firebase-android)

3. RecyclerView implementations inspired by:
   - [Android Developers Guide](https://developer.android.com/guide/topics/ui/layout/recyclerview)

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