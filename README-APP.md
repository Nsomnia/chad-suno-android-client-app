# ChadSuno

> "I use arch, btw." - The ultimate Chad's Suno music client

A superior third-party Suno Android app that actually slaps.

## Features

- **Full Library Management** - Browse, search, filter your entire song collection
- **Multi-Select Operations** - Bulk delete, move, and manage songs
- **Offline Caching** - Room database for fast local access
- **Music Generation** - Simple mode, Custom mode, Lyrics generation
- **Audio Tools** - Extend songs, generate stems, concatenate clips
- **Playback** - ExoPlayer-powered with lyrics sync and queue
- **Dark Theme** - Terminal/hacker aesthetic by default

## Tech Stack

- **Jetpack Compose** - Modern UI toolkit
- **Material 3** - Dark theme with neon green accents
- **Hilt** - Dependency injection
- **Room** - Local database
- **Retrofit** - API client
- **ExoPlayer** - Audio playback
- **Coroutines/Flow** - Reactive streams

## Building

1. Install Android SDK (minSdk 26, targetSdk 34)
2. Set `sdk.dir` in `local.properties`
3. Run `./gradlew assembleDebug`

## Authentication

Get your cookie from suno.com:
1. Open DevTools (F12)
2. Go to Network tab
3. Find a request with `?__clerk_api_version`
4. Copy the Cookie header value
5. Paste in Settings screen

## API Endpoints

Based on [suno-api](https://github.com/gcui-art/suno-api):

- `GET /api/get_limit` - Quota info
- `GET /api/get` - Song library
- `GET /api/clip` - Single song
- `GET /api/get_aligned_lyrics` - Timestamped lyrics
- `GET /api/persona` - Artist info
- `POST /api/generate` - Simple generation
- `POST /api/custom_generate` - Custom generation
- `POST /api/generate_lyrics` - Lyrics generation
- `POST /api/extend_audio` - Extend song
- `POST /api/generate_stems` - Stem separation
- `POST /api/concat` - Full song concatenation

## License

LGPL-3.0 or later

---

*"Row-Row Raggy!"*
