# Quiz App

A modern, interactive Android Quiz application built with Kotlin, Jetpack Compose, Hilt, and Room.

## 🚀 Features & Implementation

### 1. Modern Architecture
*   **MVVM Pattern**: Clean separation of concerns between UI, ViewModel, and Data layers.
*   **Dependency Injection**: Fully integrated with **Hilt** for robust and testable code.
*   **Navigation**: Uses **Jetpack Compose Navigation** for smooth transitions between the Quiz and Results screens.

### 2. Data Persistence & Resuming
*   **Local Storage**: Integrated **Room Database** to persist quiz categories, question progress, scores, and streaks.
*   **Quiz Resuming**: Smart logic that automatically brings users back to their **last attempted question** for in-progress quizzes.
*   **Review Mode**: If a quiz has already been completed, opening it enters "Review Mode," starting from the first question.
*   **Offline Support**: Once fetched, quiz data is cached locally for instant access.

### 3. Core Quiz Logic
*   **Dynamic Questions**: Fetches quiz data via **Retrofit** from a remote source and syncs with the local database.
*   **Interactive Feedback**: 
    *   Immediate revelation of correct (Green) and incorrect (Red) answers upon selection.
    *   Selection locking once a question is answered.
*   **Auto-Advance**: Smart 2-second timer to move to the next question after answering, managed within the ViewModel.
*   **Skip Functionality**: Allows users to bypass difficult questions immediately.
*   **Full Navigation Control**: Supports going back to previous questions.

### 4. Gamification & Engagement
*   **Streak Tracking**: Tracks consecutive correct answers in real-time.
*   **Animated Streak Badge**: A pulsing "ON FIRE!" badge that activates when the user hits a streak of 3 or more.
*   **Longest Streak**: Tracks and displays the best streak achieved during the session on the results screen.

### 5. UI & UX Design
*   **Material 3**: Built with the latest Material Design components.
*   **Landscape Optimization**: Custom handling for **display cutouts and safe insets**, ensuring UI elements aren't obscured by camera notches in landscape mode.
*   **Responsive Layouts**: Fully scrollable screens to support various screen sizes.
*   **Splash Screen**: Integrated `androidx-core-splashscreen` for a professional first impression.
*   **Theming**: Centralized color palette and typography system matching the "Quiz app" branding.

### 6. Build & Configuration
*   **Build Variants**: Supports `stage` and `prod` environments with different package names and app titles.
*   **Externalized Config**: `BASE_URL` managed via Gradle `BuildConfig` for easy environment switching.
*   **R8/ProGuard Optimized**: Custom rules ensuring stability in minified release builds, specifically for GSON and Retrofit generic type preservation.

## 🛠 Tech Stack
*   **Language**: Kotlin
*   **UI Framework**: Jetpack Compose
*   **Database**: Room
*   **Networking**: Retrofit + OkHttp + GSON
*   **DI**: Hilt
*   **Processing**: KSP
*   **Lifecycle**: ViewModel, StateFlow, collectAsStateWithLifecycle
*   **Debugging**: Chucker Interceptor for network inspection.

## 🏁 Getting Started
1. Open the project in Android Studio (Koala or newer recommended).
2. Select the `prodDebug` or `stageDebug` build variant.
3. Sync Gradle and Run on an emulator or physical device.
