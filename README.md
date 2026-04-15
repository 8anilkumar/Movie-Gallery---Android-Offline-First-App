# Movie Gallery - Android Offline-First App 🎬

A modern, production-ready Android application showcasing an offline-first architecture with real-time Firebase synchronization. Built with Kotlin and Jetpack Compose following MVVM and Repository patterns.

## Features ✨

- **Offline-First Architecture**: Local Room database persistence with Firebase Realtime Database cloud sync
- **Real-Time Synchronization**: Bidirectional data sync between local database and Firebase
- **Modern UI**: Built with Jetpack Compose and Material3 design system
- **Smart Connectivity**: Lifecycle-aware internet status monitoring with contextual notifications
- **Dynamic UI Elements**: 
  - Conditional like button colors (grey when 0, red when > 0)
  - Release date overlays on movie thumbnails
  - Delete/Like/Share action buttons
- **MVVM Architecture**: Clean separation of concerns with ViewModel and LiveData/Flow
- **Dependency Injection**: Hilt for automatic dependency management
- **Asynchronous Operations**: Kotlin Coroutines and Flow for non-blocking operations

## Tech Stack 🛠️

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose + Material3
- **Database**: Room (Local) + Firebase Realtime Database (Cloud)
- **Architecture**: MVVM + Repository Pattern
- **Dependency Injection**: Hilt
- **Async**: Kotlin Coroutines & Flow
- **Navigation**: Jetpack Navigation Compose
- **Image Loading**: Coil
- **Build System**: Gradle with Kotlin DSL

## Project Structure 📁

```
app/src/main/java/com/ebixcash/offlinefirstapp/
├── data/
│   ├── local/              # Room database & DAOs
│   │   ├── MovieDatabase.kt
│   │   └── MovieDao.kt
│   ├── model/              # Data classes
│   │   └── Movie.kt
│   ├── remote/             # Firebase operations
│   │   └── FirebaseMovieDataSource.kt
│   └── repository/         # Repository pattern
│       └── MovieRepository.kt
├── di/                     # Hilt dependency injection
│   └── DataModule.kt
├── presentation/
│   ├── ui/
│   │   ├── components/     # Reusable UI components
│   │   │   └── MovieCardNew.kt
│   │   ├── screens/        # Full screens
│   │   │   ├── MovieListScreen.kt
│   │   │   └── AddMovieScreen.kt
│   │   └── navigation/
│   │       └── Navigation.kt
│   └── viewmodel/          # ViewModels
│       ├── MovieListViewModel.kt
│       └── AddMovieViewModel.kt
├── utils/                  # Utility classes
│   └── NetworkConnectivityObserver.kt
└── MainActivity.kt
```

## Key Components 🔧

### Movie Data Model
```kotlin
@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey val id: String,
    var name: String,
    var thumbnail: String,
    var releaseDate: String,
    var rating: Double,
    var description: String,
    var likes: Int = 0,
    var syncedWithFirebase: Boolean = false,
    var lastModified: Long = System.currentTimeMillis()
)
```

### Repository Pattern
- Fetches from Firebase
- Caches in Room database
- Syncs unsynced movies automatically
- Provides single source of truth via Flow

### Connectivity Management
- Tracks app lifecycle (Foreground/Background)
- Shows internet status notifications only when app is active
- Supports both connection lost and connection resumed events

## Getting Started 🚀

### Prerequisites
- Android Studio Flamingo or later
- Android SDK 34+
- JDK 17+
- Firebase account with Realtime Database

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/8anilkumar/Movie-Gallery---Android-Offline-First-App.git
   cd Movie-Gallery---Android-Offline-First-App
   ```

2. **Set up Firebase**
   - Download `google-services.json` from Firebase Console
   - Place it in `app/` directory
   - Update Firebase Realtime Database Rules:
     ```json
     {
       "rules": {
         "movies": {
           ".read": true,
           ".write": true
         }
       }
     }
     ```

3. **Configure Java Home (Windows)**
   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
   ```

4. **Build and Run**
   ```bash
   ./gradlew clean build
   ./gradlew installDebug
   ```

## Usage 📱

### Add Movies
- Tap the **+** button to open Add Movie screen
- Enter movie details and save
- Movie syncs to Firebase automatically

### Manage Movies
- **Like**: Tap the heart icon (turns red after first like)
- **Share**: Tap the share icon
- **Delete**: Tap the delete icon (right end)

### Network Features
- **Offline**: Continue using app with locally cached movies
- **Internet Lost**: Snackbar notification appears
- **Internet Resumed**: Confirmation snackbar appears
- Notifications only show when app is in foreground

## API Integration 🔗

### Firebase Realtime Database Structure
```json
{
  "movies": {
    "movie_1": {
      "id": "movie_1",
      "name": "Inception",
      "thumbnail": "https://...",
      "releaseDate": "2010-07-16",
      "rating": 8.8,
      "description": "...",
      "likes": 0,
      "syncedWithFirebase": true
    }
  }
}
```

## Architecture Highlights 🏗️

### Offline-First Flow
1. **Fetch from Firebase** → Flow starts emitting data
2. **Save to Room** → Local cache updated
3. **Emit to UI** → Display updates immediately
4. **Network Changes** → Automatic re-sync

### Data Flow
```
Firebase → FirebaseDataSource → Repository → ViewModel → UI (Compose)
                ↓                                           ↓
              Room (Cache) ←← ← ← ← ← ← ← ← ← ← ← ← ← ← ← Sync
```

## Performance Considerations ⚡

- **Flow-based**: Reactive updates without blocking
- **Coroutines**: Non-blocking async operations
- **Room Cache**: Instant local access
- **Lifecycle Aware**: Only observes when needed

## Testing 🧪

The app follows testable architecture principles:
- Repository pattern enables easy mocking
- ViewModel can be tested independently
- Flow operations can be tested with test collectors

## Future Enhancements 🔮

- [ ] Unit & Integration tests
- [ ] User authentication (Firebase Auth)
- [ ] Movie search & filter functionality
- [ ] Rating aggregation and reviews
- [ ] Pagination for large datasets
- [ ] Image caching optimization
- [ ] Dark mode support

## Troubleshooting 🔧

### Firebase data not showing
- Check Firebase Security Rules (allow `.read: true`)
- Verify `google-services.json` is in `app/` directory
- Check network connectivity
- Review logcat for error messages

### Gradle build fails
- Ensure JAVA_HOME is set correctly
- Clear gradle cache: `./gradlew clean`
- Check AGP compatibility (8.2.2+)

### Snackbars not appearing
- Ensure app is in foreground (not backgrounded)
- Check internet connectivity changes
- Verify SnackbarHost is in Compose hierarchy

## Contributing 🤝

Contributions are welcome! Please feel free to submit a Pull Request.

## License 📄

This project is open source and available under the MIT License.

## Author 👨‍💻

**Anil Kumar** - [GitHub](https://github.com/8anilkumar)

## Acknowledgments 🙏

- Android Jetpack documentation
- Firebase best practices
- Kotlin coroutines guides
- Material Design 3 guidelines

---

**⭐ If you find this project helpful, please consider giving it a star!**
