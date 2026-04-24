# CMP-Bookpedia - Kotlin Multiplatform Book Search Application

## 📖 Project Overview

**CMP-Bookpedia** is a production-ready Kotlin Multiplatform (KMP) application that demonstrates modern cross-platform mobile development. The app provides a comprehensive book search experience, allowing users to discover books through a remote API, view detailed information, and manage their favorite books locally across Android, iOS, and Desktop platforms.

Built with **Compose Multiplatform** for shared UI and following **Clean Architecture** principles, this project showcases best practices in multiplatform development with clear separation of concerns, type-safe navigation, and robust error handling.

---

## ✨ Features

### Core Functionality
- **📚 Book Search**: Real-time book search with 500ms debouncing to optimize API calls
- **❤️ Favorites Management**: Mark books as favorites and store them locally using Room database
- **📖 Book Details**: View comprehensive book information including:
  - Cover images
  - Author information
  - Publication year
  - Average ratings and ratings count
  - Number of pages and editions
  - Languages available
  - Full descriptions
- **🔄 Dual View Modes**: Toggle between search results and favorite books with tab navigation
- **⚡ Offline Support**: Access favorite books without network connectivity

### User Experience
- Clean, modern Material 3 UI design
- Responsive layouts optimized for each platform
- Loading states and error handling with user-friendly messages
- Smooth navigation with type-safe routing
- Image loading with Coil 3 for optimal performance

## 🏗️ Architecture

### Clean Architecture Implementation

The project follows **Clean Architecture** with a **feature-first** structure, ensuring maintainability and testability:

```
book/
├── data/                    # Data Layer
│   ├── database/            # Room entities, DAOs, and database setup
│   ├── dto/                 # Network DTOs (Data Transfer Objects)
│   ├── mappers/             # Entity/DTO to Domain model mappers
│   ├── network/             # Ktor API clients and remote data sources
│   └── repository/          # Repository implementations
├── domain/                  # Domain Layer
│   ├── Book.kt              # Core domain model
│   └── BookRepository.kt    # Repository interface
└── presentation/            # Presentation Layer
    ├── book_list/           # Book list feature (ViewModel, State, UI)
    └── book_detail/         # Book detail feature (ViewModel, State, UI)
```

### Layer Responsibilities

**Domain Layer** (`domain/`)
- Pure Kotlin business logic
- Domain models (e.g., `Book`)
- Repository interfaces
- No dependencies on frameworks or external libraries

**Data Layer** (`data/`)
- Repository implementations
- Remote data sources (Ktor)
- Local data sources (Room)
- DTOs and entity mappers
- Handles data fetching, caching, and persistence

**Presentation Layer** (`presentation/`)
- ViewModels with MVI-style state management
- Compose UI screens and components
- User interaction handling
- Navigation logic

---

## 🛠️ Tech Stack

### Core Technologies
- **Kotlin 2.0.21** - Primary programming language
- **Kotlin Multiplatform** - Code sharing across platforms
- **Compose Multiplatform 1.7.0** - Declarative UI framework

### Networking & Serialization
- **Ktor 3.0.0** - HTTP client with platform-specific engines
  - OkHttp for Android & Desktop
  - Darwin for iOS
- **Kotlinx Serialization 1.7.3** - JSON serialization

### Dependency Injection
- **Koin 4.0.0** - Lightweight DI framework
  - `expect/actual` pattern for platform-specific dependencies
  - Shared module for common dependencies

### Local Persistence
- **Room 2.7.0-alpha11** - Local database with multiplatform support
- **SQLite Bundled 2.5.0-alpha11** - Embedded SQLite driver
- **KSP 2.0.20-1.0.24** - Kotlin Symbol Processing for Room

### UI & Image Loading
- **Material 3** - Modern Material Design components
- **Coil 3 (3.0.0-rc02)** - Asynchronous image loading with Ktor integration

### Architecture Components
- **Jetpack Compose Navigation 2.8.0-alpha10** - Type-safe navigation
- **AndroidX Lifecycle 2.8.3** - ViewModel and lifecycle management
- **Kotlinx Coroutines 1.9.0** - Asynchronous programming

---

## 📱 Platform Support

