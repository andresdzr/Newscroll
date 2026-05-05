# Newscroll Project Notes

## Content Quality Review

### Current sources and known issues (reviewed 2026-05-04)

| Category | Source | Status | Notes |
|---|---|---|---|
| Technology, ML & AI | Hacker News (Algolia API) | Working | Good volume. Images are keyword-based Unsplash fallbacks (no actual article images). Descriptions are just "By author" — no summary text. |
| Finance & Investing | Finnhub general news | Working | Returns 100+ articles. Images usually included. Some articles may be low quality / listicle content. |
| Politics & Geopolitics | The Guardian API | Working | Good quality. Thumbnail images included. Requires valid API key. |
| Survival Knowledge | The Survival Mom (RSS) | Unverified | RSS may be unreachable from emulator. Fallback needed. |
| Survival Knowledge | Outdoor Life (RSS) | Unverified | RSS may be unreachable from emulator. |
| History & Philosophy | Stanford Encyclopedia of Philosophy (RSS) | Unverified | Academic tone, may lack images. |
| History & Philosophy | History Today (RSS) | Unverified | Check availability from emulator. |

### Improvements to consider

- **HN summaries**: Currently shows "By [author]". Could fetch the linked article's meta description for a real summary.
- **Images**: Unsplash `source.unsplash.com` is deprecated — replace with a proper keyword-image API or use article OG images.
- **RSS fallbacks**: If survival/history RSS feeds are unreachable, add alternative sources (e.g. Wikipedia featured articles RSS for history).
- **Finnhub volume**: 100+ finance articles per load — consider capping at ~20 per source to balance the feed.
- **Content shuffle**: Articles from all selected categories are currently shuffled together. Consider interleaving proportionally by source.

## Architecture Notes

- Filter state lives in `SettingsViewModel` (SharedPreferences backed).
- `LaunchedEffect(selectedFilters)` in `NewsAppShell` re-fetches whenever filters change.
- `NewsRepository.fetchArticles` runs entirely on `Dispatchers.IO`.
- Filter button on feed opens `CategoryFilterSheet` bottom sheet — chip selection immediately updates `selectedFilters`, which triggers re-fetch via the LaunchedEffect.
