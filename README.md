# Collection de Cartes

A personal language-learning desktop app. Add words and phrases in a target
language, review them as flashcards with spaced repetition, and browse
everything in a flippable dictionary view.

## Features

- **Multi-language decks** — English, French, Spanish, German, each backed
  by its own SQLite database (`databases/*.db`).
- **Flashcard study sessions** — multiple-choice review, graded via an
  SM-2-based scheduler (`Sm2Scheduler`) with latency-aware answer grading
  (`LatencyGrader`) so accidental clicks aren't scored as real answers.
- **Dictionary view** — every word/phrase in the deck as a grid of flippable
  cards.
- **Streak tracking** on the title bar.
- **Themeable UI** — `ThemeManager` swaps in a full stylesheet set
  (`base.css`, `layout.css`, `components.css`, `flashcards.css`) per
  `Theme`, layered under a `workshop.css` staging sheet. *[WIP!]*

## Tech stack

- Java 25
- JavaFX 21.0.6
- Gradle (Kotlin DSL) with the `org.beryx.jlink` plugin for native image builds
- `sqlite-jdbc` for persistence

## Running it

```bash
./gradlew run
```

On Windows, use `gradlew.bat run` instead.

## Project structure

```
src/main/java/xyz/ryansbeanfactory/collectiondecartes/
├── CarteApplication.java        JavaFX Application, font/DB bootstrap
├── Launcher.java                 actual main-class entry point
├── SceneManager.java              swaps LandingPage / CrossRoads roots
├── database/                     DatabaseManager + per-entity repositories
├── model/                        Word, Phrase, CardSrs, ReviewLogEntry, refs/
├── session/                      AppSession, DeckLanguage
├── srs/                          StudySession, Sm2Scheduler, LatencyGrader
├── theme/                        Theme, ThemeManager
└── ui/
    ├── LandingPage.java           language picker
    ├── components/                 shared widgets (Card, LargeButton, forms)
    ├── crossroads/                  main app shell (title bar, nav, pages)
    └── util/                       Dialogs

src/main/resources/xyz/ryansbeanfactory/collectiondecartes/
├── fonts/                         Manrope (Regular/Medium/SemiBold/Bold)
└── styles/
    ├── default/                    base.css, layout.css, components.css,
    │                                flashcards.css — theme stylesheets
    └── workshop.css                staging sheet, always loaded last
```

## Status

Active development; no releases yet.