### Android
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 35 (Android 15)
- **Compile SDK**: 35
- **Package**: `com.plcoding.bookpedia`
- **Engine**: Ktor OkHttp client
- **Entry Point**: `BookApplication.kt`

### iOS
- **Targets**: `iosX64`, `iosArm64`, `iosSimulatorArm64`
- **Framework**: Static framework (`ComposeApp`)
- **Engine**: Ktor Darwin client
- **Entry Point**: `MainViewController.kt`

### Desktop (JVM)
- **Target**: JVM Desktop
- **Main Class**: `com.plcoding.bookpedia.MainKt`
- **Engine**: Ktor OkHttp client
- **Distribution Formats**: DMG (macOS), MSI (Windows), DEB (Linux)

---

## 📂 Project Structure

### Shared Code (`composeApp/src/commonMain/`)
```
commonMain/
├── kotlin/com/plcoding/bookpedia/
│   ├── app/                 # Application setup
│   │   └── Route.kt         # Type-safe navigation routes
│   ├── book/                # Book feature module
│   │   ├── data/            # Data layer implementation
│   │   ├── domain/          # Domain models and interfaces
│   │   └── presentation/    # ViewModels and UI
│   ├── core/                # Core utilities
│   │   ├── data/            # HTTP client factory
│   │   ├── domain/          # Result type, error handling
│   │   └── presentation/    # UI utilities
│   └── di/                  # Dependency injection modules
└── composeResources/        # Shared resources (images, strings)
```

### Platform-Specific Code
- **`androidMain/`**: Android-specific implementations (Application, MainActivity, DI)
- **`iosMain/`**: iOS-specific implementations (MainViewController, DI)
- **`desktopMain/`**: Desktop-specific implementations (main function, DI)
- **`nativeMain/`**: Shared iOS code (using `expect/actual` pattern)

---

## 🔑 Key Implementation Details

### Dependency Injection with Koin

**Shared Module** (`di/Modules.kt`)
```kotlin
val sharedModule = module {
    single { HttpClientFactory.create(get()) }
    singleOf(::KtorRemoteBookDataSource).bind<RemoteBookDataSource>()
    singleOf(::DefaultBookRepository).bind<BookRepository>()
    single { get<DatabaseFactory>().create().setDriver(BundledSQLiteDriver()).build() }
    single { get<FavoriteBookDatabase>().dao }
    viewModelOf(::BookListViewModel)
    viewModelOf(::BookDetailViewModel)
    viewModelOf(::SelectedBookViewModel)
}
```

**Platform Module** (using `expect/actual`)
- Each platform provides `DatabaseFactory` implementation
- Android: Uses `Context` for database creation
- iOS/Desktop: Platform-specific database paths

### Type-Safe Navigation

Routes are defined using Kotlin serialization:
```kotlin
sealed interface Route {
    @Serializable data object BookGraph: Route
    @Serializable data object BookList: Route
    @Serializable data class BookDetail(val id: String): Route
}
```

### State Management (MVI Pattern)

**State**: Immutable data classes exposed as `StateFlow`
```kotlin
data class BookListState(
    val searchQuery: String = "Kotlin",
    val searchResults: List<Book> = emptyList(),
    val favoriteBooks: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val selectedTabIndex: Int = 0,
    val errorMessage: UiText? = null
)
```

**Actions**: User interactions
```kotlin
sealed interface BookListAction {
    data class OnSearchQueryChange(val query: String) : BookListAction
    data class OnBookClick(val book: Book) : BookListAction
    data class OnTabSelected(val index: Int) : BookListAction
}
```

**Events**: One-time side effects
```kotlin
sealed interface BookListEvent {
    data class RedirectToBookDetails(val book: Book) : BookListEvent
}
```

### Error Handling with Result Type

Custom `Result<D, E>` type for type-safe error handling:
```kotlin
sealed interface Result<out D, out E> {
    data class Success<out D>(val data: D) : Result<D, Nothing>
    data class Error<out E>(val error: E) : Result<Nothing, E>
}
```

Error types:
- `DataError.Remote`: Network errors (NO_INTERNET, SERVER_ERROR, etc.)
- `DataError.Local`: Database errors (DISK_FULL, etc.)

