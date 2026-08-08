# Quiz App

A modern, interactive Android Quiz application built with Kotlin, Jetpack Compose, and Hilt.

## 🚀 Features & Implementation

### 1. Modern Architecture
*   **MVVM Pattern**: Clean separation of concerns between UI, ViewModel, and Data layers.
*   **Dependency Injection**: Fully integrated with **Hilt** for robust and testable code.
*   **Navigation**: Uses **Jetpack Compose Navigation** for smooth transitions between the Quiz and Results screens.

### 2. Core Quiz Logic
*   **Dynamic Questions**: Fetches quiz data via **Retrofit** from a remote source.
*   **Interactive Feedback**: 
    *   Immediate revelation of correct (Green) and incorrect (Red) answers upon selection.
    *   Selection locking once a question is answered.
*   **Auto-Advance**: Smart 2-second timer to move to the next question after answering, managed within the ViewModel.
*   **Skip Functionality**: Allows users to bypass difficult questions immediately.
*   **Full Navigation Control**: Supports going back to previous questions.

### 3. Gamification & Engagement
*   **Streak Tracking**: Tracks consecutive correct answers.
*   **Animated Streak Badge**: A pulsing "ON FIRE!" badge that activates when the user hits a streak of 3 or more.
*   **Longest Streak**: Tracks and displays the best streak achieved during the session on the results screen.

### 4. UI & UX Design
*   **Material 3**: Built with the latest Material Design components.
*   **Responsive Layouts**: Fully scrollable screens to support both **Portrait and Landscape** modes.
*   **Splash Screen**: Integrated `androidx-core-splashscreen` with a custom branded logo for a professional first impression.
*   **Theming**: Centralized color palette and typography system matching the "Quiz app" branding.
*   **Localization Ready**: All hardcoded strings extracted to `strings.xml`.

### 5. Build & Configuration
*   **Build Variants**: Supports `stage` and `prod` environments with different package names and app titles.
*   **Externalized Config**: `BASE_URL` managed via Gradle `BuildConfig` for easy environment switching.
*   **ProGuard/R8 Ready**: Optimized release builds with custom rules to prevent crashes due to obfuscation.

## 🛠 Tech Stack
*   **Language**: Kotlin
*   **UI Framework**: Jetpack Compose
*   **Networking**: Retrofit + OkHttp + GSON
*   **DI**: Hilt
*   **Processing**: KSP
*   **Lifecycle**: ViewModel, StateFlow, collectAsStateWithLifecycle
*   **Debugging**: Chucker Interceptor for network inspection.

## 🏁 Getting Started
1. Open the project in Android Studio (Koala or newer recommended).
2. Select the `prodDebug` or `stageDebug` build variant.
3. Sync Gradle and Run on an emulator or physical device.
