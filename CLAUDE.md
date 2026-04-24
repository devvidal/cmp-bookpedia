# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

CMP-Bookpedia is a Kotlin Multiplatform (KMP) book search application targeting Android, iOS, and Desktop (JVM). It uses Compose Multiplatform for shared UI and follows Clean Architecture principles with clear separation between data, domain, and presentation layers.

The app searches for books via a remote API and allows users to favorite books, storing them locally with Room database.

## Build & Run Commands

### Android
```bash
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:installDebug
```

### Desktop
```bash
./gradlew :composeApp:run
```

### iOS
Open `iosApp.xcodeproj` in Xcode and run, or use:
```bash
./gradlew :composeApp:iosSimulatorArm64Test
```

### Clean Build
```bash
./gradlew clean
./gradlew build
```

### Room Schema Generation
Room schemas are stored in `composeApp/schemas/`. After modifying database entities, run:
```bash
./gradlew :composeApp:kspCommonMainKotlinMetadata
```

## Architecture

### Clean Architecture Pattern

The codebase follows a feature-first structure with Clean Architecture:

```
book/
├── data/           # Data layer (repositories, data sources, DTOs, database)
│   ├── database/   # Room database entities and DAOs
│   ├── dto/        # Network response DTOs
│   ├── mappers/    # Entity/DTO to Domain mappers
│   ├── network/    # Ktor remote data sources
│   └── repository/ # Repository implementations
├── domain/         # Domain layer (business logic, models, repository interfaces)
└── presentation/   # Presentation layer (ViewModels, UI state, Compose screens)
    ├── book_list/
    └── book_detail/
```

Each feature (currently just `book`) is self-contained with its own data, domain, and presentation layers.

### Shared vs Platform-Specific Code

- **`commonMain/`**: Shared Kotlin code for all platforms (domain, data, presentation logic)
- **`androidMain/`**: Android-specific implementations (Application, MainActivity, DI)
- **`iosMain/`**: iOS-specific implementations (MainViewController, DI)
- **`desktopMain/`**: Desktop-specific implementations (main function, DI)
- **`nativeMain/`**: Shared iOS code (if needed)

Platform modules provide implementations via Koin's `expect/actual` pattern (see `platformModule` in `di/Modules.kt`).

### Dependency Injection (Koin)

All dependencies are managed via Koin:
- **`sharedModule`** (`di/Modules.kt`): Common dependencies (HttpClient, repositories, ViewModels)
- **`platformModule`**: Platform-specific dependencies (DatabaseFactory) - defined separately in each platform's `di/` folder using `expect/actual`

Initialize Koin in platform-specific entry points:
- Android: `BookApplication.kt`
- iOS: `MainViewController.kt`
- Desktop: `main.kt`

### Navigation

Uses Jetpack Compose Navigation with type-safe routing:
- Routes defined in `app/Route.kt` as Kotlin serializable objects
- Nested navigation graph (`Route.BookGraph` → `Route.BookList`, `Route.BookDetail`)
- Shared ViewModel (`SelectedBookViewModel`) scoped to navigation graph for passing selected book between screens

### Result Type Pattern

All repository operations return a custom `Result<D, E>` type (see `core/domain/Result.kt`):
- `Result.Success<D>`: Successful operation with data
- `Result.Error<E>`: Failed operation with typed error

Use extension functions `onSuccess`, `onError`, and `map` for handling results.

Error types:
- `DataError.Remote`: Network/API errors
- `DataError.Local`: Database errors

### State Management

ViewModels use MVI-style patterns:
- **State**: Immutable data class exposed as `StateFlow` (e.g., `BookListState`)
- **Actions**: User interactions (e.g., `BookListAction.OnSearchQueryChange`)
- **Events**: One-time side effects (e.g., `BookListEvent.RedirectToBookDetails`) exposed as `SharedFlow`

Example:
```kotlin
viewModel.onAction(BookListAction.OnBookClick(book))
```

## Key Technologies

- **Compose Multiplatform**: UI framework
- **Kotlin Coroutines & Flow**: Async operations and reactive state
- **Ktor**: HTTP client with platform engines (OkHttp for Android/Desktop, Darwin for iOS)
- **Kotlinx Serialization**: JSON serialization
- **Room**: Local database (with KSP for code generation)
- **Koin**: Dependency injection
- **Coil 3**: Image loading

## Platform-Specific Notes

### Android
- Package name: `com.plcoding.bookpedia`
- Min SDK: 24, Target SDK: 35
- Compose previews available via `@Preview` annotations (see `Previews.kt`)

### iOS
- Framework name: `ComposeApp`
- Targets: `iosX64`, `iosArm64`, `iosSimulatorArm64`
- Framework is static (`isStatic = true`)

### Desktop
- Main class: `com.plcoding.bookpedia.MainKt`
- Distribution formats: DMG (macOS), MSI (Windows), DEB (Linux)

## Important Implementation Details

### HTTP Client Configuration
Each platform provides its engine in `platformModule`. Common configuration (JSON serialization, logging, auth) is in `HttpClientFactory.create()`.

### Database Migrations
Room schemas are versioned in `composeApp/schemas/`. When modifying entities:
1. Update the entity
2. Increment database version in `FavoriteBookDatabase`
3. Provide migration strategy
4. Run KSP to regenerate schema files

### Image Loading
Coil 3 is configured with Ktor network layer. Use `AsyncImage` composable for loading images from URLs.

### Search Debouncing
Book search queries are debounced (500ms) to avoid excessive API calls. See `BookListViewModel.observeSearchQuery()` for implementation.