Extension functions for handling results:
```kotlin
result
    .onSuccess { data -> /* handle success */ }
    .onError { error -> /* handle error */ }
```

### Search Debouncing

Search queries are debounced by 500ms to reduce API calls:
```kotlin
state
    .map { it.searchQuery }
    .distinctUntilChanged()
    .debounce(500L)
    .onEach { query -> searchBooks(query) }
    .launchIn(viewModelScope)
```

### Database Schema Management

Room schemas are versioned in `composeApp/schemas/`:
```kotlin
@Database(entities = [BookEntity::class], version = 1)
@TypeConverters(StringListTypeConverter::class)
@ConstructedBy(BookDatabaseConstructor::class)
abstract class FavoriteBookDatabase : RoomDatabase() {
    abstract val dao: FavoriteBookDao
    companion object {
        const val DB_NAME = "favorite_books.db"
    }
}
```

---

## 🚀 Build & Run

### Prerequisites
- JDK 11 or higher
- Android Studio (for Android development)
- Xcode (for iOS development, macOS only)

### Android
```bash
# Build debug APK
./gradlew :composeApp:assembleDebug

# Install on connected device
./gradlew :composeApp:installDebug

# Run directly
./gradlew :composeApp:runDebug
```

### iOS
```bash
# Open in Xcode
open iosApp/iosApp.xcodeproj

# Or run tests
./gradlew :composeApp:iosSimulatorArm64Test
```

### Desktop
```bash
# Run application
./gradlew :composeApp:run

# Create distribution packages
./gradlew :composeApp:packageDistributionForCurrentOS
```

### Clean Build
```bash
./gradlew clean
./gradlew build
```

### Room Schema Generation
After modifying database entities:
```bash
./gradlew :composeApp:kspCommonMainKotlinMetadata
```

---

## 📦 Dependencies

### Build Tools
- **Android Gradle Plugin**: 8.5.2
- **Kotlin**: 2.0.21
- **KSP**: 2.0.20-1.0.24
- **Compose Plugin**: 1.7.0

### Core Libraries
| Library | Version | Purpose |
|---------|---------|---------|
| Ktor Client | 3.0.0 | HTTP networking |
| Koin | 4.0.0 | Dependency injection |
| Room | 2.7.0-alpha11 | Local database |
| Coil 3 | 3.0.0-rc02 | Image loading |
| Kotlinx Serialization | 1.7.3 | JSON serialization |
| Compose Navigation | 2.8.0-alpha10 | Type-safe navigation |
| AndroidX Lifecycle | 2.8.3 | ViewModel & lifecycle |
| Kotlinx Coroutines | 1.9.0 | Async operations |

### Ktor Bundle
- `ktor-client-core`
- `ktor-client-content-negotiation`
- `ktor-client-auth`
- `ktor-client-logging`
- `ktor-serialization-kotlinx-json`

### Coil Bundle
- `coil-compose`
- `coil-compose-core`
- `coil-network-ktor2`
- `coil-network-ktor3`
- `coil-mp`

---

## 🎯 Key Highlights

✅ **100% Shared Business Logic**: All ViewModels, repositories, and domain logic shared across platforms
✅ **Type-Safe Navigation**: Compile-time safety with Kotlin serialization
✅ **Modern Architecture**: Clean Architecture with clear separation of concerns
✅ **Reactive State Management**: MVI pattern with StateFlow and SharedFlow
✅ **Robust Error Handling**: Custom Result type with typed errors
✅ **Offline-First**: Local database with Room for favorites
✅ **Performance Optimized**: Search debouncing, image caching, efficient state updates
✅ **Production Ready**: Comprehensive error handling, loading states, and user feedback

---

## 📝 Version

- **Version Code**: 1
- **Version Name**: 1.0
- **Package Version**: 1.0.0

---

## 🙏 Acknowledgments

This project demonstrates modern Kotlin Multiplatform development patterns and serves as a reference implementation for building production-grade cross-platform applications with Compose Multiplatform.

---

## 📱 Video
<video height="500" src="https://github.com/user-attachments/assets/b704921a-b947-427d-b061-f508e54dddca" />

---
